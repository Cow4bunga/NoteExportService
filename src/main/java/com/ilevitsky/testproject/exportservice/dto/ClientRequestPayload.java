package com.ilevitsky.testproject.exportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestPayload {
  private String agency;
  private String clientGuid;
  private LocalDate dateFrom;
  private LocalDate dateTo;
}
