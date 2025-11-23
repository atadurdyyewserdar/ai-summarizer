package com.aissummarizer.jennet.apilog.tool;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RequestSizeWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public RequestSizeWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cachedBody = request.getInputStream().readAllBytes();
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
    }

    public int getRequestSize() {
        return cachedBody.length;
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() { return bais.available() == 0; }
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setReadListener(ReadListener listener) {}
            @Override
            public int read() { return bais.read(); }
        };
    }
}