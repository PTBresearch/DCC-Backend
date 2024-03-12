package de.ptb.dsi.dcc_backend.model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Table(name = "dcc")
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class Dcc {
    @Id
    @UuidGenerator
    private String id;
    private String pid;
    @Lob
    @Column(length = 10485763)
    private String xmlBase64;
    private boolean isDccValid;

}
