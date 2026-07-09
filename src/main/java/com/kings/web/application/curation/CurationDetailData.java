package com.kings.web.application.curation;

import com.kings.web.application.file.FileResourceData;
import com.kings.web.domain.curation.detail.BrandShortcutsDetail;
import com.kings.web.domain.curation.detail.CategoriesDetail;
import com.kings.web.domain.curation.detail.CurationDetail;
import com.kings.web.domain.curation.detail.ImageProductsDetail;
import com.kings.web.domain.curation.detail.MainBannerDetail;
import com.kings.web.domain.curation.detail.NormalBannerDetail;
import com.kings.web.domain.curation.detail.TitledProductsDetail;
import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.link.ImageLink;
import com.kings.web.domain.link.Link;

import java.util.List;
import java.util.Map;

public interface CurationDetailData {

    static CurationDetailData from(
            CurationDetail detail,
            Map<String, FileResource> fileResourceByStorageKey
    ) {
        if (detail instanceof MainBannerDetail mainBannerDetail) {
            return new MainBannerDetailData(
                    "MainBannerDetail",
                    toImageLinkData(mainBannerDetail.getItems(), fileResourceByStorageKey)
            );
        }
        if (detail instanceof NormalBannerDetail normalBannerDetail) {
            return new NormalBannerDetailData(
                    "NormalBannerDetail",
                    toImageLinkData(normalBannerDetail.getItems(), fileResourceByStorageKey)
            );
        }
        if (detail instanceof CategoriesDetail categoriesDetail) {
            return new CategoriesDetailData(
                    "CategoriesDetail",
                    toImageLinkData(categoriesDetail.getItems(), fileResourceByStorageKey)
            );
        }
        if (detail instanceof TitledProductsDetail titledProductsDetail) {
            return new TitledProductsDetailData(
                    "TitledProductsDetail",
                    titledProductsDetail.getTitle(),
                    titledProductsDetail.getProductCodes()
            );
        }
        if (detail instanceof ImageProductsDetail imageProductsDetail) {
            return new ImageProductsDetailData(
                    "ImageProductsDetail",
                    toFileResourceData(imageProductsDetail.getImageStorageKey(), fileResourceByStorageKey),
                    imageProductsDetail.getLink(),
                    imageProductsDetail.getTitle(),
                    imageProductsDetail.getSubTitle(),
                    imageProductsDetail.getProductCodes()
            );
        }
        if (detail instanceof BrandShortcutsDetail brandShortcutsDetail) {
            return new BrandShortcutsDetailData(
                    "BrandShortcutsDetail",
                    brandShortcutsDetail.getBrandIds()
            );
        }

        return null;
    }

    static List<String> collectImageStorageKeys(CurationDetail detail) {
        if (detail instanceof MainBannerDetail mainBannerDetail) {
            return collectImageStorageKeys(mainBannerDetail.getItems());
        }
        if (detail instanceof NormalBannerDetail normalBannerDetail) {
            return collectImageStorageKeys(normalBannerDetail.getItems());
        }
        if (detail instanceof CategoriesDetail categoriesDetail) {
            return collectImageStorageKeys(categoriesDetail.getItems());
        }
        if (detail instanceof ImageProductsDetail imageProductsDetail
                && imageProductsDetail.getImageStorageKey() != null) {
            return List.of(imageProductsDetail.getImageStorageKey());
        }

        return List.of();
    }

    private static List<String> collectImageStorageKeys(List<? extends ImageLink> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .filter(item -> item != null && item.getImageStorageKey() != null)
                .map(ImageLink::getImageStorageKey)
                .distinct()
                .toList();
    }

    private static List<ImageLinkData> toImageLinkData(
            List<ImageLink> items,
            Map<String, FileResource> fileResourceByStorageKey
    ) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .filter(item -> item != null)
                .map(item -> new ImageLinkData(
                        toFileResourceData(item.getImageStorageKey(), fileResourceByStorageKey),
                        item.getLink()
                ))
                .toList();
    }

    private static FileResourceData toFileResourceData(
            String storageKey,
            Map<String, FileResource> fileResourceByStorageKey
    ) {
        var fileResource = fileResourceByStorageKey.get(storageKey);
        return fileResource == null ? null : FileResourceData.from(fileResource);
    }

    record MainBannerDetailData(
            String type,
            List<ImageLinkData> items
    ) implements CurationDetailData {
    }

    record NormalBannerDetailData(
            String type,
            List<ImageLinkData> items
    ) implements CurationDetailData {
    }

    record CategoriesDetailData(
            String type,
            List<ImageLinkData> items
    ) implements CurationDetailData {
    }

    record TitledProductsDetailData(
            String type,
            String title,
            List<String> productCodes
    ) implements CurationDetailData {
    }

    record ImageProductsDetailData(
            String type,
            FileResourceData imageStorageKey,
            Link link,
            String title,
            String subTitle,
            List<String> productCodes
    ) implements CurationDetailData {
    }

    record BrandShortcutsDetailData(
            String type,
            List<Long> brandIds
    ) implements CurationDetailData {
    }

    record ImageLinkData(
            FileResourceData imageStorageKey,
            Link link
    ) {
    }
}
