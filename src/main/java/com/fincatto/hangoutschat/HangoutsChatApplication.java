package com.fincatto.hangoutschat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class HangoutsChatApplication implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(HangoutsChatApplication.class);

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(HangoutsChatApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final String endpoint = environment.getProperty("endpoint");
        if (endpoint != null) {
            LOG.info("Enviando mensagens para o endpoing: {}", endpoint);
            this.sendSimpleMessage(endpoint);
        } else {
            LOG.error("Configure a variavel de ambiente 'endpoint' com a url do canal que o bot deve postar!");
        }
    }

    private void sendSimpleMessage(String endpoint) throws java.io.IOException, InterruptedException {
        final HttpResponse<String> response = send(endpoint, "Important message for <users/all>: Code freeze _starts_ at midnight *tonight*!");
        LOG.debug("Response status code: " + response.statusCode());
        LOG.debug("Response headers: " + response.headers());
        LOG.debug("Response body: " + response.body());
    }

    private static HttpResponse<String> send(String endpoint, String payload) throws IOException, InterruptedException {
        final HttpClient httpClient = HttpClient.newBuilder().build();
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint)).setHeader("Content-Type", "application/json; charset=UTF-8").POST(HttpRequest.BodyPublishers.ofString(String.format("{'text' : '%s'}", payload))).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
