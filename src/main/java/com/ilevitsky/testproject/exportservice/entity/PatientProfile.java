package com.ilevitsky.testproject.exportservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient_profile")
public class PatientProfile {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "old_guids", joinColumns = @JoinColumn(name = "patient_profile_id"))
  @Column(name = "old_client_guid")
  private Set<String> oldClientGuid;

  @Column(name = "status_id", nullable = false)
  private Integer statusId;
}
