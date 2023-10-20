package de.ptb.dsi.dcc_backend.model;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "dcc")
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class Dcc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Hidden
    private UUID id;
    private String pid;
    private String xmlBase64;
    private boolean isDccValid;

}
