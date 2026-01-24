package dev.iraelie.testing.repository;

import dev.iraelie.testing.model.Book;
import dev.iraelie.testing.model.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAllByAuthorId(Long authorId);

    List<Book> findAllByGenre(BookGenre genre);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAllAvailableBooks();
}