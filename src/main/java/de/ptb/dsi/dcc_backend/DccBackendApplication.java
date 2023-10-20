package de.ptb.dsi.dcc_backend;


import de.ptb.dsi.dcc_backend.repository.DccRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DccBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DccBackendApplication.class, args);
    }

}
