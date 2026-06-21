package com.kings.web.application.product;

import com.kings.web.domain.product.Product;
import com.kings.web.domain.product.ProductRepository;
import com.kings.web.domain.product.option.ProductOption;
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

@Service
@RequiredArgsConstructor
public class CreateProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void create(CreateProductCommand command) {
        validate(command);

        if (productRepository.existsByCode(command.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "product code already exists");
        }

        var product = Product.create(
                command.code(),
                command.name(),
                command.price()
        );

        for (var optionCommand : normalizedOptions(command.options())) {
            product.addOption(ProductOption.create(
                    product,
                    optionCommand.name(),
                    optionCommand.price(),
                    optionCommand.type()
            ));
        }

        productRepository.save(product);
    }

    private void validate(CreateProductCommand command) {
        if (!StringUtils.hasText(command.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (command.price() != null && command.price() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be greater than or equal to 0");
        }

        var optionNames = new HashSet<String>();
        for (var option : normalizedOptions(command.options())) {
            if (!StringUtils.hasText(option.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option name is required");
            }
            if (option.price() != null && option.price() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option price must be greater than or equal to 0");
            }
            if (option.type() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option type is required");
            }
            if (!optionNames.add(option.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "option name must be unique");
            }
        }
    }

    private List<CreateProductCommand.CreateProductOptionCommand> normalizedOptions(
            List<CreateProductCommand.CreateProductOptionCommand> options
    ) {
        return options == null ? List.of() : options;
    }
}
