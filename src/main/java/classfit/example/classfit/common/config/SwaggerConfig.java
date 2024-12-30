package classfit.example.classfit.common.config;

import classfit.example.classfit.auth.security.filter.CustomLoginFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import java.util.Optional;

@OpenAPIDefinition(
    info = @Info(title = "CLASSFIT API 명세서", description = "백엔드 API"),
    security = @SecurityRequirement(name = "JWT TOKEN")
)
@SecurityScheme(
    name = "JWT TOKEN",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ApplicationContext applicationContext; // ApplicationContext 주입
    private static final String LOGIN_ENDPOINT = "/api/v1/signin";

    @Bean
    public OpenApiCustomizer springSecurityLoginCustomised() {


        return openAPI -> {
            FilterChainProxy filterChainProxy = applicationContext.getBean(
                AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);

            for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
                Optional<CustomLoginFilter> optionalFilter =
                    filterChain.getFilters().stream()
                        .filter(CustomLoginFilter.class::isInstance)
                        .map(CustomLoginFilter.class::cast)
                        .findAny();

                if (optionalFilter.isPresent()) {
                    CustomLoginFilter customLoginFilter = optionalFilter.get();

                    Operation operation = new Operation()
                        .summary("로그인 API입니다.")
                        .addTagsItem("로그인 컨트롤러") // 태그 추가
                        .requestBody(new RequestBody().content(
                            new Content().addMediaType(
                                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                new MediaType().schema(new ObjectSchema()
                                    .addProperties("email", new StringSchema().example("email@example.com"))
                                    .addProperties(customLoginFilter.getPasswordParameter(), new StringSchema().example("password123"))
                                )
                            )
                        ));

                    ApiResponses apiResponses = new ApiResponses();
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
                        new ApiResponse()
                            .description("로그인 성공")
                            .content(new Content().addMediaType(
                                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                new MediaType().example("{\"accessToken\": \"sample-access-token\", \"refreshToken\": \"sample-refresh-token\"}")
                            )));
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                        new ApiResponse()
                            .description("로그인 실패")
                            .content(new Content().addMediaType(
                                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                new MediaType().example("{\"error\": \"Unauthorized\"}")
                            )));

                    operation.responses(apiResponses);

                    operation.responses(apiResponses);

                    PathItem pathItem = new PathItem().post(operation);
                    openAPI.getPaths().addPathItem(LOGIN_ENDPOINT, pathItem);
                }
            }
        };
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("CLASSFIT API")
            .pathsToMatch("/api/**")
            .addOpenApiCustomizer(springSecurityLoginCustomised())
            .build();
    }
}