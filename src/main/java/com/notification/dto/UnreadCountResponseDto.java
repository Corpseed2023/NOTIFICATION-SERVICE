package com.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreadCountResponseDto {

    private Long userId;

    private long unreadCount;
}