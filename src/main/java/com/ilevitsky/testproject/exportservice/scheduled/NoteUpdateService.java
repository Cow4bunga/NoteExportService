package com.ilevitsky.testproject.exportservice.scheduled;

import com.ilevitsky.testproject.exportservice.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteUpdateService {
  private final ExportService exportService;

  @Scheduled(cron = "0 * * * * *")
  public void exportNotes() {
    exportService.updateClients();
    exportService.updateClientNotes();
  }
}
