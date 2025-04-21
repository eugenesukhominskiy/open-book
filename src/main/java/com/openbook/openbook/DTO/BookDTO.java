package com.openbook.openbook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String title;
    private String description;
    private Long characters;
    private Integer price;
    private Long genreId;
    private Long authorId;
}
