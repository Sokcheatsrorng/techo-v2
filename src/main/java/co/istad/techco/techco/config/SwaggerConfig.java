package co.istad.techco.techco.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("TechCo API")
                        .version("v1")
                        .description("Build trust using blockchain technology "))
//                .servers(Arrays.asList(
//                        new Server().url("https://ishop-api.istad.co").description("iShop API"),
////                        new Server().url("https://ishop-api.cheatdev.online").description("Production API")
//                        new Server().url("http://localhost:8081").description("Local Development Server")
////                        new Server().url("http://202.178.125.77:10500").description("Local Development Server")
//                ));
                .addServersItem(
                        new Server().url("http://localhost:3243").description("TechCo API Server")
                );

    }
}



