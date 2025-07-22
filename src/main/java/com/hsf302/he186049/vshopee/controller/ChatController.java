package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.dto.ChatRequest;
import com.hsf302.he186049.vshopee.dto.ChatResponse;
import com.hsf302.he186049.vshopee.service.OpenAiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final OpenAiService openAiService;

    public ChatController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = openAiService.chat(request.getMessage());
        return new ChatResponse(reply);
    }
}
