package com.ilevitsky.testproject.exportservice.entity;

import lombok.Getter;

public enum PatientStatus {
  ACTIVE(200, "ACTIVE"),
  INACTIVE(400, "INACTIVE");

  @Getter private final int status_code;
  private final String status_name;

  PatientStatus(int statusCode, String statusName) {
    this.status_code = statusCode;
    this.status_name = statusName;
  }

  public static Integer getStatusCodeByName(String name) {
    for (PatientStatus status : PatientStatus.values()) {
      if (status.status_name.equalsIgnoreCase(name)) {
        return status.status_code;
      }
    }
    return null;
  }
}
