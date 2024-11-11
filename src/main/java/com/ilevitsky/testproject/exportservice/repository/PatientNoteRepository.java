package com.ilevitsky.testproject.exportservice.repository;

import com.ilevitsky.testproject.exportservice.entity.PatientNote;
import com.ilevitsky.testproject.exportservice.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientNoteRepository extends JpaRepository<PatientNote, UUID> {
  Optional<PatientNote> findPatientNoteByPatient(PatientProfile patientProfile);
}
