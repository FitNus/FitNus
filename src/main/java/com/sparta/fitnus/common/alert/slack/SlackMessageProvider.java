package com.sparta.fitnus.common.alert.slack;

import com.slack.api.model.block.LayoutBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlackMessageProvider {

    private final SlackAlertService slackAlertService;
    private final int MAX_LEN = 500;

    @Value("${SLACK_CHANNEL}")
    private String slackChannel;

    public String getErrorStack(Throwable throwable) {
        String exceptionAsString = Arrays.toString(throwable.getStackTrace());
        int cutLength = Math.min(exceptionAsString.length(), MAX_LEN);
        return exceptionAsString.substring(0, cutLength);
    }

    @Async
    public void sendMessage(List<LayoutBlock> layoutBlockList) {
        slackAlertService.sendMessage(slackChannel, layoutBlockList);
    }
}
