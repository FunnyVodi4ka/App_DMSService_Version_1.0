package ru.astondevs.mycare.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI (Swagger 3) для документирования API.
 * <p>
 * Этот класс настраивает глобальные параметры безопасности
 * для Swagger UI, позволяя тестировать
 * защищенные эндпоинты путем добавления кастомных
 * заголовков (X-User-Id, X-Keycloak-Id и т.д.).
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает бин {@link OpenAPI}.
     * <p>
     * Добавляет три схемы безопасности (SecurityScheme)
     * типа APIKEY (в заголовке)
     * для эмуляции заголовков, пробрасываемых
     * нашим security-стартером.
     *
     * @return кастомизированный экземпляр {@link OpenAPI}.
     */
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("keycloakIdHeader", new SecurityScheme()
                    .name("X-Keycloak-Id")
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER))
                .addSecuritySchemes("userIdHeader", new SecurityScheme()
                    .name("X-User-Id")
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER))
                .addSecuritySchemes("rolesHeader", new SecurityScheme()
                    .name("X-User-Roles")
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER))
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("userIdHeader")
                .addList("keycloakIdHeader")
                .addList("rolesHeader"));
    }
}
