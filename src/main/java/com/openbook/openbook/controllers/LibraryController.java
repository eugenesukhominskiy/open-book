package com.openbook.openbook.controllers;

import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.services.LibraryService;
import com.openbook.openbook.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public LibraryController(LibraryService libraryService, MemberService memberService) {
        this.libraryService = libraryService;
        this.memberService = memberService;
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
        return ResponseEntity.ok(library);
    }

    @PostMapping("/{bookId}/purchase")
    @Operation(summary = "Purchase a book", description = "Add a book to the user's library.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully added to library"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Error adding the book")
    })
    private ResponseEntity<?> buyBook(@PathVariable Long bookId, Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        libraryService.add(member.get().getId(), bookId);

        return ResponseEntity.ok("Book successful add to the library");
    }

    @DeleteMapping("/{bookId}/remove")
    @Operation(summary = "Remove book from library", description = "Remove a book from the current user's library.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book removed from library"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Book not found in user's library")
    })
    public ResponseEntity<String> removeBookFromLibrary(@PathVariable Long bookId, Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        boolean removed = libraryService.remove(member.get().getId(), bookId);
        if (!removed) {
            return ResponseEntity.status(400).body("Book not found in the member's library");
        }

        return ResponseEntity.ok("Book removed from library");
    }
}
