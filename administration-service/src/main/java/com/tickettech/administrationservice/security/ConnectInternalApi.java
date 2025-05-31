package com.tickettech.administrationservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.config.AppConfig;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ConnectInternalApi {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${languageMicroserviceUrl}")
    String languageMicroserviceUrl;

    @Value("${secretUser}")
    String secretUser;

    @Value("${secretKey}")
    String secretKey;

    @Value("${authUrl}")
    String authUrl;
    private String gLanguage;


    @PostConstruct
    public void initialize() {
        if(appConfig==null) {
            this.appConfig = new AppConfig();
        }
        if(restTemplate==null) {
            this.restTemplate = new RestTemplate();
        }
        if(languageMicroserviceUrl==null) {
            this.languageMicroserviceUrl = "http://localhost:8888/LANGUAGE-SERVICE/hq-crm/v2/message/getOneMessage/";
        }
        this.gLanguage = appConfig.getLanguage();
    }

    private String GenerateToken() {

        try {
            String url = authUrl + "/internalLogin";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = "{\"username\":\"" + secretUser + "\", \"password\":\"" + secretKey + "\"}";

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();
                return parseTokenFromResponse(responseBody);
            } else {
                //Mensaje que no se lo puede tener en el servicio de Language porque no hay como consultar.
                System.out.println("Error: Server no found");
                return "";
            }
        }catch (HttpServerErrorException e) {
            //Mensaje que no se lo puede tener en el servicio de Language porque no hay como consultar.
            System.out.println("Server Error: " + e.getRawStatusCode() + " - " + e.getStatusText());
        } catch (Exception e) {
            //Mensaje que no se lo puede tener en el servicio de Language porque no hay como consultar.
            System.out.println("Error: " + e.getMessage());
        }
        return "";
    }

    private static String parseTokenFromResponse(String responseBody) {
        return responseBody.substring(responseBody.indexOf("\"token\":\"") + 8, responseBody.indexOf("\",\"username\"")).replace("\"","");
    }

    public String chargeMessage(String key, String language) throws URISyntaxException, JsonProcessingException {
        if(language != null && !language.isEmpty())
            this.gLanguage = language;

        String url = languageMicroserviceUrl + key + "/" + gLanguage;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + GenerateToken());

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        System.out.println(responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        ResultDTO returnService = objectMapper.readValue(responseBody, ResultDTO.class);

        return returnService.getObject().toString();
    }

    public String chargeMessage(String key) {
        initialize();
        String url = languageMicroserviceUrl + key + "/" + gLanguage;
        return restTemplate.getForObject(url, String.class);
    }

}
