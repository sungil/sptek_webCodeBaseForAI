package com._sptek.__webFramework.legacy.webWrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @deprecated 요청 본문 캐싱은 Spring의 {@code ContentCachingRequestWrapper} 등 검증된 래퍼 사용을 우선한다.
 *
 * <p>필터 등 컨트롤러 이전 단계에서 request body를 먼저 읽은 뒤 다시 읽을 수 있도록 보관하는 기존 래퍼이다.</p>
 */
@Deprecated
public class DPRECATED_HttpServletRequestWrapperSupport extends HttpServletRequestWrapper {

    private byte[] rawData;
    private HttpServletRequest request;
    private ResettableServletInputStream servletInputStream;

    public DPRECATED_HttpServletRequestWrapperSupport(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletInputStream = new ResettableServletInputStream();
    }

    /**
     * 요청 본문을 문자열로 읽고 내부 입력 스트림을 같은 본문으로 재설정한다.
     */
    public String getRequestBody() throws IOException {
        String requestBody = IOUtils.toString(this.getReader());
        this.setRequestBody(requestBody);

        return requestBody;
    }

    /**
     * 이후 {@link #getInputStream()}과 {@link #getReader()}가 읽을 본문을 교체한다.
     */
    public void setRequestBody(String requestBody) throws IOException {
        this.resetInputStream(requestBody.getBytes());
    }

    /**
     * 내부 ServletInputStream이 읽을 원본 byte 배열을 새 입력 스트림으로 교체한다.
     */
    public void resetInputStream(byte[] data) {
        servletInputStream.inputStream = new ByteArrayInputStream(data);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletInputStream.inputStream = new ByteArrayInputStream(rawData);
        }
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader(), StandardCharsets.UTF_8);
            servletInputStream.inputStream = new ByteArrayInputStream(rawData);
        }
        return new BufferedReader(new InputStreamReader(servletInputStream));
    }

    private class ResettableServletInputStream extends ServletInputStream {
        private InputStream inputStream;

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}
