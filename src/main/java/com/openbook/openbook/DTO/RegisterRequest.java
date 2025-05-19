package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Schema(example = "READER")
    private Role role;
    @Schema(example = "user@openbook.com")
    private String email;
    @Schema(example = "user_openbook")
    private String username;
    @Schema(example = "secret_password")
    private String password;
}
