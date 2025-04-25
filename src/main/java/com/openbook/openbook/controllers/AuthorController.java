package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.BookDTO;
import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.services.BookService;
import com.openbook.openbook.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/author")
public class AuthorController {
    private final BookService bookService;
    private final MemberService memberService;

    @Autowired
    public AuthorController(BookService bookService, MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @PostMapping("/create")
    private ResponseEntity<?> createNewBook(@RequestBody BookDTO bookDTO) {
        try {
            Book newBook = bookService.create(bookDTO);
            return ResponseEntity.ok(newBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book not created: " + e.getMessage());
        }
    }

    @GetMapping("/mine")
    private ResponseEntity<?> viewOwnWorks(Principal principal) {
        Optional<Member> author = memberService.findByUsername(principal.getName());

        if (author.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        List<Book> works = bookService.findAllAuthorBooks(author.get().getId());

        return ResponseEntity.ok(works);
    }

    @PatchMapping("/{workID}")
    private ResponseEntity<?> editBook(@PathVariable Long workID, @RequestBody BookDTO bookDTO, Principal principal) {
        String username = principal.getName();

        try {
            Book book = bookService.update(workID, bookDTO, username);
            return ResponseEntity.ok(book);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body("You are not allowed to edit this book.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Book not found.");
        }
    }

    @GetMapping("/status")
    private ResponseEntity<?> getStatusOfBooks(Principal principal){
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
