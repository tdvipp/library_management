package com.library.management.service;

import java.util.List;

import com.library.management.entity.*;
import com.library.management.dto.request.*;
import com.library.management.dto.response.*;


public interface BorrowRecordService {
    List<BorrowRecordResponseDTO> getBorrowRecords(BorrowRecordFilterDTO request);
    BorrowRecordResponseDTO getBorrowRecordById(Long id);
    BorrowRecordResponseDTO createBorrowRecord(BorrowRecordRequestDTO request);
    BorrowRecordResponseDTO updateBorrowRecord(Long id, BorrowRecordRequestDTO request);
    BorrowRecordResponseDTO returnBook(Long id);
    void deleteBorrowRecord(Long id);
}