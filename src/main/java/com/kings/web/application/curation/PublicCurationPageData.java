package com.kings.web.application.curation;

import com.kings.web.domain.brand.Brand;
import com.kings.web.domain.category.Category;
import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.CurationType;
import com.kings.web.domain.curation.detail.CategoriesDetail;
import com.kings.web.domain.curation.detail.CategoryProductsDetail;
import com.kings.web.domain.curation.detail.TitledProductsDetail;
import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageType;
import com.kings.web.domain.link.BrandLink;
import com.kings.web.domain.link.CategoryLink;
import com.kings.web.domain.link.ImageLink;
import com.kings.web.domain.link.Link;
import com.kings.web.domain.link.ProductDetailLink;
import com.kings.web.domain.product.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record PublicCurationPageData(
        Long id,
        CurationPageType type,
        String typeLabel,
        List<PublicCurationData> curations
) {
    public static PublicCurationPageData from(
            CurationPage curationPage,
            Map<String, Product> productByCode,
            Map<String, String> mainImageStorageKeyByProductCode,
            Map<String, List<String>> optionNamesByProductCode,
            Map<Long, Category> categoryById,
            Map<Long, Brand> brandById
    ) {
        return new PublicCurationPageData(
                curationPage.getId(),
                curationPage.getType(),
                curationPage.getType().getLabel(),
                curationPage.getCurations()
                        .stream()
                        .sorted(Comparator.comparingInt(Curation::getSortOrder))
                        .map(curation -> PublicCurationData.from(
                                curation,
                                productByCode,
                                mainImageStorageKeyByProductCode,
                                optionNamesByProductCode,
                                categoryById,
                                brandById
                        ))
                        .toList()
        );
    }

    public record PublicCurationData(
            Long id,
            CurationPageType curationPageType,
            CurationType type,
            String name,
            int sortOrder,
            Object detail
    ) {
        public static PublicCurationData from(
                Curation curation,
                Map<String, Product> productByCode,
                Map<String, String> mainImageStorageKeyByProductCode,
                Map<String, List<String>> optionNamesByProductCode,
                Map<Long, Category> categoryById,
                Map<Long, Brand> brandById
        ) {
            return new PublicCurationData(
                    curation.getId(),
                    curation.getCurationPage().getType(),
                    curation.getType(),
                    curation.getName(),
                    curation.getSortOrder(),
                    toPublicDetail(
                            curation.getDetail(),
                            productByCode,
                            mainImageStorageKeyByProductCode,
                            optionNamesByProductCode,
                            categoryById,
                            brandById
                    )
            );
        }
    }

    public record TitledProductsPublicDetail(
            String title,
            List<PublicProductData> products
    ) {
    }

    public record CategoryProductsPublicDetail(
            PublicCategoryData category,
            List<PublicProductData> products
    ) {
    }

    public record CategoriesPublicDetail(
            List<PublicImageLinkData> items
    ) {
    }

    public record PublicImageLinkData(
            String imageStorageKey,
            String link,
            String name
    ) {
        public static PublicImageLinkData from(
                ImageLink imageLink,
                Map<String, Product> productByCode,
                Map<Long, Category> categoryById,
                Map<Long, Brand> brandById
        ) {
            var link = imageLink.getLink();
            return new PublicImageLinkData(
                    imageLink.getImageStorageKey(),
                    link == null ? null : link.getLink(),
                    resolveName(link, productByCode, categoryById, brandById)
            );
        }
    }

    public record PublicProductData(
            String code,
            String name,
            Double price,
            String link,
            String imageStorageKey,
            PublicBrandData brand,
            List<String> optionNames
    ) {
        public static PublicProductData from(
                Product product,
                String imageStorageKey,
                List<String> optionNames
        ) {
            return new PublicProductData(
                    product.getCode(),
                    product.getName(),
                    product.getPrice(),
                    new ProductDetailLink(product.getCode()).getLink(),
                    imageStorageKey,
                    product.getBrand() == null ? null : PublicBrandData.from(product.getBrand()),
                    optionNames
            );
        }
    }

    public record PublicBrandData(
            Long id,
            String name,
            String link
    ) {
        public static PublicBrandData from(Brand brand) {
            return new PublicBrandData(
                    brand.getId(),
                    brand.getName(),
                    new BrandLink(String.valueOf(brand.getId())).getLink()
            );
        }
    }

    public record PublicCategoryData(
            Long id,
            String name,
            String link
    ) {
        public static PublicCategoryData from(Category category) {
            return new PublicCategoryData(
                    category.getId(),
                    category.getName(),
                    new CategoryLink(String.valueOf(category.getId())).getLink()
            );
        }
    }

    private static Object toPublicDetail(
            Object detail,
            Map<String, Product> productByCode,
            Map<String, String> mainImageStorageKeyByProductCode,
            Map<String, List<String>> optionNamesByProductCode,
            Map<Long, Category> categoryById,
            Map<Long, Brand> brandById
    ) {
        if (detail instanceof TitledProductsDetail titledProductsDetail) {
            return new TitledProductsPublicDetail(
                    titledProductsDetail.getTitle(),
                    toProducts(
                            titledProductsDetail.getProductCodes(),
                            productByCode,
                            mainImageStorageKeyByProductCode,
                            optionNamesByProductCode
                    )
            );
        }

        if (detail instanceof CategoriesDetail categoriesDetail) {
            var items = categoriesDetail.getItems() == null ? List.<ImageLink>of() : categoriesDetail.getItems();
            return new CategoriesPublicDetail(
                    items.stream()
                            .filter(Objects::nonNull)
                            .map(item -> PublicImageLinkData.from(item, productByCode, categoryById, brandById))
                            .toList()
            );
        }

        if (detail instanceof CategoryProductsDetail categoryProductsDetail) {
            var categoryId = categoryProductsDetail.getCategoryId();
            var category = categoryId == null ? null : categoryById.get(categoryId);
            return new CategoryProductsPublicDetail(
                    category == null ? null : PublicCategoryData.from(category),
                    toProducts(
                            categoryProductsDetail.getProductCodes(),
                            productByCode,
                            mainImageStorageKeyByProductCode,
                            optionNamesByProductCode
                    )
            );
        }

        return detail;
    }

    private static List<PublicProductData> toProducts(
            List<String> productCodes,
            Map<String, Product> productByCode,
            Map<String, String> mainImageStorageKeyByProductCode,
            Map<String, List<String>> optionNamesByProductCode
    ) {
        if (productCodes == null) {
            return List.of();
        }

        return productCodes.stream()
                .map(productByCode::get)
                .filter(Objects::nonNull)
                .map(product -> PublicProductData.from(
                        product,
                        mainImageStorageKeyByProductCode.get(product.getCode()),
                        optionNamesByProductCode.getOrDefault(product.getCode(), List.of())
                ))
                .toList();
    }

    private static String resolveName(
            Link link,
            Map<String, Product> productByCode,
            Map<Long, Category> categoryById,
            Map<Long, Brand> brandById
    ) {
        if (link instanceof ProductDetailLink productDetailLink) {
            if (productDetailLink.getProductCode() == null) {
                return null;
            }
            var product = productByCode.get(productDetailLink.getProductCode());
            return product == null ? null : product.getName();
        }

        if (link instanceof CategoryLink categoryLink) {
            var categoryId = parseLong(categoryLink.getCategoryId());
            if (categoryId == null) {
                return null;
            }
            var category = categoryById.get(categoryId);
            return category == null ? null : category.getName();
        }

        if (link instanceof BrandLink brandLink) {
            var brandId = parseLong(brandLink.getBrandId());
            if (brandId == null) {
                return null;
            }
            var brand = brandById.get(brandId);
            return brand == null ? null : brand.getName();
        }

        return null;
    }

    private static Long parseLong(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
