package com.jaychat;

public record ChatTextMessage(
        String msg,
        String roomId
) {
}
