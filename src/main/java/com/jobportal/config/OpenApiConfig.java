package com.jobportal.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Job Portal System API",
        version = "1.0.0",
        description = "Enterprise Job Portal REST API — Auth, Jobs, Applicants, Recruiters, Admin",
        contact = @Contact(name = "Job Portal Team", email = "support@jobportal.com")
    ),
    servers = {
        @Server(url = "/api", description = "Default"),
        @Server(url = "http://localhost:8080/api", description = "Local Dev")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {}
