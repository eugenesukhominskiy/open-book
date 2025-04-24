package com.openbook.openbook.controllers;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.services.BookService;
import com.openbook.openbook.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final MemberService memberService;
    private final BookService bookService;

    @Autowired
    public AdminController(MemberService memberService, BookService bookService) {
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping("/users")
    private ResponseEntity<?> viewAllUsers() {
        List<Member> allMember = memberService.findAllMembers();
        // TODO: Fix json output
        return ResponseEntity.ok(allMember);
    }

    @GetMapping("/books/pending")
    private ResponseEntity<?> viewAllModerationBook() {
        return ResponseEntity.ok(bookService.getAllNotVerifiedBooks());
    }

    @PatchMapping("/books/{id}/approve")
    private ResponseEntity<?> approvingBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.changeBookStatus(id, BookStatus.APPROVED));
    }

    @PatchMapping("/books/{id}/reject")
    private ResponseEntity<?> rejectingBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.changeBookStatus(id, BookStatus.REJECTED));
    }
}
