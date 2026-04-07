package com.library.management.service;

import java.util.List;

import com.library.management.dto.request.*;
import com.library.management.dto.response.*;

public interface BookService {

    List<BookResponseDTO> getBooks(BookFilterDTO request);
    BookResponseDTO getBookById(Long id);
    BookResponseDTO createBook(BookRequestDTO request);
    BookResponseDTO updateBook(Long id, BookRequestDTO request);
    void deleteBook(Long id);
}