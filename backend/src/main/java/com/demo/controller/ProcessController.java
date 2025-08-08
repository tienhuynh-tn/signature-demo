package com.demo.controller;

import com.demo.dto.StartInstanceRequest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
    private final ZeebeClient zeebeClient;

    public ProcessController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startProcessInstance(@RequestBody StartInstanceRequest request) {
        try {
            Map<String, Object> variables = Map.of("signatureId", request.getSignatureId());

            ProcessInstanceEvent instanceEvent = zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(request.getProcessId())
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();

            return ResponseEntity.ok("Started process instance with key: " + instanceEvent.getProcessInstanceKey());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start process: " + e.getMessage());
        }
    }
}
