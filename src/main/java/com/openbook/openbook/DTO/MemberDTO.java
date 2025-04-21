package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Role role;
    private String email;
    private String username;
    private String password;
}
