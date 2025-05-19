package com.openbook.openbook.DTO;

import com.openbook.openbook.enums.BookStatus;
import com.openbook.openbook.model.Book;
import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String description;
    private Long characters;
    private Integer price;
    private BookStatus status;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.description = book.getDescription();
        this.characters = book.getCharacters();
        this.price = book.getPrice();
        this.status = book.getStatus();
    }
}
