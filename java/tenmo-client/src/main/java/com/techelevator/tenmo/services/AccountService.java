package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.Principal;


public class AccountService {

    private String BASE_URL;
    public RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        BASE_URL = url;
    }

    public Account[] getAll(String token) throws AccountServiceException {
        Account[] accounts = null;
        try {
            accounts = restTemplate.exchange(BASE_URL + "account", HttpMethod.GET, makeAuthEntity(token), Account[].class).getBody();
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
        return accounts;
    }

    public Account getAccount(String token) {
        Account a = null;
        try {
           a = restTemplate.exchange(BASE_URL + "account/", HttpMethod.GET, makeAuthEntity(token), Account.class).getBody();
       } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return a;
    }

    public int getAccountIdByUserId(int userId, String token) {
        int id = 0;
        try {
            id = restTemplate.exchange(BASE_URL + "accounts/id/" + userId, HttpMethod.GET, makeAuthEntity(token), Integer.class).getBody();
        } catch (RestClientResponseException ex) {
            System.err.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException | NullPointerException ex) {
            System.err.println(ex.getMessage());
        }
        return id;
    }

    private HttpEntity makeAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
