package com.openbook.openbook.services;

import com.openbook.openbook.DTO.BookRequest;
import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Genre;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.BookRepository;
import com.openbook.openbook.repository.GenreRepository;
import com.openbook.openbook.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BookService(BookRepository bookRepository, GenreRepository genreRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.memberRepository = memberRepository;
    }

    public Book create(BookRequest request, Long memberId) {
        Genre genre = genreRepository.findById(request.getGenreId()).orElseThrow(() -> new RuntimeException("Genre not found"));
        Member author = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Author not found"));

        Book book = Book.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .characters(request.getCharacters())
                .price(request.getPrice())
                .status(BookStatus.PENDING)
                .genre(genre)
                .author(author)
                .build();
        return bookRepository.save(book);
    }

    public Book viewBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book update(Long bookId, BookRequest request, String username) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NoSuchElementException("Book not found"));

        if (!book.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update this book.");
        }

        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setCharacters(request.getCharacters());
        book.setPrice(request.getPrice());

        return bookRepository.save(book);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    public List<Book> findAllAuthorBooks(Long authorId) {
        return bookRepository.findAllByAuthor_Id(authorId);
    }

    public List<Book> searchBooks(String title, String authorUsername, String genreName) {
        boolean allParamsEmpty = (title == null || title.isBlank()) &&
                (authorUsername == null || authorUsername.isBlank()) &&
                (genreName == null || genreName.isBlank());

        if (allParamsEmpty) {
            return bookRepository.findAll(); // Возврат всех книг
        }

        return bookRepository.findBooksByAnyParams(title, authorUsername, genreName);
    }

    public List<Book> getAllNotVerifiedBooks() {
        return bookRepository.findAllByStatusNot(BookStatus.APPROVED);
    }

    public Book changeBookStatus(Long id, BookStatus status) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
        book.setStatus(status);

        return bookRepository.save(book);
    }

    public Optional<Book> findById(Long bookId) {
        return bookRepository.findById(bookId);
    }
}