package com.sparta.fitnus.common.service;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.sparta.fitnus.common.exception.SlackException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

@Service
@RequiredArgsConstructor
public class SlackAlertService {

    private final MethodsClient methodsClient;

    @Value("${SLACK_CHANNEL}")
    private String slackChannel;

    public void execute(
            HttpServletRequest request,
            Exception e
    ) {
        final String url = request.getRequestURI();
        final String method = request.getMethod();
        final String errorMessage = e.getMessage();
        final String errorStack = getErrorStack(e);
        final String errorUserIp = request.getRemoteAddr();

        List<LayoutBlock> layoutBlockList = new ArrayList<>();

        layoutBlockList.add(Blocks.header(headerBlockBuilder
                -> headerBlockBuilder.text(plainText("Error Detection"))));

        layoutBlockList.add(divider());

        MarkdownTextObject methodMarkdown = MarkdownTextObject.builder()
                .text("* Request Addr :*\n" + method + " : " + url)
                .build();

        MarkdownTextObject errorUserIpMarkdown = MarkdownTextObject.builder()
                .text("* User IP :*\n" + errorUserIp)
                .build();

        layoutBlockList.add(section(section
                -> section.fields(List.of(methodMarkdown, errorUserIpMarkdown))));

        layoutBlockList.add(divider());

        MarkdownTextObject errorNameMarkdown = MarkdownTextObject.builder()
                .text("* Message :*\n" + errorMessage)
                .build();
        MarkdownTextObject errorStackMarkdown = MarkdownTextObject.builder()
                .text("* Stack Trace :*\n" + errorStack)
                .build();
        layoutBlockList.add(section(section
                -> section.fields(List.of(errorNameMarkdown, errorStackMarkdown))));

        sendMessage(layoutBlockList);
    }

    public String getErrorStack(Throwable throwable) {
        String exceptionAsString = Arrays.toString(throwable.getStackTrace());
        int MAX_LEN = 500;
        int cutLength = Math.min(exceptionAsString.length(), MAX_LEN);
        return exceptionAsString.substring(0, cutLength);
    }

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
