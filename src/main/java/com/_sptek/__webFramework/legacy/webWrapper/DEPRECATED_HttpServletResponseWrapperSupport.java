package com._sptek.__webFramework.legacy.webWrapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


/**
 * @deprecated 응답 본문 캐싱은 Spring의 {@code ContentCachingResponseWrapper} 등 검증된 래퍼 사용을 우선한다.
 *
 * <p>응답 본문을 메모리 버퍼에 저장해 필터 단계에서 읽거나 교체할 수 있도록 만든 기존 래퍼이다.</p>
 */
@Deprecated
public class DEPRECATED_HttpServletResponseWrapperSupport extends HttpServletResponseWrapper {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintWriter writer = new PrintWriter(outputStream, true);

    public DEPRECATED_HttpServletResponseWrapperSupport(HttpServletResponse response) {
        super(response);
    }

    /**
     * 현재까지 버퍼에 기록된 응답 본문을 UTF-8 문자열로 반환한다.
     */
    public String getResponseBody() {
        writer.flush();
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    /**
     * 응답 버퍼를 비우고 새 본문을 기록한다.
     */
    public void setResponseBody(String body) throws IOException {
        outputStream.reset();
        writer.write(body);
        writer.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new DEPRECATED_CustomServletOutputStream(outputStream);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }
}

/**
 * 응답 wrapper가 사용하는 메모리 기반 ServletOutputStream 구현체.
 */
class DEPRECATED_CustomServletOutputStream extends ServletOutputStream {

    private final ByteArrayOutputStream buffer;

    public DEPRECATED_CustomServletOutputStream(ByteArrayOutputStream buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }

    @Override
    public boolean isReady() {
        return true; // 항상 준비된 상태 반환
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        // 동기 방식이므로 바로 처리 완료 알림
        if (listener != null) {
            try {
                listener.onWritePossible();
            } catch (IOException e) {
                throw new RuntimeException("WriteListener 처리 중 에러 발생", e);
            }
        }
    }
}
