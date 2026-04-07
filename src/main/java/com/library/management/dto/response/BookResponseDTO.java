package com.library.management.dto.response;

import lombok.Data;

@Data
public class BookResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String category;
    private int totalCopies;
    private int availableCopies;
}
