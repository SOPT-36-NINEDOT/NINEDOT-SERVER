package org.sopt36.ninedotserver.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME = "BearerAuth";
    private static final String PROD_ORIGIN = "https://api.ninedot.p-e.kr";
    private static final String DEV_ORIGIN = "https://dev.ninedot.p-e.kr";
    private static final String LOCAL_ORIGIN = "http://localhost:8080";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
            .components(new Components().addSecuritySchemes(
                SCHEME_NAME,
                new SecurityScheme()
                    .name(SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            ));
    }

    @Bean
    public GroupedOpenApi all() {
        return GroupedOpenApi.builder()
            .group("all")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return groupedApi("v1", "/api/v1");
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return groupedApi("v2", "/api/v2");
    }

    private GroupedOpenApi groupedApi(String group, String fullPrefix) {
        return GroupedOpenApi.builder()
            .group(group)
            .pathsToMatch(fullPrefix + "/**")
            .addOpenApiCustomizer(stripPrefixAndSetServer(fullPrefix))
            .build();
    }

    private OpenApiCustomizer stripPrefixAndSetServer(String fullPrefix) {
        final Pattern exact = Pattern.compile("^" + Pattern.quote(fullPrefix) + "/?$");
        final String prefixWithSlash = fullPrefix.endsWith("/") ? fullPrefix : fullPrefix + "/";

        return openApi -> {
            Paths source = openApi.getPaths();
            if (source == null || source.isEmpty()) {
                return;
            }

            Paths rewritten = new Paths();
            source.forEach((path, item) -> {
                String newPath = path;
                if (exact.matcher(path).matches()) {
                    newPath = "/";
                } else if (path.startsWith(prefixWithSlash)) {
                    newPath = path.substring(fullPrefix.length());
                }
                rewritten.addPathItem(newPath, item);
            });

            openApi.setPaths(rewritten);

            List<Server> servers = new ArrayList<>();
            servers.add(new Server().url(PROD_ORIGIN + fullPrefix).description("Prod"));
            servers.add(new Server().url(DEV_ORIGIN + fullPrefix).description("Dev"));
            servers.add(new Server().url(LOCAL_ORIGIN + fullPrefix).description("Local"));
            openApi.setServers(servers);
        };
    }

    private Info apiInfo() {
        return new Info()
            .title("NiNE DOT Swagger")
            .description("AT SOPT 36기 앱잼 | NiNE DOT Swagger")
            .version("1.0.0");
    }
}
