package com.itgowo.servercore.http;

import com.itgowo.servercore.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.stream.ChunkedFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class HttpServerHandler implements ServerHandler {
    public static final String JSON = "application/json;charset=utf-8";
    public static final String HTML = "text/html";
    public static final String JS = "application/javascript";
    public static final String CSS = "text/css";
    public static final String OBJECT = "application/octet-stream";
    private ChannelHandlerContext ctx;
    private FullHttpRequest httpRequest;
    private ByteBuf body = Unpooled.buffer();
    private QueryStringDecoder decoderQuery;
    private boolean isMultipart;
    private String webRootDir;

    private Map<String, String> parameters = new HashMap<>();
    private HttpHeaders responseHeader = new DefaultHttpHeaders();
    private List<File> fileUploads = new ArrayList<>();
    private HttpPostRequestDecoder decoder;

    public void addHeaderToResponse(String key, String value) {
        responseHeader.add(key, value);
    }

    public String getWebRootDir() {
        return webRootDir;
    }

    public HttpServerHandler(ChannelHandlerContext ctx, FullHttpRequest httpRequest, HttpDataFactory factory, String webRootDir) {
        this.ctx = ctx;
        this.webRootDir = webRootDir;
        this.httpRequest = httpRequest;
        decoder = new HttpPostRequestDecoder(factory, httpRequest);
        if (decoder.isMultipart()) {
            isMultipart = true;
            try {
                decoder.offer(httpRequest);
                for (InterfaceHttpData httpData : decoder.getBodyHttpDatas()) {
                    try {
                        if (httpData.retain() instanceof FileUpload) {
                            FileUpload fileUpload = (FileUpload) httpData.retain();
                            if (fileUpload.isInMemory()) {
                                File file = new File(webRootDir, "upload");
                                file.mkdirs();
                                file = new File(file, fileUpload.getFilename());
                                FileChannel fileChannel = null;
                                fileChannel = new FileOutputStream(file).getChannel();
                                fileChannel.write(fileUpload.content().nioBuffer());
                                fileChannel.force(true);
                                fileChannel.close();
                                fileUploads.add(file);
                            } else {
                                File file = fileUpload.getFile();
                                File fileto = new File(file.getParent(), fileUpload.getFilename());
                                boolean r = file.renameTo(fileto);
                                fileUploads.add(fileto);
                            }
                        } else {
                            MixedAttribute attribute = (MixedAttribute) httpData.retain();
                            parameters.put(attribute.getName(), attribute.getValue());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                ctx.channel().close();
                return;
            }
        } else {
            body.writeBytes(httpRequest.content());
            httpRequest.release();
        }
        if (httpRequest != null) {
            decoderQuery = new QueryStringDecoder(this.httpRequest.uri());
            Map<String, List<String>> stringListMap = decoderQuery.parameters();
            for (Map.Entry<String, List<String>> stringListEntry : stringListMap.entrySet()) {
                if (stringListEntry.getValue() == null || stringListEntry.getValue().size() == 0) {
                    continue;
                }
                parameters.put(stringListEntry.getKey(), stringListEntry.getValue().get(0));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HttpServerHandler{")
                .append("\r\nHttpMethod=").append(httpRequest == null ? "" : httpRequest.method() == null ? "" : httpRequest.method().name())
                .append("\r\nuri='" + decoderQuery == null ? "" : decoderQuery.toString() + '\'')
                .append("\r\nparameters=" + parameters)
                .append("\r\nfileUploads=" + fileUploads)
                .append(httpRequest == null || httpRequest.headers() == null || httpRequest.headers().entries() == null
                        ? "" : "\r\nmHeaders=" + httpRequest.headers().entries())
                .append("\r\nbody='" + getBody(null) + '\'' + "\r\n}");
        return stringBuilder.toString();
    }

    public HttpServerHandler setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        return this;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public List<File> getFileUploads() {
        return fileUploads;
    }

    public HttpServerHandler setFileUploads(List<File> fileUploads) {
        this.fileUploads = fileUploads;
        return this;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public String getPath() {
        return decoderQuery.path();
    }

    public String getUri() {
        return decoderQuery.uri();
    }

    public String getBody(Charset charset) {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        if (body == null) {
            return String.valueOf("");
        }
        return body.toString(charset);
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
     * 返回OPTIONNS请求，默认允许所有
     */
    public void sendOptionsResult() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().add(responseHeader);
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*");
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "Origin, 3600");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public String getDefaultMimeType(File file) {
        if (file == null) {
            return JSON;
        } else if (file.getName().toLowerCase().endsWith(".html")) {
            return HTML;
        } else if (file.getName().toLowerCase().endsWith(".htm")) {
            return HTML;
        } else if (file.getName().toLowerCase().endsWith(".js")) {
            return JS;
        } else if (file.getName().toLowerCase().endsWith(".css")) {
            return CSS;
        } else {
            return OBJECT;
        }
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
        response.headers().add(responseHeader);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
        if (!response.headers().contains(HttpHeaderNames.CONTENT_TYPE)) {
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, getDefaultMimeType(file));
        }
        boolean isAttachment = true;
        if (autoHtmltoNotAttachment) {
            isAttachment = !(file.getName().endsWith(".html") || file.getName().endsWith(".htm"));
        }
        response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, isAttachment ? "attachment; " : "" + "filename = " + file.getName());
        if (HttpUtil.isKeepAlive(httpRequest)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);
        ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

    }

    public boolean isMultipart() {
        return isMultipart;
    }
}
