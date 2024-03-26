package com.jaychat;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final WebSocketClientHandshaker handShaker;
    private ChannelPromise channelPromise;

    public WebSocketClientHandler(WebSocketClientHandshaker handShaker) {
        this.handShaker = handShaker;
    }

    public ChannelPromise handShakerFuture() {
        return channelPromise;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelPromise = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handShaker.handshake(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(!channelPromise.isDone()) {
            channelPromise.setFailure(cause);
        }
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if(!handShaker.isHandshakeComplete()) {
            try {
                handShaker.finishHandshake(ch, (FullHttpResponse) msg);
                System.out.println("WebSocket Client connected!");
                channelPromise.setSuccess();
            } catch (WebSocketClientHandshakeException e) {
                channelPromise.setFailure(e);
            }
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if(frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            System.out.println("WebSocket received message: " + textFrame.text());
        } else if(frame instanceof PongWebSocketFrame) {
            System.out.println("WebSocket client received pong");
        } else if(frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ch.close();
        }
    }
}
