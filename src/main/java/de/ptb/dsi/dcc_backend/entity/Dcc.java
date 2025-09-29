package de.ptb.dsi.dcc_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private String information;
    private boolean isDccValid;
    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    //@JsonBackReference
    private User user;


}
