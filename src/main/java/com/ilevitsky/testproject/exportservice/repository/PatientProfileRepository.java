package com.ilevitsky.testproject.exportservice.repository;

import com.ilevitsky.testproject.exportservice.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, UUID> {
  Optional<PatientProfile> findPatientProfileByFirstNameAndLastName(
      String firstName, String lastName);

  @Query(
          value = "SELECT pp.* FROM patient_profile pp JOIN old_guids og ON pp.id = og.patient_profile_id WHERE og.old_client_guid = :clientGuid",
          nativeQuery = true)
  Optional<PatientProfile> findByOldClientGuid(@Param("clientGuid") String clientGuid);
}
