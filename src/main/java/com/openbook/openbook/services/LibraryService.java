package com.openbook.openbook.services;

import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.BookRepository;
import com.openbook.openbook.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LibraryService(MemberRepository memberRepository, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    // Add book to the library
    public void add(Long memberId, Long bookId) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!member.getLibrary().contains(book)) {
            member.getLibrary().add(book);
            memberRepository.save(member);
        }
    }

    // Remove book from the library
    public boolean remove(Long memberId, Long bookId) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getLibrary().contains(book)) {
            member.getLibrary().remove(book);
            memberRepository.save(member);
            return true;
        }

        return false;
    }

    // Get books from current user`s library
    public List<Book> getLibrary(Long memberId) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return member.getLibrary();
    }
}
