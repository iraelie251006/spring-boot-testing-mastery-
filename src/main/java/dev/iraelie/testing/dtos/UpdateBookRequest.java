package dev.iraelie.testing.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookRequest {
    private String title;
    private Integer availableCopies;
    private Double price;
    private Long publisherId;
}