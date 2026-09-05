package com.example.backend.controller;

import com.example.backend.auth.SessionKeys;
import com.example.backend.service.CurrencyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/currency")
public class CurrencyController {
    private final CurrencyService service;
    public CurrencyController(CurrencyService service) { this.service = service; }

    @GetMapping("/convert")
    public Map<String, BigDecimal> convert(@RequestParam BigDecimal amount,
            @RequestParam String from, @RequestParam String to, HttpServletRequest request) {
        var session = request.getSession(false);
        if (session == null || session.getAttribute(SessionKeys.USER_DTO) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to convert currencies");
        }
        return Map.of("result", service.convert(amount, from, to));
    }
}
