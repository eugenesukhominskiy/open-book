package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.DTO.MemberResponse;
import com.openbook.openbook.enums.BookStatus;
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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private AdminController adminController;

    private Member testMember;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setUsername("admin");
        testMember.setEmail("admin@example.com");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setStatus(BookStatus.PENDING);
    }

    @Test
    void viewAllUsers_ReturnsListOfMembers() {
        // Arrange
        when(memberService.findAllMembers()).thenReturn(List.of(testMember));

        // Act
        ResponseEntity<?> response = adminController.viewAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<?> responseList = (List<?>) response.getBody();
        assertEquals(1, responseList.size());
        assertTrue(responseList.get(0) instanceof MemberResponse);
    }

    @Test
    void viewAllModerationBook_WithPendingBooks_ReturnsBooks() {
        // Arrange
        when(bookService.getAllNotVerifiedBooks()).thenReturn(List.of(testBook));

        // Act
        ResponseEntity<?> response = adminController.viewAllModerationBook();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<?> responseList = (List<?>) response.getBody();
        assertEquals(1, responseList.size());
        assertTrue(responseList.get(0) instanceof BookResponse);
    }

    @Test
    void viewAllModerationBook_NoPendingBooks_ReturnsNotFound() {
        // Arrange
        when(bookService.getAllNotVerifiedBooks()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = adminController.viewAllModerationBook();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No books found for moderation.", response.getBody());
    }

    @Test
    void approvingBook_ExistingBook_ReturnsApprovedBook() {
        // Arrange
        Book approvedBook = new Book();
        approvedBook.setId(1L);
        approvedBook.setStatus(BookStatus.APPROVED);

        when(bookService.changeBookStatus(1L, BookStatus.APPROVED)).thenReturn(approvedBook);

        // Act
        ResponseEntity<?> response = adminController.approvingBook(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BookResponse);
        assertEquals(BookStatus.APPROVED, ((BookResponse) response.getBody()).getStatus());
    }

    @Test
    void approvingBook_NonExistingBook_ReturnsNotFound() {
        // Arrange
        when(bookService.changeBookStatus(1L, BookStatus.APPROVED))
                .thenThrow(new NoSuchElementException());

        // Act
        ResponseEntity<?> response = adminController.approvingBook(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found with ID: 1", response.getBody());
    }

    @Test
    void rejectingBook_ExistingBook_ReturnsRejectedBook() {
        // Arrange
        Book rejectedBook = new Book();
        rejectedBook.setId(1L);
        rejectedBook.setStatus(BookStatus.REJECTED);

        when(bookService.changeBookStatus(1L, BookStatus.REJECTED)).thenReturn(rejectedBook);

        // Act
        ResponseEntity<?> response = adminController.rejectingBook(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BookResponse);
        assertEquals(BookStatus.REJECTED, ((BookResponse) response.getBody()).getStatus());
    }

    @Test
    void rejectingBook_NonExistingBook_ReturnsNotFound() {
        // Arrange
        when(bookService.changeBookStatus(1L, BookStatus.REJECTED))
                .thenThrow(new NoSuchElementException());

        // Act
        ResponseEntity<?> response = adminController.rejectingBook(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found with ID: 1", response.getBody());
    }
}