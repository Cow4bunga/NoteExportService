package com.ilevitsky.testproject.exportservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient_note")
public class PatientNote {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "created_date_time", nullable = false)
  private LocalDateTime createdDateTime;

  @Column(name = "last_modified_date_time", nullable = false)
  private LocalDateTime lastModifiedDateTime;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id")
  private CompanyUser createdBy;

  @ManyToOne
  @JoinColumn(name = "last_modified_by_user_id")
  private CompanyUser lastModifiedBy;

  @Column(nullable = true, length = 4000)
  private String note;

  @ManyToOne
  @JoinColumn(name = "patient_id", nullable = false)
  private PatientProfile patient;
}
