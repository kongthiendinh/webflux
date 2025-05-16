package com.example.reactiveservice.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class AccountInqController {
    private final AccountInqService  accountInqService;
    @PostMapping("/v1/accting")
    public Mono<Object> accting(@Valid @RequestBody String payload) {
        return accountInqService.sendAsyncMessage(payload);
    }




}
