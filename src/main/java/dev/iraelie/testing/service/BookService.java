package dev.iraelie.testing.service;

import dev.iraelie.testing.dtos.*;
import dev.iraelie.testing.exception.DuplicateResourceException;
import dev.iraelie.testing.exception.ResourceNotFoundException;
import dev.iraelie.testing.mapper.BookMapper;
import dev.iraelie.testing.model.Author;
import dev.iraelie.testing.model.Book;
import dev.iraelie.testing.model.Publisher;
import dev.iraelie.testing.repository.AuthorRepository;
import dev.iraelie.testing.repository.BookRepository;
import dev.iraelie.testing.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookMapper bookMapper;

    // 1. Create a new book
    @Transactional
    public Long createBook(CreateBookRequest request) {
        // Check if ISBN already exists
        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        // Validate author exists
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));

        // Validate publisher exists
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + request.getPublisherId()));

        // Create book
        Book book = bookMapper.toBook(request);
        book.setAuthor(author);
        book.setPublisher(publisher);

        return bookRepository.save(book).getId();
    }

    // 2. Get book by ID
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return bookMapper.toBookDTO(book);
    }

    // 3. Update book
    @Transactional
    public void updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // If publisher is being updated, validate it exists
        if (request.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + request.getPublisherId()));
            book.setPublisher(publisher);
        }

        bookMapper.updateBook(book, request);
        bookRepository.save(book);
    }

    // 4. Get all books by author
    public List<BookDTO> getBooksByAuthor(Long authorId) {
        // Validate author exists
        authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        return bookRepository.findAllByAuthorId(authorId)
                .stream()
                .map(bookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    // 5. Get author with all their books
    public AuthorDTO getAuthorWithBooks(Long authorId) {
        Author author = authorRepository.findByIdWithBooks(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        return bookMapper.toAuthorDTO(author);
    }

    // 6. Delete book
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}