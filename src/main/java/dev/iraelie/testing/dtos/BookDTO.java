package dev.iraelie.testing.dtos;

import dev.iraelie.testing.model.BookGenre;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private Integer availableCopies;
    private Double price;
    private BookGenre genre;
    private String authorName;
    private String publisherName;
}