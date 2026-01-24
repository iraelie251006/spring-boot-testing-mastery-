package dev.iraelie.testing.mapper;

import dev.iraelie.testing.dtos.AuthorDTO;
import dev.iraelie.testing.dtos.BookDTO;
import dev.iraelie.testing.dtos.CreateBookRequest;
import dev.iraelie.testing.dtos.UpdateBookRequest;
import dev.iraelie.testing.model.Author;
import dev.iraelie.testing.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toBook(CreateBookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publicationYear(request.getPublicationYear())
                .availableCopies(request.getAvailableCopies())
                .price(request.getPrice())
                .genre(request.getGenre())
                .build();
    }

    public BookDTO toBookDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .availableCopies(book.getAvailableCopies())
                .price(book.getPrice())
                .genre(book.getGenre())
                .authorName(book.getAuthor() != null ?
                        book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName() : null)
                .publisherName(book.getPublisher() != null ? book.getPublisher().getName() : null)
                .build();
    }

    public AuthorDTO toAuthorDTO(Author author) {
        return AuthorDTO.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .email(author.getEmail())
                .country(author.getCountry())
                .totalBooks(author.getBooks() != null ? author.getBooks().size() : 0)
                .build();
    }

    public void updateBook(Book book, UpdateBookRequest request) {
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAvailableCopies() != null) {
            book.setAvailableCopies(request.getAvailableCopies());
        }
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }
    }
}