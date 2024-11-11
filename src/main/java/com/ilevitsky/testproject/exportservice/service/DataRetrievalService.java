package com.ilevitsky.testproject.exportservice.service;

import com.ilevitsky.testproject.exportservice.dto.Client;
import com.ilevitsky.testproject.exportservice.dto.ClientNote;

import java.util.List;

public interface DataRetrievalService {
  List<Client> getAllClients();

  List<ClientNote> getAllNotesForClient();
}
