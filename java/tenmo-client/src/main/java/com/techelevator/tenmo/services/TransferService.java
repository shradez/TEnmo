package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
public class TransferService {

    private String BASE_URL;
    public RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url) {
        BASE_URL = url;
    }

    public Transfer[] getUserTransferHistory(String token) {
        Transfer[] transferHistory = null;
        try {
            transferHistory = restTemplate.exchange(BASE_URL + "transfers/getforuser", HttpMethod.GET, makeSimpleAuthEntity(token), Transfer[].class).getBody();
        } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return transferHistory;
    }

    public Transfer createSend(String token, int acctIdFrom, int acctIdTo, BigDecimal amount) {
        Transfer t = new Transfer(acctIdFrom, acctIdTo, amount);
        try {
            t = restTemplate.postForObject(BASE_URL + "transfers/send", makeAuthEntity(token, t), Transfer.class);
        } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return t;
    }
    public Transfer createRequest(String token, int acctIdFrom, int acctIdTo, BigDecimal amount) {
        Transfer t = new Transfer(acctIdFrom, acctIdTo, amount);
        try {
            t = restTemplate.postForObject(BASE_URL + "transfers/request", makeAuthEntity(token, t), Transfer.class);
        } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return t;
    }

    public Transfer approveTransferStatus(String token, Transfer transfer) {
       Transfer t = transfer;
        try {
            restTemplate.put(BASE_URL + "transfers/approve", makeAuthEntity(token, t));
        } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return t;
    }
    public Transfer rejectTransferStatus(String token, Transfer transfer) {
        Transfer t = transfer;
        try {
            restTemplate.put(BASE_URL + "transfers/reject", makeAuthEntity(token, t));
        } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return t;
    }

    private HttpEntity makeAuthEntity(String token, Transfer t) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity<>(t, headers);
        return entity;
    }

    private HttpEntity makeSimpleAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
