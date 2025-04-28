package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.MemberDTO;
import com.openbook.openbook.models.Member;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
@Tag(name = "Account", description = "Controller for managing user account operation")
public class AccountController {
    private final MemberService memberService;

    @Autowired
    public AccountController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    @Operation(summary = "View current user profile", description = "Returns the profile information of the currently logged-in user.")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Update user profile", description = "Updates the profile information of the currently logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Delete user account", description = "Deletes the account of the currently logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
