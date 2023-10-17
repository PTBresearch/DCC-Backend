package de.ptb.dsi.dcc_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "dcc")
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class Dcc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String pid;
    private String xmlBase64;
    private boolean isDccValid;

}
