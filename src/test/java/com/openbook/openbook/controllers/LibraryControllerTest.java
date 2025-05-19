package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.BookService;
import com.openbook.openbook.services.LibraryService;
import com.openbook.openbook.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private LibraryService libraryService;

    @Mock
    private MemberService memberService;

    @Mock
    private BookService bookService;

    @Mock
    private Principal principal;

    @InjectMocks
    private LibraryController libraryController;

    private final String username = "testUser";
    private final Long userId = 1L;
    private final Long bookId = 1L;
    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(userId);
        member.setUsername(username);

        book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        when(principal.getName()).thenReturn(username);
    }

    @Test
    void getMemberLibrary_Success() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(libraryService.getLibrary(userId)).thenReturn(List.of(book));

        ResponseEntity<?> response = libraryController.getMemberLibrary(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<?> books = (List<?>) response.getBody();
        assertEquals(1, books.size());
        assertTrue(books.get(0) instanceof BookResponse);
        assertEquals(book.getTitle(), ((BookResponse) books.get(0)).getTitle());
    }

    @Test
    void getMemberLibrary_MemberNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = libraryController.getMemberLibrary(principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Member is not found", response.getBody());
    }

    @Test
    void buyBook_Success() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));
        doNothing().when(libraryService).add(userId, bookId);

        ResponseEntity<?> response = libraryController.buyBook(bookId, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book successfully added to the library", response.getBody());
    }

    @Test
    void buyBook_MemberNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = libraryController.buyBook(bookId, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Member not found", response.getBody());
    }

    @Test
    void buyBook_BookNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(bookService.findById(bookId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = libraryController.buyBook(bookId, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with id " + bookId + " not found", response.getBody());
    }

    @Test
    void buyBook_AddBookError() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));
        doThrow(new IllegalArgumentException("Book already in library"))
                .when(libraryService).add(userId, bookId);

        ResponseEntity<?> response = libraryController.buyBook(bookId, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error adding the book"));
    }

    @Test
    void removeBookFromLibrary_Success() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));
        when(libraryService.remove(userId, bookId)).thenReturn(true);

        ResponseEntity<String> response = libraryController.removeBookFromLibrary(bookId, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book removed from library", response.getBody());
    }

    @Test
    void removeBookFromLibrary_MemberNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<String> response = libraryController.removeBookFromLibrary(bookId, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Member is not found", response.getBody());
    }

    @Test
    void removeBookFromLibrary_BookNotFound() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(bookService.findById(bookId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = libraryController.removeBookFromLibrary(bookId, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book with id " + bookId + " not found", response.getBody());
    }

    @Test
    void removeBookFromLibrary_BookNotInLibrary() {
        when(memberService.findByUsername(username)).thenReturn(Optional.of(member));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));
        when(libraryService.remove(userId, bookId)).thenReturn(false);

        ResponseEntity<String> response = libraryController.removeBookFromLibrary(bookId, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book not found in the member's library", response.getBody());
    }
}