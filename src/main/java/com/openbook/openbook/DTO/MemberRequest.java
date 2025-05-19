package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberRequest {
    @Schema(example = "READER")
    private Role role;
    @Schema(example = "user@openbook.com")
    private String email;
    @Schema(example = "new_user")
    private String username;
    @Schema(example = "user_password")
    private String password;
}
