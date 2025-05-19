package com.openbook.openbook.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.openbook.openbook.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(unique = true, nullable = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;

    @OneToMany(mappedBy = "author")
    @JsonBackReference
    private List<Book> books;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "library",
            joinColumns = @JoinColumn(name = "reader_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> library;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
