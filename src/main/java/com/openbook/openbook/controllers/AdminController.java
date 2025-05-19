package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.DTO.MemberResponse;
import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.BookService;
import com.openbook.openbook.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin/")
@Tag(name = "Admin", description = "Controller for managing admin`s operation")
public class AdminController {
    private final MemberService memberService;
    private final BookService bookService;

    @Autowired
    public AdminController(MemberService memberService, BookService bookService) {
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<?> viewAllUsers() {
        List<Member> allMember = memberService.findAllMembers();
        List<MemberResponse> responses = allMember.stream()
                .map(MemberResponse::new)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/books/pending")
    @Operation(summary = "Get all books pending moderation", description = "Retrieves a list of all books that have not yet been approved.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending books retrieved successfully")
    })
    public ResponseEntity<?> viewAllModerationBook() {
        List<Book> books = bookService.getAllNotVerifiedBooks();
        List<BookResponse> responses = books.stream()
                .map(BookResponse::new)
                .toList();

        if (books.isEmpty()) {
            return ResponseEntity.status(404).body("No books found for moderation.");
        }

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/books/{id}/approve")
    @Operation(summary = "Approve a book", description = "Changes the status of a book to APPROVED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book approved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<?> approvingBook(@PathVariable Long id) {
        try {
            Book updated = bookService.changeBookStatus(id, BookStatus.APPROVED);
            BookResponse response = new BookResponse(updated);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Book not found with ID: " + id);
        }
    }

    @PatchMapping("/books/{id}/reject")
    @Operation(summary = "Reject a book", description = "Changes the status of a book to REJECTED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<?> rejectingBook(@PathVariable Long id) {
        try {
            Book updated = bookService.changeBookStatus(id, BookStatus.REJECTED);
            BookResponse response = new BookResponse(updated);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Book not found with ID: " + id);
        }
    }
}
