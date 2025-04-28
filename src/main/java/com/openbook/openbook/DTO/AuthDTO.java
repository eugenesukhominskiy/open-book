package com.openbook.openbook.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthDTO {
    @Schema(example = "new_user")
    private String username;
    @Schema(example = "user_password")
    private String password;
}
