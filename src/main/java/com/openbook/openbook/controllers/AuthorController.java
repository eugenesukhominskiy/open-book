package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookRequest;
import com.openbook.openbook.DTO.BookResponse;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/author")
@Tag(name = "Author", description = "Endpoints for authors to manage their books")
public class AuthorController {
    private final BookService bookService;
    private final MemberService memberService;

    @Autowired
    public AuthorController(BookService bookService, MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new book", description = "Allows an author to create a new book by providing necessary details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid book data provided")
    })
    public ResponseEntity<?> createNewBook(Principal principal, @RequestBody BookRequest request) {
        Optional<Member> author = memberService.findByUsername(principal.getName());

        if (author.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        try {
            Book newBook = bookService.create(request, author.get().getId());
            BookResponse response = new BookResponse(newBook);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book not created: " + e.getMessage());
        }
    }

    @GetMapping("/mine")
    @Operation(summary = "View books by the author", description = "Returns a list of all books written by the currently logged-in author.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of author's books"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<?> viewOwnWorks(Principal principal) {
        Optional<Member> author = memberService.findByUsername(principal.getName());

        if (author.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        List<Book> works = bookService.findAllAuthorBooks(author.get().getId());

        List<BookResponse> list = works.stream()
                .map(BookResponse::new)
                .toList();

        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{workId}")
    @Operation(summary = "Edit an existing book", description = "Allows the author to edit details of a book they have written.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully updated"),
            @ApiResponse(responseCode = "403", description = "Access denied - user is not the author of the book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<?> editBook(@PathVariable Long workId, @RequestBody BookRequest request, Principal principal) {
        String username = principal.getName();

        try {
            Book book = bookService.update(workId, request, username);
            BookResponse response = new BookResponse(book);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body("You are not allowed to edit this book.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Book not found.");
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get status of author's books", description = "Returns a list of books written by the author along with their approval status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of books and their statuses"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<?> getStatusOfBooks(Principal principal){
        String username = principal.getName();
        Optional<Member> author = memberService.findByUsername(username);

        if (author.isEmpty()) {
            return ResponseEntity.status(404).body("Author not found");
        }

        List<Book> books = bookService.findAllAuthorBooks(author.get().getId());

        List<Map<String, Object>> statusList = books.stream()
                .map(book -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", book.getId());
                    map.put("title", book.getTitle());
                    map.put("status", book.getStatus().toString());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(statusList);
    }
}
