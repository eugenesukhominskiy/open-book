package com.openbook.openbook.repository;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByGithubId(String githubId);
}
