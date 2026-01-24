package dev.iraelie.testing.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private Integer totalBooks;
}