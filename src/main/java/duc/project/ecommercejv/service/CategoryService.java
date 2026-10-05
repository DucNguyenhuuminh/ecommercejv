package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.product.CategoryRequest;
import duc.project.ecommercejv.dto.product.CategoryResponse;
import duc.project.ecommercejv.entity.product.Category;
import duc.project.ecommercejv.repository.product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final int MAX_DEPTH = 5;
    private static final long ROOT_KEY = 0L;

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        validateParent(req.getParentId(), null);
        Category category = new Category();
        applyRequest(category, req);
        return toFlatResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalStateException("Cannot find category"));
        validateParent(req.getParentId(), id);
        applyRequest(category, req);
        return toFlatResponse(categoryRepository.save(category));
    }

    @Transactional
    public void softDelete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalStateException("Cannot find category"));
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getFlatList() {
        return categoryRepository.findAll().stream().map(this::toFlatResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getTree() {
        List<Category> all = categoryRepository.findAll();
        Map<Long, List<Category>> byParent = all.stream().collect(Collectors.groupingBy(c -> c.getParentId() == null ? ROOT_KEY : c.getParentId()));
        return buildChildren(ROOT_KEY, byParent, 0);
    }

//    ==========HELPER==========
    private List<CategoryResponse> buildChildren(Long parentId, Map<Long, List<Category>> byParent, int depth) {
        if (depth >= MAX_DEPTH) {
            return List.of();
        }
        return byParent.getOrDefault(parentId, List.of()).stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getParentId(), buildChildren(c.getId(), byParent, depth+1)))
                .collect(Collectors.toList());
    }

    private void validateParent(Long parentId, Long selfId) {
        if (parentId == null) {
            return;
        }
        if (parentId.equals(selfId)) {
            throw new IllegalStateException("Category cannot be father of itself");
        }
        if (!categoryRepository.existsById(parentId)) {
            throw new IllegalStateException("Father's category is not existed");
        }
    }

    private void applyRequest(Category category, CategoryRequest req) {
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        category.setParentId(req.getParentId());
    }

    private CategoryResponse toFlatResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getParentId(), List.of());
    }
}
