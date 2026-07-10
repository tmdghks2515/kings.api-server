package com.kings.web.infra.presentation.brand;

import com.kings.web.application.brand.BrandCommand;
import com.kings.web.application.brand.BrandData;
import com.kings.web.application.brand.BrandService;
import com.kings.web.infra.web.NoAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public Long create(@RequestBody BrandCommand command) {
        return brandService.create(command);
    }

    @NoAuthentication
    @GetMapping
    public List<BrandData> findAll() {
        return brandService.findAll();
    }

    @NoAuthentication
    @GetMapping("/{id}")
    public BrandData findById(@PathVariable Long id) {
        return brandService.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody BrandCommand command) {
        brandService.update(id, command);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        brandService.delete(id);
    }
}
