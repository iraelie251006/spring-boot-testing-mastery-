package dev.iraelie.testing.service;

import dev.iraelie.testing.dtos.AuthorDTO;
import dev.iraelie.testing.dtos.BookDTO;
import dev.iraelie.testing.dtos.CreateBookRequest;
import dev.iraelie.testing.dtos.UpdateBookRequest;
import dev.iraelie.testing.exception.DuplicateResourceException;
import dev.iraelie.testing.exception.ResourceNotFoundException;
import dev.iraelie.testing.mapper.BookMapper;
import dev.iraelie.testing.model.Author;
import dev.iraelie.testing.model.Book;
import dev.iraelie.testing.model.BookGenre;
import dev.iraelie.testing.model.Publisher;
import dev.iraelie.testing.repository.AuthorRepository;
import dev.iraelie.testing.repository.BookRepository;
import dev.iraelie.testing.repository.PublisherRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Book Service tests")
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Author author;
    private Publisher publisher;
    private Book book;
    private BookDTO bookDTO;
    private AuthorDTO authorDTO;
    private CreateBookRequest createBookRequest;
    private UpdateBookRequest updateBookRequest;

    @BeforeEach
    void setup() {
        this.author = Author.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .country("Rwanda")
                .books(new ArrayList<>())
                .build();

        this.publisher = Publisher.builder()
                .id(3L)
                .name("Amazon")
                .address("USA")
                .website("https://amazon.com")
                .books(new ArrayList<>())
                .build();

        this.book = Book.builder()
                .id(1L)
                .title("Master System design")
                .isbn("5155172381")
                .publicationYear(2026)
                .availableCopies(200)
                .price(49.9)
                .genre(BookGenre.valueOf("SCIENCE"))
                .author(this.author)
                .publisher(this.publisher)
                .build();

        this.authorDTO = AuthorDTO.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .country("Rwanda")
                .totalBooks(20)
                .build();

        this.bookDTO = BookDTO.builder()
                .id(1L)
                .title("Master System design")
                .isbn("5155172381")
                .publicationYear(2026)
                .availableCopies(200)
                .price(49.9)
                .genre(BookGenre.valueOf("SCIENCE"))
                .authorName(this.author.getFirstName() + this.author.getLastName())
                .publisherName(this.publisher.getName())
                .build();

        this.createBookRequest = CreateBookRequest.builder()
                .title("Master System design")
                .isbn("5155172381")
                .publicationYear(2026)
                .availableCopies(200)
                .price(49.9)
                .genre(BookGenre.valueOf("SCIENCE"))
                .authorId(this.author.getId())
                .publisherId(this.publisher.getId())
                .build();

        this.updateBookRequest = UpdateBookRequest.builder()
                .title("Master System design and DSA")
                .availableCopies(100)
                .price(99.9)
                .publisherId(this.publisher.getId())
                .build();
    }

    @Nested
    @DisplayName("Create Book validation tests")
    class CreateBookValidationTests {
        @Test
        @DisplayName("Validate Isbn does already exists")
        void validateIsbnDoesAlreadyExist() {
            // Given
            CreateBookRequest request = BookServiceTest.this.createBookRequest;
            when(BookServiceTest.this.bookRepository.findByIsbn(request.getIsbn()))
                    .thenReturn(Optional.of(BookServiceTest.this.book));
            // When
            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> BookServiceTest.this.bookService.createBook(request)
                    );
            // Then
            assertEquals("Book with ISBN " + request.getIsbn() + " already exists", exception.getMessage());

            verify(BookServiceTest.this.bookRepository, times(1)).findByIsbn(request.getIsbn());
            verifyNoInteractions(BookServiceTest.this.authorRepository);
            verifyNoInteractions(BookServiceTest.this.publisherRepository);
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }

        @Test
        @DisplayName("Validate If Author Exists")
        void validateIfAuthorExists() {
            CreateBookRequest request = BookServiceTest.this.createBookRequest;
            when(bookRepository.findByIsbn(request.getIsbn()))
                    .thenReturn(Optional.empty());

            when(BookServiceTest.this.authorRepository.findById(request.getAuthorId()))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.createBook(request)
            );

            assertEquals("Author not found with id: " + request.getAuthorId(), exception.getMessage());
            verify(BookServiceTest.this.bookRepository).findByIsbn(request.getIsbn());
            verify(BookServiceTest.this.authorRepository, times(1)).findById(request.getAuthorId());
            verifyNoInteractions(BookServiceTest.this.publisherRepository);
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }

        @Test
        @DisplayName("Validate If Publisher Exists")
        void validateIfPublisherExists() {
            CreateBookRequest request = BookServiceTest.this.createBookRequest;
            when(bookRepository.findByIsbn(request.getIsbn()))
                    .thenReturn(Optional.empty());

            when(authorRepository.findById(request.getAuthorId()))
                    .thenReturn(Optional.of(author));

            when(BookServiceTest.this.publisherRepository.findById(request.getPublisherId()))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.createBook(request)
            );

            assertEquals("Publisher not found with id: " + request.getPublisherId(), exception.getMessage());
            verify(BookServiceTest.this.bookRepository).findByIsbn(request.getIsbn());
            verify(BookServiceTest.this.authorRepository).findById(request.getAuthorId());
            verify(BookServiceTest.this.publisherRepository, times(1)).findById(request.getPublisherId());
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }
    }

    @Nested
    @DisplayName("Get Book By Id Validation Tests")
    class GetBookByIdValidationTests {
        @Test
        @DisplayName("Get Book by id test")
        void getBookById() {
            // Given
            Long bookId = 1L;
            when(BookServiceTest.this.bookRepository.findById(bookId))
                    .thenReturn(Optional.empty());
            // When
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.getBookById(bookId)
            );
            // Then
            assertEquals("Book not found with id: " + 1L, exception.getMessage());
            verify(BookServiceTest.this.bookRepository).findById(bookId);
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }
    }

    @Nested
    @DisplayName("Updates book detail tests")
    class UpdatesBookDetailTests {
        @Test
        @DisplayName("Validates book exists")
        void shouldThrowErrorIfNotExists() {
            Long bookId = 1L;
            UpdateBookRequest request = BookServiceTest.this.updateBookRequest;
            when(BookServiceTest.this.bookRepository.findById(bookId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.updateBook(bookId, request)
            );

            assertEquals("Book not found with id: " + bookId, exception.getMessage());
            verify(BookServiceTest.this.bookRepository).findById(bookId);
            verifyNoInteractions(BookServiceTest.this.publisherRepository);
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }

        @Test
        @DisplayName("Validates publisher if being updated")
        void shouldUpdatePublisher() {
            Long bookId = 1L;
            UpdateBookRequest request = BookServiceTest.this.updateBookRequest;
            when(BookServiceTest.this.bookRepository.findById(bookId))
                    .thenReturn(Optional.of(BookServiceTest.this.book));
            when(BookServiceTest.this.publisherRepository.findById(request.getPublisherId()))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.updateBook(bookId, request)
            );

            assertNotNull(request.getPublisherId());
            assertEquals("Publisher not found with id: " + request.getPublisherId(), exception.getMessage());
            assertEquals(book.getPublisher().getName(), publisher.getName());
            verify(BookServiceTest.this.bookRepository).findById(bookId);
            verify(BookServiceTest.this.publisherRepository).findById(request.getPublisherId());
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }
    }

    @Nested
    @DisplayName("Gets all books by an author")
    class GetBooksByAuthorTests {
        @Test
        @DisplayName("Validate author exists")
        void validateIfAuthorExists() {
            Long authorId = 1L;

            when(BookServiceTest.this.authorRepository.findById(authorId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.getBooksByAuthor(authorId)
            );

            assertEquals("Author not found with id: " + authorId, exception.getMessage());
            verify(BookServiceTest.this.authorRepository, times(1)).findById(authorId);
            verifyNoInteractions(BookServiceTest.this.bookRepository);
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }
    }

    @Nested
    @DisplayName("Gets author with their book count tests")
    class GetAuthorWithBooksTests {
        @Test
        @DisplayName("Throws exception when author does not exist")
        void shouldThrowWhenAuthorDoesNotExist() {
            Long authorId = 1L;

            when(BookServiceTest.this.authorRepository.findByIdWithBooks(authorId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.getAuthorWithBooks(authorId)
            );

            assertEquals("Author not found with id: " + authorId, exception.getMessage());
            verify(BookServiceTest.this.authorRepository, times(1)).findByIdWithBooks(authorId);
            verifyNoInteractions(BookServiceTest.this.bookMapper);
        }

        @Test
        @DisplayName("Validate author book counts")
        void shouldReturnAuthorWithBookCount() {
            Long authorId = 1L;

            when(BookServiceTest.this.authorRepository.findByIdWithBooks(authorId))
                    .thenReturn(Optional.of(BookServiceTest.this.author));

            when(BookServiceTest.this.bookMapper.toAuthorDTO(BookServiceTest.this.author))
                    .thenReturn(BookServiceTest.this.authorDTO);

            AuthorDTO authorBookCounts = BookServiceTest.this.bookService.getAuthorWithBooks(authorId);

            assertEquals(20, authorBookCounts.getTotalBooks());
            verify(BookServiceTest.this.authorRepository).findByIdWithBooks(authorId);
            verify(BookServiceTest.this.bookMapper).toAuthorDTO(author);
        }
    }

    @Nested
    @DisplayName("Delete book tests")
    class DeleteBookTests {
        @Test
        @DisplayName("Check if book exists if not throw an exception")
        void shouldThrowExceptionExistsIfBookNotExists() {
            Long bookId = 1L;
            when(BookServiceTest.this.bookRepository.existsById(bookId))
                    .thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> BookServiceTest.this.bookService.deleteBook(bookId)
            );

            assertEquals("Book not found with id: " + bookId, exception.getMessage());
            verify(BookServiceTest.this.bookRepository).existsById(bookId);
            verify(BookServiceTest.this.bookRepository, never()).deleteById(bookId);
        }

        @Test
        @DisplayName("Should delete the book")
        void shouldDeleteTheBook() {
            Long bookId = 1L;
            when(BookServiceTest.this.bookRepository.existsById(bookId))
                    .thenReturn(true);

            assertDoesNotThrow(
                    () -> BookServiceTest.this.bookService.deleteBook(bookId)
            );

            verify(BookServiceTest.this.bookRepository).existsById(bookId);
            verify(BookServiceTest.this.bookRepository).deleteById(bookId);
        }
    }
}