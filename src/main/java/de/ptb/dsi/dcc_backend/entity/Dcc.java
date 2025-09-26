package de.ptb.dsi.dcc_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;


@Entity
@Table(name = "dcc")
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class Dcc {
    @Id
    @UuidGenerator
    @JsonIgnore
    private String id;
    private String pid;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String xmlBase64;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] signedTsrFile;
    private LocalDateTime createdAt;

    private boolean isDccValid;
    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


}
