package com.itgowo.servercore.http;/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://lujianchao.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

import com.itgowo.servercore.onServerListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.multipart.*;

import java.io.File;

/**
 * Created by lujianchao onServerListener 2017/3/28.
 * http服务器
 */
public class HttpServerInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    private onServerListener onServerListener;
    private String webRootDir;
    private ByteBuf byteBuf = Unpooled.buffer();
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    private HttpPostRequestDecoder decoder;

    public HttpServerInboundHandlerAdapter(String webRootDir, onServerListener onServerListener) {
        this.webRootDir = webRootDir;
        this.onServerListener = onServerListener;
        String dir = webRootDir + "/upload";
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = dir;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = dir;
        File file = new File(dir);
        file.mkdirs();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            if (decoder != null) {
                decoder.cleanFiles();
                decoder = null;
            }
            decoder = new HttpPostRequestDecoder(factory, fullHttpRequest);
            if (decoder.isMultipart()) {
                try {
                    decoder.offer((HttpContent) msg);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    ctx.channel().close();
                    return;
                }
            } else {
                byteBuf.writeBytes(fullHttpRequest.content());
                fullHttpRequest.release();
            }
            if (onServerListener != null) {
                if (ctx != null) {
                    try {
                        onServerListener.onReceiveHandler(new HttpServerHandler(ctx, fullHttpRequest, byteBuf, decoder, webRootDir));
                    } catch (Exception e) {
                        onServerListener.onError(e);
                    }
                }
            }
            byteBuf = Unpooled.buffer();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (onServerListener != null) {
            onServerListener.onError(cause);
        }
        ctx.close();
    }

}
