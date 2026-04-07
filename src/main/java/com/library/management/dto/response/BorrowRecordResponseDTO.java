package com.library.management.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BorrowRecordResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime startTime;
    private LocalDateTime dueDate;
    private LocalDateTime returnTime;
    private BigDecimal fine;
}
