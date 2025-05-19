package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookResponse;
import com.openbook.openbook.model.Book;
import com.openbook.openbook.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Management", description = "Endpoints for managing books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search for books based on title, author, and genre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books found"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<?> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre
    ) {
        List<Book> books = bookService.searchBooks(title, author, genre);

        List<BookResponse> response = books.stream()
                .map(BookResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Get book details", description = "Retrieve the details of a specific book by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<?> getBookProfile(@PathVariable Long bookId) {
        Optional<Book> book = bookService.findById(bookId);

        if (book.isPresent()) {
            BookResponse response = new BookResponse(book.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }
}
