package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.MemberDTO;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final MemberService memberService;

    @Autowired
    public AccountController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    private ResponseEntity<?> viewProfile(Principal principal) {
        String username = principal.getName();
        Optional<Member> member = memberService.findByUsername(username);

        if (member.isPresent()) {
            return ResponseEntity.ok(member.get());
        } else {
            return ResponseEntity.status(404).body("User don`t found");
        }
    }

    @PatchMapping("/update")
    private ResponseEntity<?> updateAccount(Principal principal, @RequestBody MemberDTO memberDTO) {
        String username = principal.getName();
        Optional<Member> member = memberService.findByUsername(username);

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Member currentMember = member.get();
        Member updatedMember = memberService.update(memberDTO, currentMember.getId());

        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/delete")
    private ResponseEntity<?> deleteAccount(Principal principal) {
        String username = principal.getName();
        Optional<Member> member = memberService.findByUsername(username);

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        memberService.delete(member.get().getId());
        return ResponseEntity.ok("Account deleted successfully!");
    }
}
