package com.openbook.openbook.controllers;

import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.services.LibraryService;
import com.openbook.openbook.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService libraryService;
    private final MemberService memberService;

    @Autowired
    public LibraryController(LibraryService libraryService, MemberService memberService) {
        this.libraryService = libraryService;
        this.memberService = memberService;
    }

    @GetMapping()
    public ResponseEntity<?> getMemberLibrary(Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        List<Book> library = libraryService.getLibrary(member.get().getId());
        return ResponseEntity.ok(library);
    }

    @PostMapping("/{bookId}/purchase")
    private ResponseEntity<?> buyBook(@PathVariable Long bookId, Principal principal) {
        Optional<Member> member = memberService.findByUsername(principal.getName());

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("Member is not found");
        }

        libraryService.add(member.get().getId(), bookId);

        return ResponseEntity.ok("Book successful add to the library");
    }

    @DeleteMapping("/{bookId}/remove")
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
