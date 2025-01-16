package com.consultacep.service;


import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.consultacep.model.ConsultaCep;
import com.consultacep.repository.ConsultaCepRepository;

@Service
public class CepService {
    private final CepClient cepClient;
    private final CepPersistenceService persistenceService;
	
    public CepService(CepClient cepClient, CepPersistenceService persistenceService) {
        this.cepClient = cepClient;
        this.persistenceService = persistenceService;
    }
    
    public ConsultaCep consultarCep1(String cep) {
        Map<String, String> dados = cepClient.consultarCep(cep);
        ConsultaCep consulta = new ConsultaCep();
        consulta.setCep(cep);
        consulta.setLogradouro(dados.get("logradouro"));
        consulta.setBairro(dados.get("bairro"));
        consulta.setCidade(dados.get("cidade"));
        consulta.setEstado(dados.get("estado"));
        consulta.setDataConsulta(LocalDateTime.now());
        return persistenceService.salvarConsulta(consulta);
    }
    
	@Autowired
    private ConsultaCepRepository repository;

    private static final String API_URL = "http://localhost:8080/api/cep"; // Mockito

    public ConsultaCep consultarCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> response = restTemplate.getForObject(API_URL + "/" + cep, Map.class);

        if (response == null) {
            throw new RuntimeException("CEP não encontrado.");
        }

        ConsultaCep consulta = new ConsultaCep();
        consulta.setCep(cep);
        consulta.setLogradouro(response.get("logradouro"));
        consulta.setBairro(response.get("bairro"));
        consulta.setCidade(response.get("cidade"));
        consulta.setEstado(response.get("estado"));
        consulta.setDataConsulta(LocalDateTime.now());

        repository.save(consulta);

        return consulta;
    }
}
