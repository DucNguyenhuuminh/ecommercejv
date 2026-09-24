package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.product.ProductRequest;
import duc.project.ecommercejv.dto.product.ProductResponse;
import duc.project.ecommercejv.entity.product.*;
import duc.project.ecommercejv.exception.NotFoundException;
import duc.project.ecommercejv.repository.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PriceRepository priceRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductImageRepository productImageRepository;
    private final DiscountRepository discountRepository;
    private final ProductDiscountRepository productDiscountRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(Long categoryId, String keyword, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecs.hasCategory(categoryId)).and(ProductSpecs.nameContains(keyword));
        return productRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find product"));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product product = new Product();
        applyBasicFields(product, req);
        product = productRepository.save(product);

        if (req.getPrice() != null) {
            addPrice(product, req);
        }
        if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
            addImages(product, req.getImageUrls());
        }
        if (req.getDiscountIds() != null && !req.getDiscountIds().isEmpty()) {
            linkDiscounts(product, req.getDiscountIds());
        }
        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalStateException("Cannot find product"));
        applyBasicFields(product, req);
        productRepository.save(product);

        if (req.getPrice() != null) {
            addPrice(product, req);
        }
        return toResponse(product);
    }

    public void softDelete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalStateException("Cannot find product"));
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    private void applyBasicFields(Product product, ProductRequest req) {
        product.setSeri(req.getSeri());
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setStock(req.getStock());

        if (req.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(req.getCategoryIds());
            if (categories.size() != req.getCategoryIds().size()) {
                throw new NotFoundException("One or many categories not exist");
            }
            product.setCategories(new HashSet<>(categories));
        }
    }

    private void addPrice(Product product, ProductRequest req) {
        Price price = new Price();
        price.setPrice(req.getPrice());
        price = priceRepository.save(price);

        ProductPrice link = new ProductPrice();
        link.setProduct(product);
        link.setPrice(price);
        productPriceRepository.save(link);
    }

    private void addImages(Product product, List<String> urls) {
        for (String url : urls) {
            ProductImage image = new ProductImage();
            image.setUrl(url);
            image = productImageRepository.save(image);
            product.getImages().add(image);
        }
        productRepository.save(product);
    }

    private void linkDiscounts(Product product, List<Long> discountIds) {
        List<Discount> discounts = discountRepository.findAllById(discountIds);
        for (Discount discount : discounts) {
            ProductDiscount link = new ProductDiscount();
            link.setProduct(product);
            link.setDiscount(discount);
            productDiscountRepository.save(link);
        }
    }

    private ProductResponse toResponse(Product p) {
        var currentPrice = productPriceRepository
                .findTopByProductIdOrderByPrice_CreatedAtDesc(p.getId())
                .map(pp -> pp.getPrice().getPrice())
                .orElse(null);

        List<String> categoryNames = p.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        List<String> imageUrls = p.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList());
        List<String> discountCodes = productDiscountRepository.findByProductId(p.getId()).stream().map(pd -> pd.getDiscount().getCode()).collect(Collectors.toList());

        return new ProductResponse(p.getId(), p.getSeri(), p.getName(), p.getDescription(), p.getStock(), currentPrice, categoryNames, imageUrls, discountCodes);
    }
}
