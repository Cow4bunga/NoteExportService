package com.ilevitsky.testproject.exportservice.service.impl;

import com.ilevitsky.testproject.exportservice.dto.ClientRequestPayload;
import com.ilevitsky.testproject.exportservice.entity.CompanyUser;
import com.ilevitsky.testproject.exportservice.entity.PatientNote;
import com.ilevitsky.testproject.exportservice.entity.PatientProfile;
import com.ilevitsky.testproject.exportservice.entity.PatientStatus;
import com.ilevitsky.testproject.exportservice.dto.Client;
import com.ilevitsky.testproject.exportservice.dto.ClientNote;
import com.ilevitsky.testproject.exportservice.repository.*;
import com.ilevitsky.testproject.exportservice.service.DataRetrievalService;
import com.ilevitsky.testproject.exportservice.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {
  private final CompanyUserRepository companyUserRepository;
  private final PatientProfileRepository patientProfileRepository;
  private final PatientNoteRepository patientNoteRepository;
  private final DataRetrievalService dataRetrievalService;

  @Override
  @Async("noteExportExecutor")
  public void updateClients() {
    var clients = dataRetrievalService.getAllClients();
    int totalClients = clients.size();
    int processedCount = 0;

    for (Client client : clients) {
      createAndSavePatient(client);
      processedCount++;

      log.info(
          "Processed {} of {}, remaining: {}",
          processedCount,
          totalClients,
          totalClients - processedCount);
    }
  }

  @Override
  @Async("noteExportExecutor")
  public void updateClientNotes() {
    var notes = dataRetrievalService.getAllNotesForClient();
    int totalNotes = notes.size();
    int processedCount = 0;

    for (ClientNote clientNote : notes) {
      createAndSaveNote(clientNote);
      processedCount++;

      log.info(
          "Processed {} of {}, remaining: {}",
          processedCount,
          totalNotes,
          totalNotes - processedCount);
    }
  }

  private void createAndSavePatient(Client client) {
    var data =
        patientProfileRepository.findPatientProfileByFirstNameAndLastName(
            client.getFirstName(), client.getLastName());

    if (data.isPresent()) {
      var patient = data.get();
      var oldGuids = patient.getOldClientGuid();

      oldGuids.add(client.getGuid());
      patient.setOldClientGuid(oldGuids);

      patientProfileRepository.save(patient);
      log.info("Updated patient: {} {}", patient.getFirstName(), patient.getLastName());
    } else {
      PatientProfile patientProfile = new PatientProfile();
      patientProfile.setFirstName(client.getFirstName());
      patientProfile.setLastName(client.getLastName());
      patientProfile.setStatusId(PatientStatus.getStatusCodeByName(client.getStatus()));

      Set<String> guids = new HashSet<>();
      guids.add(client.getGuid());
      patientProfile.setOldClientGuid(guids);

      patientProfileRepository.save(patientProfile);
      log.info(
          "Created new patient: {} {}, guid: {}, status: {}",
          client.getFirstName(),
          client.getLastName(),
          client.getGuid(),
          client.getStatus());
    }
  }

  private CompanyUser createAndSaveUser(ClientNote clientNote) {
    if (!companyUserRepository.existsCompanyUserByLogin(clientNote.getLoggedUser())) {
      CompanyUser companyUser = new CompanyUser();
      companyUser.setLogin(clientNote.getLoggedUser());
      var user = companyUserRepository.save(companyUser);
      log.info("Created new user with login {}", clientNote.getLoggedUser());
      return user;
    } else {
      log.info("User with login {} already exists in new system", clientNote.getLoggedUser());
      return companyUserRepository.findCompanyUserByLogin(clientNote.getLoggedUser()).get();
    }
  }

  private void createAndSaveNote(ClientNote clientNote) {
    var user = createAndSaveUser(clientNote);
    var data = patientProfileRepository.findByOldClientGuid(clientNote.getClientGuid());

    if (data.isPresent()) {
      if (data.get().getStatusId() == 400) {
        return;
      }
      var note = patientNoteRepository.findPatientNoteByPatient(data.get());
      if (note.isEmpty()) {
        PatientNote patientNote = new PatientNote();

        patientNote.setCreatedBy(user);
        patientNote.setPatient(data.get());
        patientNote.setNote(clientNote.getComments());
        patientNote.setLastModifiedBy(user);
        patientNote.setCreatedDateTime(clientNote.getCreatedDateTime());
        patientNote.setLastModifiedDateTime(clientNote.getModifiedDateTime());

        patientNoteRepository.save(patientNote);
      } else {
        var noteToUpdate = note.get();
        if (clientNote.getModifiedDateTime().isAfter(noteToUpdate.getLastModifiedDateTime())) {
          noteToUpdate.setNote(clientNote.getComments());
          noteToUpdate.setLastModifiedDateTime(clientNote.getModifiedDateTime());
          noteToUpdate.setLastModifiedBy(user);

          patientNoteRepository.save(noteToUpdate);
        }
      }
    }
  }
}
