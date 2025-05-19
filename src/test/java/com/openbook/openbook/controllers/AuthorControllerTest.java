package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookRequest;
import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.BookService;
import com.openbook.openbook.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private MemberService memberService;

    @Mock
    private Principal principal;

    @InjectMocks
    private AuthorController authorController;

    private final String username = "testAuthor";
    private final Long authorId = 1L;
    private final Long bookId = 1L;
    private Member author;
    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        author = new Member();
        author.setId(authorId);
        author.setUsername(username);

        book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor(author);

        bookRequest = new BookRequest();
        bookRequest.setTitle("Test Book");
        bookRequest.setDescription("Test Description");

        when(principal.getName()).thenReturn(username);
    }

    @Test
    void createNewBook_Success() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(author));
        when(bookService.create(bookRequest, authorId)).thenReturn(book);

        ResponseEntity<?> response = authorController.createNewBook(principal, bookRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BookResponse);
        assertEquals(book.getTitle(), ((BookResponse) response.getBody()).getTitle());
    }

    @Test
    void createNewBook_AuthorNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authorController.createNewBook(principal, bookRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Member is not found", response.getBody());
    }

    @Test
    void createNewBook_InvalidData() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(author));
        when(bookService.create(bookRequest, authorId)).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = authorController.createNewBook(principal, bookRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Book not created"));
    }

    @Test
    void viewOwnWorks_Success() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(author));
        when(bookService.findAllAuthorBooks(authorId)).thenReturn(List.of(book));

        ResponseEntity<?> response = authorController.viewOwnWorks(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<?> books = (List<?>) response.getBody();
        assertEquals(1, books.size());
        assertTrue(books.get(0) instanceof BookResponse);
    }

    @Test
    void viewOwnWorks_AuthorNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authorController.viewOwnWorks(principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Member is not found", response.getBody());
    }

    @Test
    void editBook_Success() {
        when(bookService.update(bookId, bookRequest, username)).thenReturn(book);

        ResponseEntity<?> response = authorController.editBook(bookId, bookRequest, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BookResponse);
        assertEquals(book.getTitle(), ((BookResponse) response.getBody()).getTitle());
    }

    @Test
    void editBook_AccessDenied() {
        when(bookService.update(bookId, bookRequest, username))
                .thenThrow(new AccessDeniedException("Access denied"));

        ResponseEntity<?> response = authorController.editBook(bookId, bookRequest, principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You are not allowed to edit this book.", response.getBody());
    }

    @Test
    void editBook_NotFound() {
        when(bookService.update(bookId, bookRequest, username))
                .thenThrow(new NoSuchElementException("Book not found"));

        ResponseEntity<?> response = authorController.editBook(bookId, bookRequest, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found.", response.getBody());
    }

    @Test
    void getStatusOfBooks_AuthorNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authorController.getStatusOfBooks(principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Author not found", response.getBody());
    }
}