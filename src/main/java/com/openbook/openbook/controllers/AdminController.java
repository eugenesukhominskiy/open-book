package com.openbook.openbook.controllers;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.models.Member;
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
    private ResponseEntity<?> viewAllUsers() {
        List<Member> allMember = memberService.findAllMembers();
        // TODO: Fix json output
        return ResponseEntity.ok(allMember);
    }

    @GetMapping("/books/pending")
    @Operation(summary = "Get all books pending moderation", description = "Retrieves a list of all books that have not yet been approved.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending books retrieved successfully")
    })
    private ResponseEntity<?> viewAllModerationBook() {
        return ResponseEntity.ok(bookService.getAllNotVerifiedBooks());
    }

    @PatchMapping("/books/{id}/approve")
    @Operation(summary = "Approve a book", description = "Changes the status of a book to APPROVED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book approved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    private ResponseEntity<?> approvingBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.changeBookStatus(id, BookStatus.APPROVED));
    }

    @PatchMapping("/books/{id}/reject")
    @Operation(summary = "Reject a book", description = "Changes the status of a book to REJECTED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    private ResponseEntity<?> rejectingBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.changeBookStatus(id, BookStatus.REJECTED));
    }
}
