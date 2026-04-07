package com.library.management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.library.management.service.BorrowRecordService;
import com.library.management.dto.request.*;
import com.library.management.dto.response.*;
import com.library.management.entity.Book;
import com.library.management.entity.BorrowRecord;
import com.library.management.entity.User;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRecordRepository;
import com.library.management.repository.UserRepository;
import com.library.management.exception.ResourceNotFoundException;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Value("${library.borrow.max-days:14}")
    private int maxBorrowDays;

    @Value("${library.borrow.fine-per-day:5000}")
    private BigDecimal finePerDay;

    public BorrowRecordServiceImpl(BorrowRecordRepository borrowRecordRepository,
                                   BookRepository bookRepository,
                                   UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<BorrowRecordResponseDTO> getBorrowRecords(BorrowRecordFilterDTO filter) {
        // TODO: thêm Specification nếu cần filter phức tạp hơn
        return borrowRecordRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public BorrowRecordResponseDTO getBorrowRecordById(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found: " + id));
        return toResponseDTO(record);
    }

    @Override
    public BorrowRecordResponseDTO createBorrowRecord(BorrowRecordRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new ResourceNotFoundException("Book is not available for borrowing");
        }

        // Tạo borrow record
        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setStartTime(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(maxBorrowDays));

        // Giảm số lượng sách available
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return toResponseDTO(borrowRecordRepository.save(record));
    }

    @Override
    public BorrowRecordResponseDTO updateBorrowRecord(Long id, BorrowRecordRequestDTO request) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found: " + id));
        // Chỉ cho phép update dueDate (ví dụ gia hạn)
        return toResponseDTO(borrowRecordRepository.save(record));
    }

    @Override
    public BorrowRecordResponseDTO returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found: " + id));

        if (record.getReturnTime() != null) {
            throw new ResourceNotFoundException("Book already returned");
        }

        LocalDateTime returnTime = LocalDateTime.now();
        record.setReturnTime(returnTime);

        // Tính tiền phạt nếu trả trễ
        if (returnTime.isAfter(record.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), returnTime);
            record.setFine(finePerDay.multiply(BigDecimal.valueOf(daysLate)));
        }

        // Tăng lại số lượng sách available
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toResponseDTO(borrowRecordRepository.save(record));
    }

    @Override
    public void deleteBorrowRecord(Long id) {
        if (!borrowRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Borrow record not found: " + id);
        }
        borrowRecordRepository.deleteById(id);
    }

    private BorrowRecordResponseDTO toResponseDTO(BorrowRecord record) {
        BorrowRecordResponseDTO dto = new BorrowRecordResponseDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUser().getId());
        dto.setUserName(record.getUser().getName());
        dto.setBookId(record.getBook().getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setStartTime(record.getStartTime());
        dto.setDueDate(record.getDueDate());
        dto.setReturnTime(record.getReturnTime());
        dto.setFine(record.getFine());
        return dto;
    }
}
