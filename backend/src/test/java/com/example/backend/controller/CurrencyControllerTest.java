package com.example.backend.controller;

import com.example.backend.auth.SessionKeys;
import com.example.backend.dto.UserDTO;
import com.example.backend.service.CurrencyService;
import com.example.backend.config.ApiCacheFilter;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CurrencyControllerTest {
    @Test void requiresLoginBeforeUsingPaidProvider() throws Exception {
        var service = mock(CurrencyService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new CurrencyController(service)).build();
        mvc.perform(get("/currency/convert").param("amount", "10").param("from", "AUD").param("to", "USD"))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
    @Test void authenticatedConversionReturnsOnlyResultAndCannotBeCached() throws Exception {
        var service = mock(CurrencyService.class);
        when(service.convert(BigDecimal.TEN, "AUD", "USD")).thenReturn(new BigDecimal("6.5"));
        var mvc = MockMvcBuilders.standaloneSetup(new CurrencyController(service)).addFilters(new ApiCacheFilter()).build();
        mvc.perform(get("/currency/convert").sessionAttr(SessionKeys.USER_DTO, new UserDTO(1, "tester"))
                .param("amount", "10").param("from", "AUD").param("to", "USD"))
            .andExpect(status().isOk()).andExpect(content().json("{\"result\":6.5}", true))
            .andExpect(header().string("Cache-Control", "no-store, private"));
    }
}
