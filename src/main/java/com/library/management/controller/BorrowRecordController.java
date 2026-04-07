package com.library.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.management.dto.request.*;
import com.library.management.dto.response.*;
import com.library.management.service.BorrowRecordService;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @GetMapping
    public ResponseEntity<List<BorrowRecordResponseDTO>> getBorrowRecords(BorrowRecordFilterDTO filter) {
        return ResponseEntity.ok(borrowRecordService.getBorrowRecords(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecordResponseDTO> getBorrowRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.getBorrowRecordById(id));
    }

    @PostMapping
    public ResponseEntity<BorrowRecordResponseDTO> createBorrowRecord(@RequestBody BorrowRecordRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecordService.createBorrowRecord(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BorrowRecordResponseDTO> updateBorrowRecord(@PathVariable Long id,
                                                                       @RequestBody BorrowRecordRequestDTO request) {
        return ResponseEntity.ok(borrowRecordService.updateBorrowRecord(id, request));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<BorrowRecordResponseDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowRecord(@PathVariable Long id) {
        borrowRecordService.deleteBorrowRecord(id);
        return ResponseEntity.noContent().build();
    }
}
