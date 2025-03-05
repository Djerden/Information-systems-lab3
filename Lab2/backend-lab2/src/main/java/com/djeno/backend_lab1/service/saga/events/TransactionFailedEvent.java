package com.djeno.backend_lab1.service.saga.events;

import com.djeno.backend_lab1.models.ImportHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFailedEvent {
    private String fileUrl;
    private ImportHistory importHistoryRecord;
}
