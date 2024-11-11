package com.ilevitsky.testproject.exportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientNote {

  private String guid;

  private String comments;

  private LocalDateTime modifiedDateTime;

  private String clientGuid;

  private LocalDateTime datetime;

  private String loggedUser;

  private LocalDateTime createdDateTime;
}
