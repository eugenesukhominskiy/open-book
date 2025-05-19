package com.openbook.openbook.services;

import com.openbook.openbook.model.Book;
import com.openbook.openbook.model.Member;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LibraryService libraryService;

    private Member testMember;
    private Book testBook;
    private final Long memberId = 1L;
    private final Long bookId = 1L;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(memberId);
        testMember.setLibrary(new ArrayList<>());

        testBook = new Book();
        testBook.setId(bookId);
        testBook.setTitle("Test Book");
    }

    @Test
    void add_WhenBookNotInLibrary_AddsBook() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // Act
        libraryService.add(memberId, bookId);

        // Assert
        assertTrue(testMember.getLibrary().contains(testBook));
        verify(memberRepository).save(testMember);
    }

    @Test
    void add_WhenBookAlreadyInLibrary_DoesNotAddAgain() {
        // Arrange
        testMember.getLibrary().add(testBook);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // Act
        libraryService.add(memberId, bookId);

        // Assert
        assertEquals(1, testMember.getLibrary().size());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void add_WhenBookNotFound_ThrowsException() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                libraryService.add(memberId, bookId)
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void add_WhenMemberNotFound_ThrowsException() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                libraryService.add(memberId, bookId)
        );
    }

    @Test
    void remove_WhenBookInLibrary_RemovesBook() {
        // Arrange
        testMember.getLibrary().add(testBook);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // Act
        boolean result = libraryService.remove(memberId, bookId);

        // Assert
        assertTrue(result);
        assertFalse(testMember.getLibrary().contains(testBook));
        verify(memberRepository).save(testMember);
    }

    @Test
    void remove_WhenBookNotInLibrary_ReturnsFalse() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // Act
        boolean result = libraryService.remove(memberId, bookId);

        // Assert
        assertFalse(result);
        verify(memberRepository, never()).save(any());
    }

    @Test
    void getLibrary_ReturnsMemberLibrary() {
        // Arrange
        testMember.getLibrary().add(testBook);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // Act
        List<Book> result = libraryService.getLibrary(memberId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void getLibrary_WhenMemberNotFound_ThrowsException() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                libraryService.getLibrary(memberId)
        );
    }
}