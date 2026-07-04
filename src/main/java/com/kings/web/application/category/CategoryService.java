package com.kings.web.application.category;

import com.kings.web.domain.category.Category;
import com.kings.web.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long create(CategoryCommand command) {
        validate(command);

        var parentCategory = findParentCategory(command.parentCategoryId());
        var category = createCategory(command, parentCategory);

        return category.getId();
    }

    @Transactional(readOnly = true)
    public List<CategoryData> findAll() {
        return categoryRepository.findAllOrderBySortOrder()
                .stream()
                .filter(category -> category.getParentCategory() == null)
                .map(this::toData)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryData findById(Long id) {
        return toData(getById(id));
    }

    @Transactional
    public void update(Long id, CategoryCommand command) {
        validate(command);

        var category = getById(id);
        var parentCategory = findParentCategory(command.parentCategoryId());

        if (parentCategory != null && parentCategory.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상위 카테고리는 현재 카테고리와 달라야 합니다.");
        }
        if (parentCategory != null && isDescendant(id, parentCategory.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "하위 카테고리를 상위 카테고리로 지정할 수 없습니다.");
        }

        category.update(command.depth(), command.name(), command.sortOrder(), parentCategory);
        syncChildren(category, normalizedChildren(command.children()), new HashSet<>(Set.of(id)));
    }

    @Transactional
    public void deleteAll(CategoryDeleteCommand command) {
        var categoryIds = validateDeleteCommand(command);
        var selectedCategoryIds = Set.copyOf(categoryIds);
        var categories = categoryIds.stream()
                .map(this::getById)
                .toList();

        for (var category : categories) {
            if (!hasSelectedAncestor(category, selectedCategoryIds)) {
                deleteWithChildren(category);
            }
        }
    }

    private Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."));
    }

    private Category findParentCategory(Long parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }

        return categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "상위 카테고리를 찾을 수 없습니다."));
    }

    private void validate(CategoryCommand command) {
        if (command == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 정보를 입력해 주세요.");
        }
        if (command.depth() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 깊이는 0 이상이어야 합니다.");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리명을 입력해 주세요.");
        }
        if (command.sortOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서는 0 이상이어야 합니다.");
        }
        for (var child : normalizedChildren(command.children())) {
            validate(child);
        }
    }

    private Category createCategory(CategoryCommand command, Category parentCategory) {
        var category = categoryRepository.save(
                Category.create(command.depth(), command.name(), command.sortOrder(), parentCategory)
        );

        for (var child : normalizedChildren(command.children())) {
            createCategory(child, category);
        }

        return category;
    }

    private void syncChildren(
            Category parentCategory,
            List<CategoryCommand> childCommands,
            Set<Long> ancestorIds
    ) {
        var childCommandsById = childCommands.stream()
                .filter(child -> child.id() != null)
                .collect(Collectors.toMap(
                        CategoryCommand::id,
                        Function.identity(),
                        (left, right) -> {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "하위 카테고리 ID는 중복될 수 없습니다.");
                        }
                ));

        var existingChildren = categoryRepository.findByParentCategoryId(parentCategory.getId());
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상위 카테고리를 하위 카테고리로 지정할 수 없습니다.");
            }

            var childCategory = getById(childCommand.id());
            childCategory.update(childCommand.depth(), childCommand.name(), childCommand.sortOrder(), parentCategory);

            var nextAncestorIds = new HashSet<>(ancestorIds);
            nextAncestorIds.add(childCategory.getId());
            syncChildren(childCategory, normalizedChildren(childCommand.children()), nextAncestorIds);
        }
    }

    private void deleteWithChildren(Category category) {
        for (var child : categoryRepository.findByParentCategoryId(category.getId())) {
            deleteWithChildren(child);
        }

        categoryRepository.delete(category);
    }

    private CategoryData toData(Category category) {
        var children = categoryRepository.findByParentCategoryIdOrderBySortOrder(category.getId())
                .stream()
                .map(this::toData)
                .toList();

        return CategoryData.from(category, children);
    }

    private boolean isDescendant(Long parentCategoryId, Long candidateCategoryId) {
        for (var child : categoryRepository.findByParentCategoryId(parentCategoryId)) {
            if (child.getId().equals(candidateCategoryId) || isDescendant(child.getId(), candidateCategoryId)) {
                return true;
            }
        }

        return false;
    }

    private List<Long> validateDeleteCommand(CategoryDeleteCommand command) {
        if (command == null || command.categoryIds() == null || command.categoryIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 카테고리를 선택해 주세요.");
        }

        var categoryIds = command.categoryIds();
        if (categoryIds.stream().anyMatch(Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 ID에 빈 값이 포함될 수 없습니다.");
        }
        if (new HashSet<>(categoryIds).size() != categoryIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 카테고리 목록에 중복된 ID가 있습니다.");
        }

        return categoryIds;
    }

    private boolean hasSelectedAncestor(Category category, Set<Long> selectedCategoryIds) {
        var parentCategory = category.getParentCategory();

        while (parentCategory != null) {
            if (selectedCategoryIds.contains(parentCategory.getId())) {
                return true;
            }

            parentCategory = parentCategory.getParentCategory();
        }

        return false;
    }

    private List<CategoryCommand> normalizedChildren(List<CategoryCommand> children) {
        return children == null ? List.of() : children;
    }
}
