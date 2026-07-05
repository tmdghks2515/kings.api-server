package com.kings.web.application.curation;

import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.CurationRepository;
import com.kings.web.domain.curation.detail.CategoriesDetail;
import com.kings.web.domain.curation.detail.CategoryProductsDetail;
import com.kings.web.domain.curation.detail.CurationItem;
import com.kings.web.domain.curation.detail.MainBannerDetail;
import com.kings.web.domain.curation.detail.NormalBannerDetail;
import com.kings.web.domain.curation.detail.TitledProductsDetail;
import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageRepository;
import com.kings.web.domain.brand.BrandRepository;
import com.kings.web.domain.category.CategoryRepository;
import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
import com.kings.web.domain.link.BrandLink;
import com.kings.web.domain.link.CategoryLink;
import com.kings.web.domain.link.ImageLink;
import com.kings.web.domain.link.ProductDetailLink;
import com.kings.web.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurationService {

    private final CurationRepository curationRepository;
    private final CurationPageRepository curationPageRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileResourceRepository fileResourceRepository;

    @Transactional
    public Long create(CurationCommand command) {
        validate(command);

        var curationPage = getCurationPage(command);
        var curation = Curation.create(curationPage, command.type(), command.name(), command.sortOrder(), command.detail());

        return curationRepository.save(curation).getId();
    }

    @Transactional(readOnly = true)
    public List<CurationData> findAll() {
        var curations = curationRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Curation::getSortOrder))
                .toList();
        var fileResources = findImageFileResources(curations);

        return curations.stream()
                .map(curation -> CurationData.from(curation, fileResources))
                .toList();
    }

    @Transactional(readOnly = true)
    public CurationData findById(Long id) {
        var curation = getById(id);
        return CurationData.from(curation, findImageFileResources(List.of(curation)));
    }

    @Transactional
    public void update(Long id, CurationCommand command) {
        validate(command);

        var curationPage = getCurationPage(command);
        var curation = getById(id);
        curation.update(curationPage, command.type(), command.name(), command.sortOrder(), command.detail());
    }

    @Transactional
    public void updateSortOrders(List<CurationSortOrderCommand> commands) {
        validateSortOrders(commands);

        var commandById = commands.stream()
                .collect(Collectors.toMap(CurationSortOrderCommand::id, Function.identity()));
        var curations = curationRepository.findAllByIds(commandById.keySet().stream().toList());

        if (curations.size() != commandById.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션 목록에 존재하지 않는 ID가 포함되어 있습니다.");
        }

        for (var curation : curations) {
            curation.updateSortOrder(commandById.get(curation.getId()).sortOrder());
        }
    }

    @Transactional
    public void delete(Long id) {
        curationRepository.delete(getById(id));
    }

    private Curation getById(Long id) {
        return curationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "큐레이션을 찾을 수 없습니다."));
    }

    private CurationPage getCurationPage(CurationCommand command) {
        return curationPageRepository.findByType(command.curationPageType())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션 페이지 타입을 찾을 수 없습니다."));
    }

    private List<FileResource> findImageFileResources(List<Curation> curations) {
        var imageStorageKeys = CurationData.collectImageStorageKeys(curations);
        if (imageStorageKeys.isEmpty()) {
            return List.of();
        }

        return fileResourceRepository.findAllByStorageKeyIn(imageStorageKeys);
    }

    private void validate(CurationCommand command) {
        if (command.curationPageType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션 페이지 타입을 선택해 주세요.");
        }
        if (command.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션 타입을 선택해 주세요.");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션명을 입력해 주세요.");
        }
        if (command.sortOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서는 0 이상이어야 합니다.");
        }
        if (command.detail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션 상세 정보를 입력해 주세요.");
        }

        validateDetail(command);
    }

    private void validateSortOrders(List<CurationSortOrderCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "변경할 정렬 순서 정보를 입력해 주세요.");
        }
        if (commands.stream().anyMatch(Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서 목록에 빈 값이 포함될 수 없습니다.");
        }
        if (commands.stream().anyMatch(command -> command.id() == null || command.id() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서 목록에 올바른 큐레이션 ID를 입력해 주세요.");
        }
        if (commands.stream().anyMatch(command -> command.sortOrder() < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서는 0 이상이어야 합니다.");
        }
        if (commands.stream().map(CurationSortOrderCommand::id).collect(Collectors.toSet()).size() != commands.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정렬 순서 목록에 중복된 큐레이션 ID가 있습니다.");
        }
    }

    private void validateDetail(CurationCommand command) {
        switch (command.type()) {
            case MAIN_BANNER -> {
                if (!(command.detail() instanceof MainBannerDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메인 배너 상세 정보 형식이 올바르지 않습니다.");
                }
                validateCurationItems(detail.getItems(), "detail.items");
            }
            case NORMAL_BANNER -> {
                if (!(command.detail() instanceof NormalBannerDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일반 배너 상세 정보 형식이 올바르지 않습니다.");
                }
                validateCurationItems(detail.getItems(), "detail.items");
            }
            case CATEGORIES -> {
                if (!(command.detail() instanceof CategoriesDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 상세 정보 형식이 올바르지 않습니다.");
                }
                validateImageLinks(detail.getItems(), "detail.items");
            }
            case TITLED_PRODUCTS -> {
                if (!(command.detail() instanceof TitledProductsDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "타이틀 상품 상세 정보 형식이 올바르지 않습니다.");
                }
                if (!StringUtils.hasText(detail.getTitle())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "큐레이션 상세 제목을 입력해 주세요.");
                }
                validateProductCodes(detail.getProductCodes(), "detail.productCodes");
            }
            case CATEGORY_PRODUCTS -> {
                if (!(command.detail() instanceof CategoryProductsDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 상품 상세 정보 형식이 올바르지 않습니다.");
                }
                validateCategoryId(detail.getCategoryId(), "detail.categoryId");
                validateProductCodes(detail.getProductCodes(), "detail.productCodes");
            }
        }
    }

    private void validateCurationItems(List<CurationItem> items, String fieldName) {
        validateList(items, fieldName);
        validateImageLinks(items.stream().map(item -> (ImageLink) item).toList(), fieldName);
    }

    private void validateImageLinks(List<? extends ImageLink> items, String fieldName) {
        validateList(items, fieldName);

        for (var item : items) {
            if (!StringUtils.hasText(item.getImageStorageKey())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + ".imageUrl을 입력해 주세요.");
            }
            validateLink(item, fieldName + ".link");
        }
    }

    private void validateLink(ImageLink item, String fieldName) {
        var link = item.getLink();
        if (link == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 입력해 주세요.");
        }

        if (link instanceof ProductDetailLink productDetailLink) {
            validateProductCode(productDetailLink.getProductCode(), fieldName + ".productCode");
            return;
        }
        if (link instanceof CategoryLink categoryLink) {
            validateCategoryId(parseLong(categoryLink.getCategoryId(), fieldName + ".categoryId"), fieldName + ".categoryId");
            return;
        }
        if (link instanceof BrandLink brandLink) {
            validateBrandId(parseLong(brandLink.getBrandId(), fieldName + ".brandId"), fieldName + ".brandId");
            return;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " 형식이 올바르지 않습니다.");
    }

    private void validateProductCodes(List<String> productCodes, String fieldName) {
        validateList(productCodes, fieldName);

        if (productCodes.stream().anyMatch(productCode -> !StringUtils.hasText(productCode))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "에는 올바른 상품 코드만 입력할 수 있습니다.");
        }
        /*if (new HashSet<>(productCodes).size() != productCodes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "에는 중복된 상품 코드를 입력할 수 없습니다.");
        }*/
        /*if (productRepository.countByCodes(productCodes) != productCodes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "에 존재하지 않는 상품 코드가 포함되어 있습니다.");
        }*/
    }

    private void validateProductCode(String productCode, String fieldName) {
        if (!StringUtils.hasText(productCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 입력해 주세요.");
        }
        if (!productRepository.existsByCode(productCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 찾을 수 없습니다.");
        }
    }

    private void validateCategoryId(Long categoryId, String fieldName) {
        if (categoryId == null || categoryId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 입력해 주세요.");
        }
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 찾을 수 없습니다.");
        }
    }

    private Long parseLong(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 입력해 주세요.");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "은(는) 숫자로 입력해 주세요.");
        }
    }

    private void validateBrandId(Long brandId, String fieldName) {
        if (brandId == null || brandId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 입력해 주세요.");
        }
        if (brandRepository.findById(brandId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 찾을 수 없습니다.");
        }
    }

    private void validateList(List<?> values, String fieldName) {
        if (values == null || values.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "을(를) 입력해 주세요.");
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + "에 빈 값이 포함될 수 없습니다.");
        }
    }
}
