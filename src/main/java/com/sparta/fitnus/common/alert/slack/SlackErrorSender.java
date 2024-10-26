package com.sparta.fitnus.common.alert.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

@Component
@RequiredArgsConstructor
public class SlackErrorSender {

    private final ObjectMapper objectMapper;
    private final SlackMessageProvider slackMessageProvider;

    public void execute(
            ContentCachingRequestWrapper cachingRequest,
            Exception e
    ) {
        final String url = cachingRequest.getRequestURI();
        final String method = cachingRequest.getMethod();
        final String errorMessage = e.getMessage();
        final String errorStack = slackMessageProvider.getErrorStack(e);
        final String errorUserIp = cachingRequest.getRemoteAddr();

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

        slackMessageProvider.sendMessage(layoutBlockList);
    }
}
