package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.services.BookService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @Schema(example = "book_title")
    private String title;
    @Schema(example = "book_description")
    private String description;
    @Schema(example = "200000")
    private Long characters;
    @Schema(example = "400")
    private Integer price;
    @Schema(example = "PENDING")
    private BookStatus status;
    @Schema(example = "3")
    private Long genreId;
    @Schema(example = "3")
    private Long authorId;
}
