package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.MemberRequest;
import com.openbook.openbook.DTO.MemberResponse;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> viewProfile(Principal principal) {
        String username = principal.getName();
        Optional<Member> optionalMember = memberService.findByUsername(username);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return ResponseEntity.ok(new MemberResponse(member));
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PatchMapping("/update")
    @Operation(summary = "Update user profile", description = "Updates the profile information of the currently logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateAccount(Principal principal, @RequestBody MemberRequest memberRequest) {
        String username = principal.getName();
        Optional<Member> member = memberService.findByUsername(username);

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Member updatedMember = memberService.update(memberRequest, member.get().getId());
        MemberResponse response = new MemberResponse(updatedMember);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete user account", description = "Deletes the account of the currently logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> deleteAccount(Principal principal) {
        String username = principal.getName();
        Optional<Member> member = memberService.findByUsername(username);

        if (member.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        memberService.delete(member.get().getId());
        return ResponseEntity.ok("Account deleted successfully!");
    }
}
