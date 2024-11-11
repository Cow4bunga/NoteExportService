package com.ilevitsky.testproject.exportservice.service.impl;

import com.ilevitsky.testproject.exportservice.dto.ClientRequestPayload;
import com.ilevitsky.testproject.exportservice.dto.Client;
import com.ilevitsky.testproject.exportservice.dto.ClientNote;
import com.ilevitsky.testproject.exportservice.dto.ClientResponse;
import com.ilevitsky.testproject.exportservice.exception.DataRetrievalException;
import com.ilevitsky.testproject.exportservice.service.DataRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataRetrievalServiceImpl implements DataRetrievalService {
  private final RestTemplate restTemplate;

  @Value("${data-storage.url}")
  private String requestUrl;

  @Override
  public List<Client> getAllClients() {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<ClientRequestPayload> requestEntity = new HttpEntity<>(null, headers);

      ResponseEntity<ClientResponse<Client>> response =
          restTemplate.exchange(
              requestUrl + "clients",
              HttpMethod.POST,
              requestEntity,
              new ParameterizedTypeReference<ClientResponse<Client>>() {});
      return response.getBody().getResponse();
    } catch (Exception e) {
      log.error("Error retrieving clients: {}", e.getMessage());
      throw new DataRetrievalException("Error while retrieving clients.");
    }
  }

  @Override
  public List<ClientNote> getAllNotesForClient() {
    var payload = formRequestForUsers();

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<ClientRequestPayload> requestEntity = new HttpEntity<>(payload, headers);

      ResponseEntity<ClientResponse<ClientNote>> responseEntity =
          restTemplate.exchange(
              requestUrl + "notes",
              HttpMethod.POST,
              requestEntity,
              new ParameterizedTypeReference<ClientResponse<ClientNote>>() {});

      return responseEntity.getBody().getResponse();
    } catch (Exception e) {
      log.error("Error retrieving client notes: {}", e.getMessage());
      throw new DataRetrievalException(
          String.format(
              "Error while retrieving notes. Client guid: %s, agency: %s",
              payload.getClientGuid(), payload.getAgency()));
    }
  }

  /*
   * This method is for demonstration purpose only
   * */
  private ClientRequestPayload formRequestForUsers() {
    ClientRequestPayload payload = new ClientRequestPayload();

    payload.setDateFrom(LocalDate.of(1990, 1, 1));
    payload.setDateTo(LocalDate.now());

    return payload;
  }
}
