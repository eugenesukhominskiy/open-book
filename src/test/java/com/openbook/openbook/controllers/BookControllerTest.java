package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Genre;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private final Long bookId = 1L;
    private final Book book = Book.builder()
            .id(bookId)
            .title("Test Book")
            .author(new Member())
            .genre(new Genre())
            .build();

    @Test
    void searchBooks_WithAllParameters() {
        // Arrange
        String title = "Test";
        String author = "Author";
        String genre = "Genre";

        when(bookService.searchBooks(title, author, genre))
                .thenReturn(List.of(book));

        // Act
        ResponseEntity<?> response = bookController.searchBooks(title, author, genre);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<?> books = (List<?>) response.getBody();
        assertEquals(1, books.size());
        assertTrue(books.get(0) instanceof BookResponse);
        assertEquals(book.getTitle(), ((BookResponse) books.get(0)).getTitle());
    }

    @Test
    void searchBooks_WithTitleOnly() {
        // Arrange
        String title = "Test";

        when(bookService.searchBooks(title, null, null))
                .thenReturn(List.of(book));

        // Act
        ResponseEntity<?> response = bookController.searchBooks(title, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void searchBooks_WithAuthorOnly() {
        // Arrange
        String author = "Author";

        when(bookService.searchBooks(null, author, null))
                .thenReturn(List.of(book));

        // Act
        ResponseEntity<?> response = bookController.searchBooks(null, author, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void searchBooks_WithGenreOnly() {
        // Arrange
        String genre = "Genre";

        when(bookService.searchBooks(null, null, genre))
                .thenReturn(List.of(book));

        // Act
        ResponseEntity<?> response = bookController.searchBooks(null, null, genre);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void searchBooks_NoParameters() {
        // Arrange
        when(bookService.searchBooks(null, null, null))
                .thenReturn(List.of(book));

        // Act
        ResponseEntity<?> response = bookController.searchBooks(null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void searchBooks_EmptyResult() {
        // Arrange
        when(bookService.searchBooks(any(), any(), any()))
                .thenReturn(List.of());

        // Act
        ResponseEntity<?> response = bookController.searchBooks("Non-existent", null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    void getBookProfile_BookFound() {
        // Arrange
        when(bookService.findById(bookId))
                .thenReturn(Optional.of(book));

        // Act
        ResponseEntity<?> response = bookController.getBookProfile(bookId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BookResponse);
        assertEquals(book.getTitle(), ((BookResponse) response.getBody()).getTitle());
    }

    @Test
    void getBookProfile_BookNotFound() {
        // Arrange
        when(bookService.findById(bookId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = bookController.getBookProfile(bookId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found", response.getBody());
    }
}