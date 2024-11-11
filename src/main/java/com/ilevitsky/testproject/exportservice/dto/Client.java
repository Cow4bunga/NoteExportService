package com.ilevitsky.testproject.exportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

  private String guid;

  private String agency;

  private String firstName;

  private String lastName;

  private String status;

  private LocalDate dateOfBirth;

  private LocalDateTime createdDateTime;
}
