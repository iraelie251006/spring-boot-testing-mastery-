package dev.iraelie.testing.dtos;

import dev.iraelie.testing.model.BookGenre;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookRequest {
    private String title;
    private String isbn;
    private Integer publicationYear;
    private Integer availableCopies;
    private Double price;
    private BookGenre genre;
    private Long authorId;
    private Long publisherId;
}