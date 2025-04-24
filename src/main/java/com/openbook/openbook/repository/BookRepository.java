package com.openbook.openbook.repository;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByAuthor_Id(Long authorId);
    List<Book> findAllByStatusNot(BookStatus status);

    @Query("SELECT b FROM Book b " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.author.username) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:genre IS NULL OR LOWER(b.genre.name) LIKE LOWER(CONCAT('%', :genre, '%')))")
    List<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           @Param("genre") String genre);

}
