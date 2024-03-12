package de.ptb.dsi.dcc_backend;


import de.ptb.dsi.dcc_backend.model.Dcc;
import de.ptb.dsi.dcc_backend.repository.DccRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DccBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DccBackendApplication.class, args);
    }
//   @Bean
//    CommandLineRunner commandLineRunner (DccRepository dccRepository){
//        return args -> {
//            dccRepository.save(Dcc.builder().pid("zu").isDccValid(true).xmlBase64("1222").build());
//        };
//    }
}
