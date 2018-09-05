package com.itgowo.SimpleServerCore.Http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class HttpServerHandler {
    private ChannelHandlerContext ctx;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private byte[] body;
    private String path;
    private String uri;
    Map<String, List<String>> parameters;

    public HttpServerHandler(ChannelHandlerContext ctx, HttpRequest httpRequest, HttpResponse httpResponse, byte[] body) {
        this.ctx = ctx;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.body = body;
        if (httpRequest != null) {
            QueryStringDecoder decoderQuery = new QueryStringDecoder(this.httpRequest.uri());
            parameters = decoderQuery.parameters();
            path = decoderQuery.path();
            uri = decoderQuery.uri();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HttpServerHandler{")
                .append("\r\nMethod=").append(httpRequest == null ? "" : httpRequest.method() == null ? "" : httpRequest.method().name())
                .append("\r\npath='").append(path + '\'')
                .append("\r\nuri='" + uri + '\'')
                .append("\r\nparameters=" + parameters)
                .append(httpResponse == null ? "" : "\r\nhttpResponse=" + httpResponse.toString())
                .append(httpRequest == null || httpRequest.headers() == null || httpRequest.headers().entries() == null
                        ? "" : "\r\nmHeaders=" + httpRequest.headers().entries())
                .append("\r\nbody='" + getBody(null) + '\'' + "\r\n}");
        return stringBuilder.toString();
    }

    public HttpServerHandler setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        return this;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public String getPath() {
        return path;
    }

    public String getUri() {
        return uri;
    }

    public String getBody(Charset charset) {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        if (body == null) {
            return String.valueOf("");
        }
        return new String(body, charset);
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void sendData(Object data, boolean isJsonString) throws UnsupportedEncodingException {
        sendResponse(HttpResponseStatus.OK, data, isJsonString);
    }

    public void sendData(int status, Object data, boolean isJsonString) throws UnsupportedEncodingException {
        sendResponse(HttpResponseStatus.valueOf(status), data, isJsonString);
    }

    public void sendData(HttpResponseStatus status, Object data, boolean isJsonString) throws UnsupportedEncodingException {
        sendResponse(status, data, isJsonString);
    }

    /**
     * 添加通用json数据头,允许跨域
     *
     * @param response
     */
    private void addResponseHeaders(FullHttpResponse response, boolean contentIsJson) {
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentIsJson ? "application/json" : "text/html;charset=utf-8");
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    }

    /**
     * 返回结果
     */
    private void sendResponse(HttpResponseStatus status, Object entity, boolean isJsonString) throws UnsupportedEncodingException {
        FullHttpResponse response = null;
        if (entity instanceof String) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(((String) entity).getBytes("UTF-8")));
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer((entity.toString()).getBytes("UTF-8")));
        }
        addResponseHeaders(response, isJsonString);
        ctx.writeAndFlush(response);
    }

    public interface onReceiveHandlerListener {
        void onReceiveHandler(HttpServerHandler handler);

        void onError(Throwable throwable);

        void onServerStarted(int serverPort);

        void onServerStop();
    }
}
