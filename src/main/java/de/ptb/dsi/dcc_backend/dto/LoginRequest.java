package de.ptb.dsi.dcc_backend.dto;




import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank
    private  String userName;
    @NotBlank
    @Size(min=12, message = "password must be at least 12 characters")
    private String password;
}
