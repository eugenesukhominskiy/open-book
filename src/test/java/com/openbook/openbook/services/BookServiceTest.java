package com.openbook.openbook.services;

import com.openbook.openbook.DTO.BookDTO;
import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.models.Book;
import com.openbook.openbook.models.Genre;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.BookRepository;
import com.openbook.openbook.repository.GenreRepository;
import com.openbook.openbook.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void create_ShouldSaveBookWithCorrectFields_WhenDataIsValid() {
        // given
        Long genreId = 1L;
        Long authorId = 2L;

        Genre genre = new Genre();
        genre.setId(genreId);

        Member author = new Member();
        author.setId(authorId);

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Test Title");
        bookDTO.setDescription("Test Description");
        bookDTO.setCharacters(180000L);
        bookDTO.setPrice(280);
        bookDTO.setGenreId(genreId);
        bookDTO.setAuthorId(authorId);

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(memberRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Book result = bookService.create(bookDTO);

        // then
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(180000L, result.getCharacters());
        assertEquals(280, result.getPrice());
        assertEquals(BookStatus.PENDING, result.getStatus());
        assertEquals(genre, result.getGenre());
        assertEquals(author, result.getAuthor());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void create_ShouldThrowException_WhenGenreNotFound() {
        // given
        BookDTO bookDTO = new BookDTO();
        bookDTO.setGenreId(1L);
        bookDTO.setAuthorId(2L);

        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookService.create(bookDTO));

        assertEquals("Genre not found", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenAuthorNotFound() {
        // given
        Genre genre = new Genre();
        genre.setId(1L);

        BookDTO bookDTO = new BookDTO();
        bookDTO.setGenreId(1L);
        bookDTO.setAuthorId(2L);

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(memberRepository.findById(2L)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookService.create(bookDTO));

        assertEquals("Author not found", exception.getMessage());
    }

    @Test
    void viewBook_ShouldReturnBook_WhenBookExists() {
        // given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // when
        Book foundBook = bookService.viewBook(bookId);

        // then
        assertNotNull(foundBook);
        assertEquals(bookId, foundBook.getId());
        verify(bookRepository).findById(bookId);
    }

    @Test
    void viewBook_ShouldReturnNull_WhenBookNotFound() {
        // given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when
        Book foundBook = bookService.viewBook(bookId);

        // then
        assertNull(foundBook);
        verify(bookRepository).findById(bookId);
    }

    @Test
    void update_ShouldUpdateBook_WhenAuthorIsCorrect() {
        // given
        Long bookId = 1L;
        String username = "author1";
        Book book = new Book();
        book.setId(bookId);

        Member author = new Member();
        author.setUsername(username);
        book.setAuthor(author);

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Title");
        bookDTO.setDescription("New Description");
        bookDTO.setCharacters(200000L);
        bookDTO.setPrice(220);
        bookDTO.setGenreId(1L);

        Genre genre = new Genre();
        genre.setId(1L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Book updatedBook = bookService.update(bookId, bookDTO, username);

        // then
        assertEquals("New Title", updatedBook.getTitle());
        assertEquals("New Description", updatedBook.getDescription());
        assertEquals(200000, updatedBook.getCharacters());
        assertEquals(220, updatedBook.getPrice());
        assertEquals(genre, updatedBook.getGenre());
        verify(bookRepository).save(updatedBook);
    }

    @Test
    void update_ShouldThrowAccessDeniedException_WhenAuthorIsIncorrect() {
        // given
        Long bookId = 1L;
        String username = "anotherUser"; // Користувач, який не є автором
        Book book = new Book();
        book.setId(bookId);
        Member author = new Member();
        author.setUsername("author1");
        book.setAuthor(author); // Автор книги

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Title");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // when + then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> bookService.update(bookId, bookDTO, username));
        assertEquals("You are not allowed to update this book.", exception.getMessage());

        verify(bookRepository, never()).save(any(Book.class)); // збереження не викликається
    }

    @Test
    void update_ShouldThrowRuntimeException_WhenGenreNotFound() {
        // given
        Long bookId = 1L;
        String username = "author1";
        Book book = new Book();
        book.setId(bookId);

        Member author = new Member();
        author.setUsername(username);
        book.setAuthor(author); // Автор книги

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Title");
        bookDTO.setGenreId(1L); // Зазначений жанр, якого не існує

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        // when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.update(bookId, bookDTO, username));
        assertEquals("Genre not found", exception.getMessage());

        verify(bookRepository, never()).save(any(Book.class)); // збереження не викликається
    }

    @Test
    void delete_ShouldDeleteBook_WhenBookExists() {
        // given
        Long bookId = 1L;

        // mock
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // when
        bookService.delete(bookId);

        // then
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void delete_ShouldThrowException_WhenBookDoesNotExist() {
        // given
        Long bookId = 1L;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        // when + then
        assertThrows(RuntimeException.class, () -> bookService.delete(bookId));
    }

    @Test
    void findAllAuthorBooks_ShouldReturnBooks_WhenAuthorExists() {
        // given
        Long authorId = 1L;

        // створюємо список книг для автора
        Book book1 = new Book();
        book1.setId(1L);
        Member author = new Member();
        author.setUsername("author1");

        book1.setAuthor(author);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setAuthor(author );

        List<Book> books = Arrays.asList(book1, book2);

        // mock для репозиторію
        when(bookRepository.findAllByAuthor_Id(authorId)).thenReturn(books);

        // when
        List<Book> result = bookService.findAllAuthorBooks(authorId);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(book1));
        assertTrue(result.contains(book2));
        verify(bookRepository).findAllByAuthor_Id(authorId); // перевірка виклику методу репозиторію
    }

    @Test
    void getAllNotVerifiedBooks_ShouldReturnBooksWithNonApprovedStatus() {
        // given
        Book book1 = new Book();
        book1.setId(1L);
        book1.setStatus(BookStatus.PENDING);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setStatus(BookStatus.PENDING);

        Book book3 = new Book();
        book3.setId(3L);
        book3.setStatus(BookStatus.APPROVED);

        List<Book> books = Arrays.asList(book1, book2, book3);

        // mock
        when(bookRepository.findAllByStatusNot(BookStatus.APPROVED)).thenReturn(Arrays.asList(book1, book2));

        // when
        List<Book> result = bookService.getAllNotVerifiedBooks();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(book1));
        assertTrue(result.contains(book2));
        assertFalse(result.contains(book3));
        verify(bookRepository).findAllByStatusNot(BookStatus.APPROVED);
    }

    @Test
    void changeBookStatus_ShouldChangeStatus_WhenBookExists() {
        // given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.PENDING);

        BookStatus newStatus = BookStatus.APPROVED;

        // mock для репозиторію
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Book updatedBook = bookService.changeBookStatus(bookId, newStatus);

        // then
        assertEquals(newStatus, updatedBook.getStatus());
        verify(bookRepository).save(book);
    }

    @Test
    void findById_ShouldReturnBook_WhenBookExists() {
        // given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");

        // mock для репозиторію
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // when
        Optional<Book> result = bookService.findById(bookId);

        // then
        assertTrue(result.isPresent()); // Перевіряємо, що книга знайдена
        assertEquals(book, result.get()); // Перевіряємо, що повернута книга — це та сама книга
    }

    @Test
    void findById_ShouldReturnEmpty_WhenBookNotFound() {
        // given
        Long bookId = 1L;

        // mock для репозиторію
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when
        Optional<Book> result = bookService.findById(bookId);

        // then
        assertFalse(result.isPresent()); // Перевіряємо, що книга не знайдена
    }


}
