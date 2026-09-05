package com.example.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class CurrencyServiceTest {
    @Test void convertsUsingServerCredential() {
        var client = new RestTemplate();
        var server = MockRestServiceServer.createServer(client);
        server.expect(requestTo("https://api.unirateapi.com/api/convert?api_key=test-secret&amount=10&from=AUD&to=USD&format=json"))
            .andRespond(withSuccess("{\"result\":6.5}", MediaType.APPLICATION_JSON));
        assertThat(new CurrencyService("test-secret", client).convert(BigDecimal.TEN, "AUD", "USD"))
            .isEqualByComparingTo("6.5");
        server.verify();
    }
    @Test void rejectsInvalidAmountsAndCurrenciesBeforeCallingProvider() {
        var service = new CurrencyService("test-secret", new RestTemplate());
        assertThatThrownBy(() -> service.convert(BigDecimal.ZERO, "AUD", "USD"))
            .isInstanceOf(ResponseStatusException.class).hasMessageContaining("400");
        assertThatThrownBy(() -> service.convert(BigDecimal.TEN, "INVALID", "USD"))
            .isInstanceOf(ResponseStatusException.class).hasMessageContaining("400");
    }
    @Test void sameCurrencyNeedsNoProvider() {
        assertThat(new CurrencyService("", new RestTemplate()).convert(BigDecimal.TEN, "AUD", "AUD"))
            .isEqualByComparingTo("10");
    }
    @Test void missingKeyReturnsServiceUnavailable() {
        assertThatThrownBy(() -> new CurrencyService("", new RestTemplate()).convert(BigDecimal.TEN, "AUD", "USD"))
            .isInstanceOf(ResponseStatusException.class).hasMessageContaining("503");
    }
    @Test void providerErrorDoesNotLeakCredentialsOrResponse() {
        var client = new RestTemplate();
        var server = MockRestServiceServer.createServer(client);
        server.expect(anything()).andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("test-secret"));
        Throwable error = catchThrowable(() -> new CurrencyService("test-secret", client).convert(BigDecimal.TEN, "AUD", "USD"));
        assertThat(error).isInstanceOf(ResponseStatusException.class).hasMessageContaining("502");
        assertThat(error.getMessage()).doesNotContain("test-secret", "api_key");
        assertThat(error.getCause()).isNull();
    }
    @Test void malformedProviderResultIsRejected() {
        var client = new RestTemplate();
        var server = MockRestServiceServer.createServer(client);
        server.expect(anything()).andRespond(withSuccess("{\"result\":\"NaN\"}", MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> new CurrencyService("test-secret", client).convert(BigDecimal.TEN, "AUD", "USD"))
            .isInstanceOf(ResponseStatusException.class).hasMessageContaining("502");
    }
}
