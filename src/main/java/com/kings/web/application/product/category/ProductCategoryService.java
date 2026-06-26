package com.kings.web.application.product.category;

import com.kings.web.domain.product.category.ProductCategory;
import com.kings.web.domain.product.category.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public Long create(ProductCategoryCommand command) {
        validate(command);

        var parentCategory = findParentCategory(command.parentCategoryId());
        var productCategory = createCategory(command, parentCategory);

        return productCategory.getId();
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryData> findAll() {
        return productCategoryRepository.findAll()
                .stream()
                .filter(productCategory -> productCategory.getParentCategory() == null)
                .map(this::toData)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductCategoryData findById(Long id) {
        return toData(getById(id));
    }

    @Transactional
    public void update(Long id, ProductCategoryCommand command) {
        validate(command);

        var productCategory = getById(id);
        var parentCategory = findParentCategory(command.parentCategoryId());

        if (parentCategory != null && parentCategory.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parent category must be different from category");
        }
        if (parentCategory != null && isDescendant(id, parentCategory.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parent category must not be a child category");
        }

        productCategory.update(command.depth(), command.name(), parentCategory);
        syncChildren(productCategory, normalizedChildren(command.children()), new HashSet<>(Set.of(id)));
    }

    @Transactional
    public void delete(Long id) {
        var productCategory = getById(id);

        deleteWithChildren(productCategory);
    }

    private ProductCategory getById(Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
    }

    private ProductCategory findParentCategory(Long parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }

        return productCategoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "parent category not found"));
    }

    private void validate(ProductCategoryCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category is required");
        }
        if (command.depth() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "depth must be greater than or equal to 0");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        for (var child : normalizedChildren(command.children())) {
            validate(child);
        }
    }

    private ProductCategory createCategory(ProductCategoryCommand command, ProductCategory parentCategory) {
        var productCategory = productCategoryRepository.save(
                ProductCategory.create(command.depth(), command.name(), parentCategory)
        );

        for (var child : normalizedChildren(command.children())) {
            createCategory(child, productCategory);
        }

        return productCategory;
    }

    private void syncChildren(
            ProductCategory parentCategory,
            List<ProductCategoryCommand> childCommands,
            Set<Long> ancestorIds
    ) {
        var childCommandsById = childCommands.stream()
                .filter(child -> child.id() != null)
                .collect(Collectors.toMap(
                        ProductCategoryCommand::id,
                        Function.identity(),
                        (left, right) -> {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "child category id must be unique");
                        }
                ));

        var existingChildren = productCategoryRepository.findByParentCategoryId(parentCategory.getId());
        for (var existingChild : existingChildren) {
            if (!childCommandsById.containsKey(existingChild.getId())) {
                deleteWithChildren(existingChild);
            }
        }

        for (var childCommand : childCommands) {
            if (childCommand.id() == null) {
                createCategory(childCommand, parentCategory);
                continue;
            }

            if (ancestorIds.contains(childCommand.id())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "child category must not be an ancestor category");
            }

            var childCategory = getById(childCommand.id());
            childCategory.update(childCommand.depth(), childCommand.name(), parentCategory);

            var nextAncestorIds = new HashSet<>(ancestorIds);
            nextAncestorIds.add(childCategory.getId());
            syncChildren(childCategory, normalizedChildren(childCommand.children()), nextAncestorIds);
        }
    }

    private void deleteWithChildren(ProductCategory productCategory) {
        for (var child : productCategoryRepository.findByParentCategoryId(productCategory.getId())) {
            deleteWithChildren(child);
        }

        productCategoryRepository.delete(productCategory);
    }

    private ProductCategoryData toData(ProductCategory productCategory) {
        var children = productCategoryRepository.findByParentCategoryId(productCategory.getId())
                .stream()
                .map(this::toData)
                .toList();

        return ProductCategoryData.from(productCategory, children);
    }

    private boolean isDescendant(Long parentCategoryId, Long candidateCategoryId) {
        for (var child : productCategoryRepository.findByParentCategoryId(parentCategoryId)) {
            if (child.getId().equals(candidateCategoryId) || isDescendant(child.getId(), candidateCategoryId)) {
                return true;
            }
        }

        return false;
    }

    private List<ProductCategoryCommand> normalizedChildren(List<ProductCategoryCommand> children) {
        return children == null ? List.of() : children;
    }
}
