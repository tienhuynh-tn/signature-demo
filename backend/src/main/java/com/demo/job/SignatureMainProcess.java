package com.demo.service;

import com.demo.dto.AIResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class SignatureMainProcess {

    private final ZebeeService zebeeService;
    private final SftpSyncService sftpSyncService;
    private final AIService aiService;

    public SignatureMainProcess(ZebeeService zebeeService, SftpSyncService sftpSyncService, SignatureService signatureService, AIService aiService) {
        this.zebeeService = zebeeService;
        this.sftpSyncService = sftpSyncService;
        this.aiService = aiService;
    }

    @JobWorker(type = "Type_UploadFile")
    public void uploadFile(JobClient jobClient, final ActivatedJob activatedJob) {
        sftpSyncService.syncCreatedOnce();
        jobClient.newCompleteCommand(activatedJob.getKey()).send().join();
    }

    @JobWorker(type = "MsgType_SendToInfra")
    public void sendToInfra(JobClient jobClient, final ActivatedJob activatedJob) {
        final Map<String, Object> variables = activatedJob.getVariablesAsMap();
        String appId = getAppIdFromVariable(variables);
        zebeeService.sendMessageEvent("Msg_StartUploadFile", appId);
    }


    @JobWorker(type = "MsgType_SendToApprove")
    public void sendToApprove(JobClient jobClient, final ActivatedJob activatedJob) {
        final Map<String, Object> variables = activatedJob.getVariablesAsMap();
        String appId = getAppIdFromVariable(variables);
        zebeeService.sendMessageEvent("Msg_StartApprove", appId);
    }


    @JobWorker(type = "MsgType_ApproveProcessDone")
    public void approveProcessDone(JobClient jobClient, final ActivatedJob activatedJob) {
        final Map<String, Object> variables = activatedJob.getVariablesAsMap();
        String appId = getAppIdFromVariable(variables);
        zebeeService.sendMessageEvent("Msg_StartCheckSignature", appId);
    }


    @JobWorker(type = "MsgType_CheckSignatureDone")
    public void checkSignatureDone(JobClient jobClient, final ActivatedJob activatedJob) {
        final Map<String, Object> variables = activatedJob.getVariablesAsMap();
        String appId = getAppIdFromVariable(variables);
        zebeeService.sendMessageEvent("MsgCatch_SystemHandleDone", appId);
    }

    @JobWorker(type = "Type_CheckSignature")
    public void checkSignature(JobClient jobClient, final ActivatedJob activatedJob) throws IOException {
        final Map<String, Object> variables = activatedJob.getVariablesAsMap();
        String appId = getAppIdFromVariable(variables);
        UUID appUUID = UUID.fromString(appId);
        AIResponse response = aiService.checkSimilarity(appUUID);
        final Map<String, Object> variableResponse = new HashMap<>();
        variableResponse.put("isMatch", response.getResult());
        jobClient.newCompleteCommand(activatedJob.getKey()).variables(variableResponse).send().join();
    }

    private String getAppIdFromVariable(Map<String, Object> vars) {
        Object value = vars.get("appId");
        if(value == null) {
            return null;
        }

        return String.valueOf(value);
    }
}
