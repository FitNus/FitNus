package com.sparta.modulecommon.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDeportRequest {

    private Long userId;
    private Long clubId;
}
