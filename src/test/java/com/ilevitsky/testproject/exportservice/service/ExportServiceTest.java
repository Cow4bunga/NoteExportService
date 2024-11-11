package com.ilevitsky.testproject.exportservice.service;

import com.ilevitsky.testproject.exportservice.dto.Client;
import com.ilevitsky.testproject.exportservice.dto.ClientNote;
import com.ilevitsky.testproject.exportservice.entity.CompanyUser;
import com.ilevitsky.testproject.exportservice.entity.PatientNote;
import com.ilevitsky.testproject.exportservice.entity.PatientProfile;
import com.ilevitsky.testproject.exportservice.repository.CompanyUserRepository;
import com.ilevitsky.testproject.exportservice.repository.PatientNoteRepository;
import com.ilevitsky.testproject.exportservice.repository.PatientProfileRepository;
import com.ilevitsky.testproject.exportservice.service.impl.ExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExportServiceTest {

  private ExportServiceImpl exportService;
  private CompanyUserRepository companyUserRepository;
  private PatientProfileRepository patientProfileRepository;
  private PatientNoteRepository patientNoteRepository;
  private DataRetrievalService dataRetrievalService;

  @BeforeEach
  public void setUp() {
    companyUserRepository = mock(CompanyUserRepository.class);
    patientProfileRepository = mock(PatientProfileRepository.class);
    patientNoteRepository = mock(PatientNoteRepository.class);
    dataRetrievalService = mock(DataRetrievalService.class);

    exportService =
        new ExportServiceImpl(
            companyUserRepository,
            patientProfileRepository,
            patientNoteRepository,
            dataRetrievalService);
  }

  @Test
  void testUpdateClients_createsNewPatient() {
    Client client =
        new Client(
            "12345", "Agency1", "John", "Doe", "Active", LocalDate.now(), LocalDateTime.now());
    when(dataRetrievalService.getAllClients()).thenReturn(Arrays.asList(client));
    when(patientProfileRepository.findPatientProfileByFirstNameAndLastName("John", "Doe"))
        .thenReturn(Optional.empty());

    exportService.updateClients();

    verify(patientProfileRepository, times(1)).save(any(PatientProfile.class));
  }

  @Test
  void testUpdateClients_updatesExistingPatient() {
    Client client =
        new Client(
            "54321", "Agency2", "Jane", "Doe", "Active", LocalDate.now(), LocalDateTime.now());
    PatientProfile existingProfile = new PatientProfile();
    existingProfile.setOldClientGuid(new HashSet<>(Arrays.asList("existing-guid")));
    when(dataRetrievalService.getAllClients()).thenReturn(Arrays.asList(client));
    when(patientProfileRepository.findPatientProfileByFirstNameAndLastName("Jane", "Doe"))
        .thenReturn(Optional.of(existingProfile));

    exportService.updateClients();

    verify(patientProfileRepository, times(1)).save(existingProfile);
    assertTrue(existingProfile.getOldClientGuid().contains("54321"));
  }

  @Test
  void testUpdateClientNotes_createsNewNote() {
    ClientNote clientNote =
        new ClientNote(
            "note-guid",
            "New note",
            LocalDateTime.now(),
            "client-guid",
            LocalDateTime.now(),
            "User1",
            LocalDateTime.now());
    when(dataRetrievalService.getAllNotesForClient()).thenReturn(Arrays.asList(clientNote));

    PatientProfile patientProfile = new PatientProfile();
    patientProfile.setStatusId(200);
    when(patientProfileRepository.findByOldClientGuid("client-guid"))
        .thenReturn(Optional.of(patientProfile));
    when(companyUserRepository.existsCompanyUserByLogin("User1")).thenReturn(false);
    when(companyUserRepository.save(any())).thenReturn(new CompanyUser());

    exportService.updateClientNotes();

    verify(patientNoteRepository, times(1)).save(any(PatientNote.class));
  }

  @Test
  void testUpdateClientNotes_updatesExistingNote() {
    LocalDateTime now = LocalDateTime.now();
    ClientNote clientNote =
        new ClientNote(
            "note-guid",
            "Updated note",
            now.plusMinutes(1),
            "client-guid",
            now,
            "User1",
            now);

    when(dataRetrievalService.getAllNotesForClient()).thenReturn(Arrays.asList(clientNote));

    PatientProfile patientProfile = new PatientProfile();
    patientProfile.setStatusId(200);

    PatientNote existingNote = new PatientNote();
    existingNote.setLastModifiedDateTime(now);
    existingNote.setNote("Old note");

    when(patientProfileRepository.findByOldClientGuid("client-guid"))
        .thenReturn(Optional.of(patientProfile));
    when(companyUserRepository.existsCompanyUserByLogin("User1")).thenReturn(true);
    when(companyUserRepository.findCompanyUserByLogin("User1"))
        .thenReturn(Optional.of(new CompanyUser()));
    when(patientNoteRepository.findPatientNoteByPatient(patientProfile))
        .thenReturn(Optional.of(existingNote));

    exportService.updateClientNotes();

    verify(patientNoteRepository, times(1)).save(existingNote);
    assertEquals("Updated note", existingNote.getNote());
  }
}
