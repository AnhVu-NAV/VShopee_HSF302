package com.hsf302.he186049.vshopee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VShopper - REST API Documentation")
                        .description("Tài liệu REST API cho hệ thống bán hàng VShopper.\n\n📦 Bao gồm: Quản lý sản phẩm, giỏ hàng, đơn hàng, người dùng, v.v.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("VShopper Dev Team")
                                .email("support@vshopper.vn")
                                .url("https://github.com/tunngn18/vshopper"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .addServersItem(new Server()
                        .url("http://localhost:" + serverPort + contextPath)
                        .description("Local server"));
    }
}
