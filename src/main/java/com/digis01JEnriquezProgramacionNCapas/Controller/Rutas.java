package com.digis01JEnriquezProgramacionNCapas.Controller;

import com.digis01JEnriquezProgramacionNCapas.ML.Result;
import com.digis01JEnriquezProgramacionNCapas.ML.UsuarioDireccion;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Rutas {

    private RestTemplate restTemplate = new RestTemplate();
    private String baseUrl = "http://localhost:8081/";

    public Result GetAll() {
        ResponseEntity<Result<UsuarioDireccion>> responseEntityGetAll = restTemplate.exchange(baseUrl + "usuario",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
        });
        
        return responseEntityGetAll.getBody();
    }
}
