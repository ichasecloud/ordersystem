package com.example.shoppingcartservice.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.*;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI cartOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shopping Cart Service API")
                        .description("API documentation for cart operations")
                        .version("1.0"));
    }

    @Bean
    public GroupedOpenApi cartGroup() {
        return GroupedOpenApi.builder()
                .group("cart")
                .packagesToScan("com.example.shoppingcartservice.controller")
                .build();
    }

}
