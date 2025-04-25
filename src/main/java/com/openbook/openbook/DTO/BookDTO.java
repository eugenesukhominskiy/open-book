package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.services.BookService;
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
    private BookStatus status;
    private Long genreId;
    private Long authorId;
}
