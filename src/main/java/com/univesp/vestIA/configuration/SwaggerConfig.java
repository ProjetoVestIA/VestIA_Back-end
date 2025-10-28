package com.univesp.vestIA.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI springBlogPessoalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(" Repositório Projeto VestIA")
                        .description("O VestAI é uma plataforma gamificada de estudos para vestibulares como ENEM e Fuvest. Aqui, você treina com questões reais, acumula pontos a cada acerto e pode contar com a ajuda de uma inteligência artificial que explica os erros e comenta as questões. É a forma mais prática, divertida e inteligente de se preparar para o vestibular")
                        .version("v1.0.0")
                        .license(new License()
                                .name("Universidade Virtual do Estado de São Paulo (UNIVESP)")
                                .url("https://univesp.br/"))
                        .contact(new Contact()
                                .name("VestIA")
                                .url("https://github.com/ProjetoVestIA")))
                .externalDocs(new ExternalDocumentation()
                        .description("Github: Back-end")
                        .url("https://github.com/ProjetoVestIA/VestIA_Back-end"))
                .components(new Components()
                        .addSecuritySchemes("jwt_auth", createSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("jwt_auth"));
    }

    @Bean
    OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {

        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {

                ApiResponses apiResponses = operation.getResponses();

                apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
                apiResponses.addApiResponse("201", createApiResponse("Objeto persistido"));
                apiResponses.addApiResponse("204", createApiResponse("Objeto excluído"));
                apiResponses.addApiResponse("400", createApiResponse("Erro na requisição"));
                apiResponses.addApiResponse("401", createApiResponse("Acesso não autorizado"));
                apiResponses.addApiResponse("403", createApiResponse("Acesso proibido"));
                apiResponses.addApiResponse("404", createApiResponse("Objeto não encontrado"));
                apiResponses.addApiResponse("500", createApiResponse("Erro na aplicação"));

            }));
        };
    }

    private ApiResponse createApiResponse(String message) {

        return new ApiResponse().description(message);

    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("jwt_auth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Insira apenas o token JWT (a palavra 'Bearer' será adicionada automaticamente)");
    }
}
