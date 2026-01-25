package dev.iraelie.testing.service;

import dev.iraelie.testing.dtos.AuthorDTO;
import dev.iraelie.testing.dtos.BookDTO;
import dev.iraelie.testing.dtos.CreateBookRequest;
import dev.iraelie.testing.dtos.UpdateBookRequest;
import dev.iraelie.testing.mapper.BookMapper;
import dev.iraelie.testing.model.Author;
import dev.iraelie.testing.model.Book;
import dev.iraelie.testing.model.Publisher;
import dev.iraelie.testing.repository.AuthorRepository;
import dev.iraelie.testing.repository.BookRepository;
import dev.iraelie.testing.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    }
}