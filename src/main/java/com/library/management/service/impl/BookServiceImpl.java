package com.library.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.management.service.BookService;
import com.library.management.dto.request.*;
import com.library.management.dto.response.*;
import com.library.management.entity.Book;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BookSpecification;
import com.library.management.exception.ResourceNotFoundException;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookResponseDTO> getBooks(BookFilterDTO filter) {
        return bookRepository.findAll(BookSpecification.withFilter(filter))
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
        return toResponseDTO(book);
    }

    @Override
    public BookResponseDTO createBook(BookRequestDTO request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies()); // ban đầu tất cả đều available
        return toResponseDTO(bookRepository.save(book));
    }

    @Override
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        return toResponseDTO(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Helper - tránh lặp code convert entity → DTO
    private BookResponseDTO toResponseDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setAuthor(book.getAuthor());
        dto.setCategory(book.getCategory());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        return dto;
    }
}
