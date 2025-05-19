package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.Role;
import com.openbook.openbook.model.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private int booksCount;
    private int libraryCount;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.role = member.getRole();
        this.booksCount = member.getBooks() != null ? member.getBooks().size() : 0;
        this.libraryCount = member.getLibrary() != null ? member.getLibrary().size() : 0;
    }
}
