package de.ptb.dsi.dcc_backend.model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(name = "dcc")
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class Dcc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Hidden
    @UuidGenerator
    private String id;
    private String pid;
    @Lob
    @Column(length = 10485763)
    private String xmlBase64;
    private boolean isDccValid;

}
