package com.itgowo.servercore.websocket;

import com.itgowo.servercore.ServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class WebSocketServerHandler implements ServerHandler {
    private ChannelHandlerContext ctx;
    private WebSocketFrame webSocketFrame;

    public WebSocketFrame getWebSocketFrame() {
        return webSocketFrame;
    }

    public WebSocketServerHandler(ChannelHandlerContext ctx, WebSocketFrame frame) {
        this.ctx = ctx;
        this.webSocketFrame=frame;
    }

        public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WebSocketServerHandler{")
                .append("\r\nFreame='" + webSocketFrame.toString() + '\'' + "\r\n}");
        return stringBuilder.toString();
    }

    public WebSocketServerHandler setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        return this;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    /**
     * 返回结果
     */
    public void sendResponse(WebSocketFrame frame) throws UnsupportedEncodingException {
        ctx.writeAndFlush(frame);
    }
}
