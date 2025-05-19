package com.openbook.openbook.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Schema(example = "user_openbook")
    private String username;
    @Schema(example = "secret_password")
    private String password;
}
