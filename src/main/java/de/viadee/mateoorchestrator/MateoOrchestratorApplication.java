package de.viadee.mateoorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the mateo-orchestrator.
 * This application will manage request to a set of mateo instances.
 */
@SpringBootApplication
public class MateoOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MateoOrchestratorApplication.class, args);
    }

}
