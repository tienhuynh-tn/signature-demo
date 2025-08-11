package com.demo.service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ZebeeService {

    private final ZeebeClient zeebeClient;

    public ZebeeService(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public void startNewInstance(String message, String appId) {
        log.info("Start instance with name={} and appId={}", message, appId);
        Map<String, Object> variables = Map.of("appId", appId);

        ProcessInstanceEvent instanceEvent = zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId("SignatureMain")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        log.info("Started process instance with key: " + instanceEvent.getProcessInstanceKey());;
    }

    public void sendMessageEvent(String message, String appId) {
        log.info("Send receive task with name={} and appId={}", message, appId);
        zeebeClient.newPublishMessageCommand()
                .messageName(message)
                .correlationKey(appId)
                .send()
                .join();
    }
}
