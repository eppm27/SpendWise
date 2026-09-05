package com.example.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Set;

@Service
public class CurrencyService {
    private static final Set<String> CURRENCIES = Set.of("AUD", "USD", "CNY", "EUR", "JPY", "GBP");
    private final String apiKey;
    private final RestTemplate client;

    @org.springframework.beans.factory.annotation.Autowired
    public CurrencyService(@Value("${unirate.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(7000);
        this.client = new RestTemplate(factory);
    }

    // Package-private constructor lets tests intercept HTTP without real provider calls.
    CurrencyService(String apiKey, RestTemplate client) {
        this.apiKey = apiKey;
        this.client = client;
    }

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (amount == null || amount.signum() <= 0 || amount.compareTo(new BigDecimal("1000000000")) > 0
                || !CURRENCIES.contains(from) || !CURRENCIES.contains(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount or unsupported currency");
        }
        if (from.equals(to)) return amount;
        if (apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Currency service is not configured");
        }
        URI uri = UriComponentsBuilder.fromUriString("https://api.unirateapi.com/api/convert")
                .queryParam("api_key", apiKey).queryParam("amount", amount.toPlainString())
                .queryParam("from", from).queryParam("to", to).queryParam("format", "json")
                .build().encode().toUri();
        try {
            JsonNode body = client.getForObject(uri, JsonNode.class);
            JsonNode result = body == null ? null : body.get("result");
            if (result == null || (!result.isNumber() && !result.isTextual())) throw new IllegalArgumentException();
            BigDecimal value = new BigDecimal(result.asText());
            if (value.signum() <= 0) throw new IllegalArgumentException();
            return value;
        } catch (Exception ignored) {
            // Provider errors can contain the credential-bearing request URL. Never propagate them.
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Currency provider is temporarily unavailable");
        }
    }
}
