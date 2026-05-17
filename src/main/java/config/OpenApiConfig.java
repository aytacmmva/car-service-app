package config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI carServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Car Service REST API")
                        .description("Car Service management system - Production Ready")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Car Service Team")
                                .email("info@carservice.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}
