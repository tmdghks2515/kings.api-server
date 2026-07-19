package com.kings.web.infra.presentation.brand;

import com.kings.web.application.brand.BrandData;
import com.kings.web.application.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/brands")
@RequiredArgsConstructor
public class PublicBrandController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandData> findAll() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    public BrandData findById(@PathVariable Long id) {
        return brandService.findById(id);
    }
}
