package com.kings.web.application.curation;

import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.CurationRepository;
import com.kings.web.domain.curation.detail.CategoriesDetail;
import com.kings.web.domain.curation.detail.CategoryProductsDetail;
import com.kings.web.domain.curation.detail.CurationItem;
import com.kings.web.domain.curation.detail.MainBannerDetail;
import com.kings.web.domain.curation.detail.NormalBannerDetail;
import com.kings.web.domain.curation.detail.TitledProductsDetail;
import com.kings.web.domain.brand.BrandRepository;
import com.kings.web.domain.category.CategoryRepository;
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

@Service
@RequiredArgsConstructor
public class CurationService {

    private final CurationRepository curationRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long create(CurationCommand command) {
        validate(command);

        var curation = Curation.create(command.type(), command.name(), command.sortOrder(), command.detail());

        return curationRepository.save(curation).getId();
    }

    @Transactional(readOnly = true)
    public List<CurationData> findAll() {
        return curationRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Curation::getSortOrder))
                .map(CurationData::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CurationData findById(Long id) {
        return CurationData.from(getById(id));
    }

    @Transactional
    public void update(Long id, CurationCommand command) {
        validate(command);

        var curation = getById(id);
        curation.update(command.type(), command.name(), command.sortOrder(), command.detail());
    }

    @Transactional
    public void delete(Long id) {
        curationRepository.delete(getById(id));
    }

    private Curation getById(Long id) {
        return curationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "curation not found"));
    }

    private void validate(CurationCommand command) {
        if (command.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (command.sortOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortOrder must be greater than or equal to 0");
        }
        if (command.detail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail is required");
        }

        validateDetail(command);
    }

    private void validateDetail(CurationCommand command) {
        switch (command.type()) {
            case MAIN_BANNER -> {
                if (!(command.detail() instanceof MainBannerDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail must be MainBannerDetail");
                }
                validateCurationItems(detail.getItems(), "detail.items");
            }
            case NORMAL_BANNER -> {
                if (!(command.detail() instanceof NormalBannerDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail must be NormalBannerDetail");
                }
                validateCurationItems(detail.getItems(), "detail.items");
            }
            case CATEGORIES -> {
                if (!(command.detail() instanceof CategoriesDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail must be CategoriesDetail");
                }
                validateImageLinks(detail.getItems(), "detail.items");
            }
            case TITLED_PRODUCTS -> {
                if (!(command.detail() instanceof TitledProductsDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail must be TitledProductsDetail");
                }
                if (!StringUtils.hasText(detail.getTitle())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail.title is required");
                }
                validateProductCodes(detail.getProductCodes(), "detail.productCodes");
            }
            case CATEGORY_PRODUCTS -> {
                if (!(command.detail() instanceof CategoryProductsDetail detail)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "detail must be CategoryProductsDetail");
                }
                validateCategoryId(detail.getCategoryId(), "detail.categoryId");
                validateProductCodes(detail.getProductCodes(), "detail.productCodes");
            }
        }
    }

    private void validateCurationItems(List<CurationItem> items, String fieldName) {
        validateList(items, fieldName);
        validateImageLinks(items.stream().map(item -> (ImageLink) item).toList(), fieldName);

        for (var item : items) {
            if (!StringUtils.hasText(item.getTitle())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + ".title is required");
            }
            if (!StringUtils.hasText(item.getDescription())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + ".description is required");
            }
        }
    }

    private void validateImageLinks(List<? extends ImageLink> items, String fieldName) {
        validateList(items, fieldName);

        for (var item : items) {
            if (!StringUtils.hasText(item.getImageUrl())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + ".imageUrl is required");
            }
            validateLink(item, fieldName + ".link");
        }
    }

    private void validateLink(ImageLink item, String fieldName) {
        var link = item.getLink();
        if (link == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
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

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is invalid");
    }

    private void validateProductCodes(List<String> productCodes, String fieldName) {
        validateList(productCodes, fieldName);

        if (productCodes.stream().anyMatch(productCode -> !StringUtils.hasText(productCode))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must contain valid product codes");
        }
        if (new HashSet<>(productCodes).size() != productCodes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be unique");
        }
        if (productRepository.countByCodes(productCodes) != productCodes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " contains unknown product code");
        }
    }

    private void validateProductCode(String productCode, String fieldName) {
        if (!StringUtils.hasText(productCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        if (!productRepository.existsByCode(productCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " not found");
        }
    }

    private void validateCategoryId(Long categoryId, String fieldName) {
        if (categoryId == null || categoryId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " not found");
        }
    }

    private Long parseLong(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be a number");
        }
    }

    private void validateBrandId(Long brandId, String fieldName) {
        if (brandId == null || brandId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        if (brandRepository.findById(brandId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " not found");
        }
    }

    private void validateList(List<?> values, String fieldName) {
        if (values == null || values.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must not contain null");
        }
    }
}
