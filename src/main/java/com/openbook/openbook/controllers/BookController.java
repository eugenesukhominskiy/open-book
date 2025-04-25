package com.openbook.openbook.controllers;

import com.openbook.openbook.models.Book;
import com.openbook.openbook.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    private ResponseEntity<?> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre
    ) {
        List<Book> books = bookService.searchBooks(title, author, genre);
        return ResponseEntity.ok(books);
        // TODO: Fix a endpoint
    }

    @GetMapping("/{bookId}")
    private ResponseEntity<?> getBookProfile(@PathVariable Long bookId) {
        Optional<Book> book = bookService.findById(bookId);

        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }
}
