package com.openbook.openbook.repository;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByAuthor_Id(Long authorId);
    List<Book> findAllByStatusNot(BookStatus status);

    @Query("""
       SELECT b FROM Book b
       LEFT JOIN b.author a
       LEFT JOIN b.genre g
       WHERE (:title IS NOT NULL AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          OR (:author IS NOT NULL AND LOWER(a.username) LIKE LOWER(CONCAT('%', :author, '%')))
          OR (:genre IS NOT NULL AND LOWER(g.name) LIKE LOWER(CONCAT('%', :genre, '%')))
       """)
    List<Book> findBooksByAnyParams(@Param("title") String title,
                                    @Param("author") String author,
                                    @Param("genre") String genre);
}
