package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/ai")
@RestController
@CrossOrigin
public class AIChatController {
private AIChatService aiChatService;

    @Autowired
    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

//    @GetMapping("/chat")
//    public  String getResponse(@RequestBody ChatRequest chatRequest) {
//
//        return aiChatService.getResponse2(chatRequest.message(), chatRequest.conversationId());
//    }

    @PostMapping("/chat")
    public String getResponse(@RequestBody ChatRequest chatRequest) {
        return aiChatService.getResponse2(
                chatRequest.message(),
                chatRequest.conversationId()
        );
    }

}
