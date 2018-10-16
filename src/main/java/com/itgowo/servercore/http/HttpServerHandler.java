package com.itgowo.servercore.http;

import com.itgowo.servercore.ServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class HttpServerHandler implements ServerHandler {
    private ChannelHandlerContext ctx;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private byte[] body;
    private String path;
    private String uri;
    private Map<String, String> parameters;
    private HttpHeaders responseHeader = new DefaultHttpHeaders();

    public void addHeaderToResponse(String key, String value) {
        responseHeader.add(key, value);
    }


    public HttpServerHandler(ChannelHandlerContext ctx, HttpRequest httpRequest, HttpResponse httpResponse, byte[] body) {
        this.ctx = ctx;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.body = body;
        if (httpRequest != null) {
            QueryStringDecoder decoderQuery = new QueryStringDecoder(this.httpRequest.uri());
            Map<String, List<String>> stringListMap = decoderQuery.parameters();
            for (Map.Entry<String, List<String>> stringListEntry : stringListMap.entrySet()) {
                String value;
                if (stringListEntry.getValue() == null || stringListEntry.getValue().size() == 0) {
                    continue;
                }
                parameters.put(stringListEntry.getKey(), stringListEntry.getValue().get(0));
            }
            path = decoderQuery.path().replaceFirst("/", "");
            uri = decoderQuery.uri();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WebSocketServerHandler{")
                .append("\r\nHttpMethod=").append(httpRequest == null ? "" : httpRequest.method() == null ? "" : httpRequest.method().name())
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

    public Map<String, String> getParameters() {
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
        response.headers().add(responseHeader);
        ctx.writeAndFlush(response);
    }

    public boolean isKeepAlive() {
        return HttpUtil.isKeepAlive(httpRequest);
    }

    /**
     * 通知浏览器跳转到新地址，code=302
     *
     * @param newUri
     */
    public void sendRedirect(String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().add(responseHeader);
        response.headers().add(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    /**
     * 用http协议返回一个文件
     *
     * @param file                    文件地址
     * @param autoHtmltoNotAttachment 默认文件都是附件形式下载，但是html类型不应该是附件模式，所以建议为ture，当然也可以全部当做文件来用
     * @throws IOException
     */
    public void sendFile(File file, boolean autoHtmltoNotAttachment) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");// 以只读的方式打开文件
        } catch (FileNotFoundException fnfe) {
            sendData(HttpResponseStatus.NOT_FOUND, "", false);
            return;
        }
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
        boolean isAttachment = true;
        if (autoHtmltoNotAttachment) {
            isAttachment = !(file.getName().endsWith(".html") || file.getName().endsWith(".htm"));
        }
        response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, isAttachment ? "attachment; " : "" + "filename = " + file.getName());
        if (HttpUtil.isKeepAlive(httpRequest)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        response.headers().add(responseHeader);
        ctx.write(response);
        ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

    }
}
