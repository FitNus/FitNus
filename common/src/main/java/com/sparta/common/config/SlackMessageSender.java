package com.sparta.common.config;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.LayoutBlock;
import com.sparta.common.exception.SlackException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlackMessageSender {

    private final MethodsClient methodsClient;

    @Value("${SLACK_CHANNEL}")
    private String slackChannel;

    @Async
    public void sendMessage(List<LayoutBlock> layoutBlockList) {
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
