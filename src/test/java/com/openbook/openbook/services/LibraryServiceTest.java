package com.openbook.openbook.services;

import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.BookRepository;
import com.openbook.openbook.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void add_ShouldAddBookToLibrary_WhenNotAlreadyPresent() {
        Long memberId = 1L;
        Long bookId = 2L;

        Member member = new Member();
        member.setId(memberId);
        member.setLibrary(new ArrayList<>());

        Book book = new Book();
        book.setId(bookId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        libraryService.add(memberId, bookId);

        assertTrue(member.getLibrary().contains(book));
        verify(memberRepository).save(member);
    }

    @Test
    void add_ShouldNotAddBook_WhenAlreadyPresent() {
        Long memberId = 1L;
        Long bookId = 2L;

        Book book = new Book();
        book.setId(bookId);

        Member member = new Member();
        member.setId(memberId);
        member.setLibrary(new ArrayList<>(List.of(book)));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        libraryService.add(memberId, bookId);

        assertEquals(1, member.getLibrary().size());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void remove_ShouldRemoveBook_WhenPresent() {
        Long memberId = 1L;
        Long bookId = 2L;

        Book book = new Book();
        book.setId(bookId);

        Member member = new Member();
        member.setId(memberId);
        member.setLibrary(new ArrayList<>(List.of(book)));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        boolean removed = libraryService.remove(memberId, bookId);

        assertTrue(removed);
        assertFalse(member.getLibrary().contains(book));
        verify(memberRepository).save(member);
    }

    @Test
    void remove_ShouldReturnFalse_WhenBookNotInLibrary() {
        Long memberId = 1L;
        Long bookId = 2L;

        Book book = new Book();
        book.setId(bookId);

        Member member = new Member();
        member.setId(memberId);
        member.setLibrary(new ArrayList<>()); // пусто

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        boolean removed = libraryService.remove(memberId, bookId);

        assertFalse(removed);
        verify(memberRepository, never()).save(any());
    }

    @Test
    void getLibrary_ShouldReturnBooks_WhenMemberExists() {
        Long memberId = 1L;

        Book book1 = new Book(); book1.setId(1L);
        Book book2 = new Book(); book2.setId(2L);

        Member member = new Member();
        member.setId(memberId);
        member.setLibrary(List.of(book1, book2));

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        List<Book> library = libraryService.getLibrary(memberId);

        assertEquals(2, library.size());
        assertTrue(library.contains(book1));
        assertTrue(library.contains(book2));
    }

    @Test
    void getLibrary_ShouldThrow_WhenMemberNotFound() {
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> libraryService.getLibrary(memberId));
    }
}
