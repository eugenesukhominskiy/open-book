package com.openbook.openbook.services;

import com.openbook.openbook.DTO.BookDTO;
import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Genre;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.BookRepository;
import com.openbook.openbook.repository.GenreRepository;
import com.openbook.openbook.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Book create(BookDTO bookDTO) {
        Genre genre = genreRepository.findById(bookDTO.getGenreId()).orElseThrow(() -> new RuntimeException("Genre not found"));
        Member author = memberRepository.findById(bookDTO.getAuthorId()).orElseThrow(() -> new RuntimeException("Author not found"));

        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .characters(bookDTO.getCharacters())
                .price(bookDTO.getPrice())
                .status(BookStatus.PENDING)
                .genre(genre)
                .author(author)
                .build();
        return bookRepository.save(book);
    }

    public Book viewBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book update(Long bookId, BookDTO bookDTO, String username) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NoSuchElementException("Book not found"));

//        if (!book.getAuthor().getUsername().equals(username)) {
//            throw new AccessDeniedException("You are not allowed to update this book.");
//        }

        Genre genre = genreRepository.findById(bookDTO.getGenreId()).orElseThrow(() -> new RuntimeException("Genre not found"));

        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        book.setCharacters(bookDTO.getCharacters());
        book.setPrice(bookDTO.getPrice());
        book.setGenre(genre);

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

    public List<Book> searchBooks(String title, String author, String genre) {
        return bookRepository.searchBooks(title, author, genre);
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