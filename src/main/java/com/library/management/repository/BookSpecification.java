package com.library.management.repository;

import com.library.management.dto.request.BookFilterDTO;
import com.library.management.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withFilter(BookFilterDTO filter) {
        return Specification
            .where(hasTitle(filter.getTitle()))
            .and(hasAuthor(filter.getAuthor()));
    }

    private static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> title == null || title.isEmpty() ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> author == null || author.isEmpty() ? null
                : cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }
}
