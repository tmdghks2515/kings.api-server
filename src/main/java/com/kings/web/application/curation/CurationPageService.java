package com.kings.web.application.curation;

import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.brand.BrandRepository;
import com.kings.web.domain.category.Category;
import com.kings.web.domain.category.CategoryRepository;
import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.detail.CategoriesDetail;
import com.kings.web.domain.curation.detail.CategoryProductsDetail;
import com.kings.web.domain.curation.detail.TitledProductsDetail;
import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageRepository;
import com.kings.web.domain.curation.page.CurationPageType;
import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
import com.kings.web.domain.link.BrandLink;
import com.kings.web.domain.link.CategoryLink;
import com.kings.web.domain.link.Link;
import com.kings.web.domain.link.ProductDetailLink;
import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurationPageService {

    private final CurationPageRepository curationPageRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final FileResourceRepository fileResourceRepository;

    @Transactional(readOnly = true)
    public List<CurationPageData> findAll() {
        return curationPageRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CurationPage::getId))
                .map(CurationPageData::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CurationPageDetailData findById(Long id) {
        return curationPageRepository.findById(id)
                .map(curationPage -> CurationPageDetailData.from(
                        curationPage,
                        findImageFileResources(curationPage.getCurations())
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "큐레이션 페이지를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public CurationPageDetailData findByType(CurationPageType type) {
        return curationPageRepository.findByType(type)
                .map(curationPage -> CurationPageDetailData.from(
                        curationPage,
                        findImageFileResources(curationPage.getCurations())
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "큐레이션 페이지를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public PublicCurationPageData findPublicByType(CurationPageType type) {
        var curationPage = curationPageRepository.findByType(type)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "큐레이션 페이지를 찾을 수 없습니다."));
        var productCodes = collectProductCodes(curationPage.getCurations());
        var categoryIds = collectCategoryIds(curationPage.getCurations());
        var brandIds = collectBrandIds(curationPage.getCurations());
        var productByCode = findProductsByCode(productCodes);
        var mainImageStorageKeyByProductCode = findMainImageStorageKeysByProductCode(productCodes);
        var optionNamesByProductCode = findOptionNamesByProductCode(productCodes);
        var categoryById = findCategoriesById(categoryIds);
        var brandById = findBrandsById(brandIds);

        return PublicCurationPageData.from(
                curationPage,
                productByCode,
                mainImageStorageKeyByProductCode,
                optionNamesByProductCode,
                categoryById,
                brandById
        );
    }

    private List<FileResource> findImageFileResources(List<Curation> curations) {
        var imageStorageKeys = CurationData.collectImageStorageKeys(curations);
        if (imageStorageKeys.isEmpty()) {
            return List.of();
        }

        return fileResourceRepository.findAllByStorageKeyIn(imageStorageKeys);
    }

    private List<String> collectProductCodes(List<Curation> curations) {
        var productCodes = new LinkedHashSet<String>();
        for (var curation : curations) {
            var detail = curation.getDetail();
            if (detail instanceof TitledProductsDetail titledProductsDetail
                    && titledProductsDetail.getProductCodes() != null) {
                productCodes.addAll(titledProductsDetail.getProductCodes());
            }
            if (detail instanceof CategoryProductsDetail categoryProductsDetail
                    && categoryProductsDetail.getProductCodes() != null) {
                productCodes.addAll(categoryProductsDetail.getProductCodes());
            }
            if (detail instanceof CategoriesDetail categoriesDetail && categoriesDetail.getItems() != null) {
                for (var item : categoriesDetail.getItems()) {
                    if (item != null) {
                        addProductCode(productCodes, item.getLink());
                    }
                }
            }
        }
        productCodes.remove(null);
        return List.copyOf(productCodes);
    }

    private List<Long> collectCategoryIds(List<Curation> curations) {
        var categoryIds = new LinkedHashSet<Long>();
        for (var curation : curations) {
            var detail = curation.getDetail();
            if (detail instanceof CategoryProductsDetail categoryProductsDetail
                    && categoryProductsDetail.getCategoryId() != null) {
                categoryIds.add(categoryProductsDetail.getCategoryId());
            }
            if (detail instanceof CategoriesDetail categoriesDetail && categoriesDetail.getItems() != null) {
                for (var item : categoriesDetail.getItems()) {
                    if (item != null) {
                        addCategoryId(categoryIds, item.getLink());
                    }
                }
            }
        }
        return List.copyOf(categoryIds);
    }

    private List<Long> collectBrandIds(List<Curation> curations) {
        var brandIds = new LinkedHashSet<Long>();
        for (var curation : curations) {
            var detail = curation.getDetail();
            if (detail instanceof CategoriesDetail categoriesDetail && categoriesDetail.getItems() != null) {
                for (var item : categoriesDetail.getItems()) {
                    if (item != null) {
                        addBrandId(brandIds, item.getLink());
                    }
                }
            }
        }
        return List.copyOf(brandIds);
    }

    private void addProductCode(LinkedHashSet<String> productCodes, Link link) {
        if (link instanceof ProductDetailLink productDetailLink && productDetailLink.getProductCode() != null) {
            productCodes.add(productDetailLink.getProductCode());
        }
    }

    private void addCategoryId(LinkedHashSet<Long> categoryIds, Link link) {
        if (link instanceof CategoryLink categoryLink) {
            var categoryId = parseLong(categoryLink.getCategoryId());
            if (categoryId != null) {
                categoryIds.add(categoryId);
            }
        }
    }

    private void addBrandId(LinkedHashSet<Long> brandIds, Link link) {
        if (link instanceof BrandLink brandLink) {
            var brandId = parseLong(brandLink.getBrandId());
            if (brandId != null) {
                brandIds.add(brandId);
            }
        }
    }

    private Map<String, Product> findProductsByCode(List<String> productCodes) {
        if (productCodes.isEmpty()) {
            return Map.of();
        }

        return productRepository.findAllByCodes(productCodes)
                .stream()
                .collect(Collectors.toMap(Product::getCode, Function.identity()));
    }

    private Map<String, String> findMainImageStorageKeysByProductCode(List<String> productCodes) {
        if (productCodes.isEmpty()) {
            return Map.of();
        }

        return productRepository.findMainImageStorageKeysByProductCodes(productCodes)
                .stream()
                .collect(Collectors.toMap(
                        row -> row.productCode(),
                        row -> row.imageStorageKey(),
                        (left, right) -> left
                ));
    }

    private Map<String, List<String>> findOptionNamesByProductCode(List<String> productCodes) {
        if (productCodes.isEmpty()) {
            return Map.of();
        }

        return productRepository.findOptionNamesByProductCodes(productCodes)
                .stream()
                .collect(Collectors.groupingBy(
                        row -> row.productCode(),
                        Collectors.mapping(row -> row.optionName(), Collectors.toList())
                ));
    }

    private Map<Long, Category> findCategoriesById(List<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return Map.of();
        }

        return categoryRepository.findAllByIds(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
    }

    private Map<Long, Brand> findBrandsById(List<Long> brandIds) {
        if (brandIds.isEmpty()) {
            return Map.of();
        }

        return brandRepository.findAllByIds(brandIds)
                .stream()
                .collect(Collectors.toMap(Brand::getId, Function.identity()));
    }

    private Long parseLong(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
