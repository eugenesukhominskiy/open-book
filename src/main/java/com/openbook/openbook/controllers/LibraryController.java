package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.BookService;
import com.openbook.openbook.services.LibraryService;
import com.openbook.openbook.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/library")
@Tag(name = "Library Management", description = "Endpoints for managing user library")
public class LibraryController {
    private final LibraryService libraryService;
    private final MemberService memberService;
    private final BookService bookService;

    @Autowired
    public LibraryController(LibraryService libraryService, MemberService memberService, BookService bookService) {
        this.libraryService = libraryService;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping()
    @Operation(summary = "Get user's library", description = "Retrieve all books in the current user's library.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Library retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getMemberLibrary(Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        List<Book> library = libraryService.getLibrary(member.get().getId());
        List<BookResponse> response = library.stream()
                .map(BookResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookId}/purchase")
    @Operation(summary = "Purchase a book", description = "Add a book to the user's library.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully added to library"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Error adding the book")
    })
    public ResponseEntity<?> buyBook(@PathVariable Long bookId, Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }

        Optional<Book> searchBook = bookService.findById(bookId);
        if (searchBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with id " + bookId + " not found");
        }

        try {
            libraryService.add(member.get().getId(), searchBook.get().getId());
            return ResponseEntity.ok("Book successfully added to the library");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding the book: " + e.getMessage());
        }
    }


    @DeleteMapping("/{bookId}/remove")
    @Operation(summary = "Remove book from library", description = "Remove a book from the current user's library.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book removed from library"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Book not found in user's library")
    })
    public ResponseEntity<String> removeBookFromLibrary(@PathVariable Long bookId, Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());
        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        Optional<Book> book = bookService.findById(bookId);
        if (book.isEmpty()) {
            return ResponseEntity.status(404).body("Book with id " + bookId + " not found");
        }

        boolean removed = libraryService.remove(member.get().getId(), book.get().getId());
        if (!removed) {
            return ResponseEntity.status(400).body("Book not found in the member's library");
        }

        return ResponseEntity.ok("Book removed from library");
    }
}
