package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.product.AttributeValuePair;
import duc.project.ecommercejv.dto.product.ProductSkuRequest;
import duc.project.ecommercejv.dto.product.ProductSkuResponse;
import duc.project.ecommercejv.entity.product.*;
import duc.project.ecommercejv.exception.BadRequestException;
import duc.project.ecommercejv.exception.NotFoundException;
import duc.project.ecommercejv.repository.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSkuService {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductAttributeRepository attributeRepository;
    private final ProductAttributeValueRepository attributeValueRepository;
    private final PriceRepository priceRepository;
    private final ProductPriceRepository productPriceRepository;

    @Transactional
    public ProductSkuResponse create(Long productId, ProductSkuRequest req) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Cannot find product"));

        if (productSkuRepository.findBySkuCode(req.getSkuCode()).isPresent()) {
            throw new BadRequestException("SKU's code already existed: " + req.getSkuCode());
        }

        Set<ProductAttributeValue> attributeValues = resolveAttributeValues(product, req.getAttributes());
        validateNoDuplicateCombination(productId, attributeValues, null);

        ProductSku sku = new ProductSku();
        sku.setProduct(product);
        sku.setSkuCode(req.getSkuCode());
        sku.setStock(req.getStock());
        sku.setAttributeValues(attributeValues);
        sku = productSkuRepository.save(sku);

        if (req.getPrice() != null) {
            addPrice(sku, req.getPrice());
        }

        return toResponse(sku);
    }

    @Transactional
    public ProductSkuResponse update(Long skuId, ProductSkuRequest req) {
        ProductSku sku = productSkuRepository.findById(skuId).orElseThrow(() -> new NotFoundException("Cannot find SKU"));
        productSkuRepository.findBySkuCode(req.getSkuCode()).ifPresent(existing -> {
            if (!existing.getId().equals(skuId)) {
                throw new BadRequestException("SKU code already existed: " + req.getSkuCode());
            }
        });

        sku.setSkuCode(req.getSkuCode());
        sku.setStock(req.getStock());

        if (req.getAttributes() != null) {
            Set<ProductAttributeValue> attributeValues = resolveAttributeValues(sku.getProduct(), req.getAttributes());
            validateNoDuplicateCombination(sku.getProduct().getId(), attributeValues, skuId);
            sku.setAttributeValues(attributeValues);
        }
        productSkuRepository.save(sku);

        if (req.getPrice() != null) {
            addPrice(sku, req.getPrice());
        }
        return toResponse(sku);
    }

    @Transactional
    public void delete(Long skuId) {
        ProductSku sku = productSkuRepository.findById(skuId).orElseThrow(() -> new NotFoundException("Cannot find SKU"));
        sku.setDeletedAt(LocalDateTime.now());
        productSkuRepository.save(sku);
    }

    @Transactional(readOnly = true)
    public List<ProductSkuResponse> listByProduct(Long productId) {
        return productSkuRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

//    =========HELPER=========
    private Set<ProductAttributeValue> resolveAttributeValues(Product product, List<AttributeValuePair> pairs) {
        if (pairs == null || pairs.isEmpty()) {
            return new HashSet<>();
        }

        Set<ProductAttributeValue> result = new HashSet<>();
        for (AttributeValuePair pair : pairs) {
            ProductAttribute attribute = attributeRepository.findByProductIdAndName(product.getId(), pair.getName())
                    .orElseGet(() -> {
                        ProductAttribute a = new ProductAttribute();
                        a.setProduct(product);
                        a.setName(pair.getName());
                        return attributeRepository.save(a);
                    });

            ProductAttributeValue value = attributeValueRepository.findByProductAttributeIdAndValue(attribute.getId(), pair.getValue())
                    .orElseGet(() -> {
                        ProductAttributeValue v = new ProductAttributeValue();
                        v.setProductAttribute(attribute);
                        v.setValue(pair.getValue());
                        return attributeValueRepository.save(v);
                    });

            result.add(value);
        }
        return result;
    }

    private void validateNoDuplicateCombination(Long productId, Set<ProductAttributeValue> values, Long excludeSkuId) {
        Set<Long> newIds = values.stream().map(ProductAttributeValue::getId).collect(Collectors.toSet());
        List<ProductSku> existingSkus = productSkuRepository.findByProductId(productId);
        for (ProductSku existing : existingSkus) {
            if (excludeSkuId != null && existing.getId().equals(excludeSkuId)) {
                continue;
            }
            Set<Long> existingIds = existing.getAttributeValues().stream().map(ProductAttributeValue::getId).collect(Collectors.toSet());
            if (existingIds.equals(newIds)) {
                throw new BadRequestException("Already existed others SKU with their combination");
            }
        }
    }

    private void addPrice(ProductSku sku, BigDecimal value) {
        Price price = new Price();
        price.setPrice(value);
        price = priceRepository.save(price);

        ProductPrice link = new ProductPrice();
        link.setProductSku(sku);
        link.setPrice(price);
        productPriceRepository.save(link);
    }

    ProductSkuResponse toResponse(ProductSku sku) {
        BigDecimal currentPrice = productPriceRepository.findTopByProductSkuIdOrderByPrice_CreatedAtDesc(sku.getId())
                .map(pp -> pp.getPrice().getPrice())
                .orElse(null);

        Map<String, String> attributes = new LinkedHashMap<>();
        sku.getAttributeValues().forEach(v -> attributes.put(v.getProductAttribute().getName(), v.getValue()));

        return new ProductSkuResponse(sku.getId(), sku.getSkuCode(), sku.getStock(), currentPrice, attributes);
    }
}
