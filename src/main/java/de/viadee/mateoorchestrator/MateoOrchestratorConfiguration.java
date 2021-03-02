package de.viadee.mateoorchestrator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Marcel_Flasskamp
 */
@Configuration
@EnableScheduling
@EnableAsync
@Profile("!test")
@OpenAPIDefinition(
        info = @Info(
                title = "Mateo Orchestrator REST API",
                version = "0.1",
                description = "OpenApi Spec for Mateo Orchestrator REST API",
                license = @License(name = "Apache License 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(url = "http://www.viadee.de", name = "viadee Unternehmensberatung AG")),
        servers = {
                @Server(
                        description = "The API server",
                        url = "http://{host}:{port}",
                        variables = {
                                @ServerVariable(name = "host", description = "Host", defaultValue = "localhost"),
                                @ServerVariable(name = "port", description = "Port", defaultValue = "8083")
                        })
        }
)
public class MateoOrchestratorConfiguration {

}
