package com.openbook.openbook.services;

import com.openbook.openbook.DTO.BookRequest;
import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Genre;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.BookRepository;
import com.openbook.openbook.repository.GenreRepository;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BookService bookService;

    private BookRequest bookRequest;
    private Genre genre;
    private Member author;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest();
        bookRequest.setTitle("Test Book");
        bookRequest.setDescription("Test Description");
        bookRequest.setCharacters(300000L);
        bookRequest.setPrice(400);
        bookRequest.setGenreId(1L);

        genre = new Genre();
        genre.setId(1L);
        genre.setName("Fantasy");

        author = new Member();
        author.setId(1L);
        author.setUsername("testAuthor");

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author(author)
                .genre(genre)
                .status(BookStatus.PENDING)
                .build();
    }

    @Test
    void create_WithValidData_ReturnsCreatedBook() {
        // Arrange
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book result = bookService.create(bookRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void create_WithInvalidGenre_ThrowsException() {
        // Arrange
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                bookService.create(bookRequest, 1L)
        );
    }

    @Test
    void viewBook_ExistingId_ReturnsBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Book result = bookService.viewBook(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void viewBook_NonExistingId_ReturnsNull() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Book result = bookService.viewBook(1L);

        // Assert
        assertNull(result);
    }

    @Test
    void update_AuthorMatches_UpdatesBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookRequest updateRequest = new BookRequest();
        updateRequest.setTitle("Updated Title");

        // Act
        Book result = bookService.update(1L, updateRequest, "testAuthor");

        // Assert
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository).save(book);
    }

    @Test
    void update_AuthorDoesNotMatch_ThrowsAccessDenied() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                bookService.update(1L, bookRequest, "anotherUser")
        );
    }

    @Test
    void delete_ExistingId_DeletesBook() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);

        // Act
        bookService.delete(1L);

        // Assert
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void delete_NonExistingId_ThrowsException() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                bookService.delete(1L)
        );
    }

    @Test
    void findAllAuthorBooks_ReturnsBooksList() {
        // Arrange
        when(bookRepository.findAllByAuthor_Id(1L)).thenReturn(List.of(book));

        // Act
        List<Book> result = bookService.findAllAuthorBooks(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void searchBooks_WithTitle_ReturnsFilteredBooks() {
        // Arrange
        when(bookRepository.findBooksByAnyParams("Test", null, null))
                .thenReturn(List.of(book));

        // Act
        List<Book> result = bookService.searchBooks("Test", null, null);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void searchBooks_NoParams_ReturnsAllBooks() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(book));

        // Act
        List<Book> result = bookService.searchBooks(null, null, null);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getAllNotVerifiedBooks_ReturnsPendingBooks() {
        // Arrange
        when(bookRepository.findAllByStatusNot(BookStatus.APPROVED))
                .thenReturn(List.of(book));

        // Act
        List<Book> result = bookService.getAllNotVerifiedBooks();

        // Assert
        assertEquals(1, result.size());
        assertEquals(BookStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void changeBookStatus_UpdatesStatus() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book result = bookService.changeBookStatus(1L, BookStatus.APPROVED);

        // Assert
        assertEquals(BookStatus.APPROVED, result.getStatus());
    }

    @Test
    void findById_ReturnsOptionalBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Optional<Book> result = bookService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
    }
}