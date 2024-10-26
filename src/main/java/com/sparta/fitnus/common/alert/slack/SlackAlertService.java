package com.sparta.fitnus.common.alert.slack;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.LayoutBlock;
import com.sparta.fitnus.common.exception.SlackException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlackAlertService {

    private final MethodsClient methodsClient;

    @Async
    public void sendMessage(String slackChannel, List<LayoutBlock> layoutBlockList) {
        ChatPostMessageRequest chatPostMessageRequest =
                ChatPostMessageRequest.builder()
                        .channel(slackChannel)
                        .text("")
                        .blocks(layoutBlockList)
                        .build();
        try {
            methodsClient.chatPostMessage((chatPostMessageRequest));
        } catch (SlackApiException | IOException e) {
            throw new SlackException();
        }
    }
}
