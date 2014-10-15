/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ab.http;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO: Auto-generated Javadoc
/**
 * © 2012 amsoft.cn
 * 名称：AbMultipartEntity.java 
 * 描述：用于文件上传
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-11-13 下午1:09:20
 */
public class AbMultipartEntity implements HttpEntity {

    /** 日志标记. */
    private static final String TAG = "AbMultipartEntity";

    /** 流常量. */
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    
    /** CR_LF. */
    private static final byte[] CR_LF = ("\r\n").getBytes();
    
    /** TRANSFER_ENCODING_BINARY. */
    private static final byte[] TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n"
            .getBytes();

    /** MULTIPART_CHARS. */
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /** The boundary. */
    private String boundary;
    
    /** The boundary line. */
    private byte[] boundaryLine;
    
    /** The boundary end. */
    private byte[] boundaryEnd;

    /** The file parts. */
    private List<FilePart> fileParts = new ArrayList<FilePart>();

    /** 输出流. */
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /** 响应监听器. */
    private AbHttpResponseListener responseListener;

    /** The bytes written. */
    private int bytesWritten;

    /** 总大小. */
    private int totalSize;

    /**
     * 构造.
     *
     * @param responseListener the response listener
     */
    public AbMultipartEntity(AbHttpResponseListener responseListener) {
        final StringBuilder buf = new StringBuilder();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }

        boundary = buf.toString();
        boundaryLine = ("--" + boundary + "\r\n").getBytes();
        boundaryEnd = ("--" + boundary + "--\r\n").getBytes();

        this.responseListener = responseListener;
    }

    /**
     * Adds the part.
     *
     * @param key the key
     * @param value the value
     * @param contentType the content type
     */
    public void addPart(final String key, final String value, final String contentType) {
        try {
            out.write(boundaryLine);
            out.write(createContentDisposition(key));
            out.write(createContentType(contentType));
            out.write(CR_LF);
            out.write(value.getBytes());
            out.write(CR_LF);
        } catch (final IOException e) {
            // Can't happen on ByteArrayOutputStream
            Log.e(TAG, "addPart ByteArrayOutputStream exception", e);
        }
    }

    /**
     * Adds the part.
     *
     * @param key the key
     * @param value the value
     */
    public void addPart(final String key, final String value) {
        addPart(key, value, "text/plain; charset=UTF-8");
    }

    /**
     * Adds the part.
     *
     * @param key the key
     * @param file the file
     */
    public void addPart(String key, File file) {
        addPart(key, file, null);
    }

    /**
     * Adds the part.
     *
     * @param key the key
     * @param file the file
     * @param type the type
     */
    public void addPart(final String key, File file, String type) {
        if (type == null) {
            type = APPLICATION_OCTET_STREAM;
        }
        fileParts.add(new FilePart(key, file, type));
    }

    /**
     * Adds the part.
     *
     * @param key the key
     * @param streamName the stream name
     * @param inputStream the input stream
     * @param type the type
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void addPart(String key, String streamName, InputStream inputStream, String type)
            throws IOException {
        if (type == null) {
            type = APPLICATION_OCTET_STREAM;
        }
        out.write(boundaryLine);

        // Headers
        out.write(createContentDisposition(key, streamName));
        out.write(createContentType(type));
        out.write(TRANSFER_ENCODING_BINARY);
        out.write(CR_LF);

        // Stream (file)
        final byte[] tmp = new byte[4096];
        int l;
        while ((l = inputStream.read(tmp)) != -1) {
            out.write(tmp, 0, l);
        }

        out.write(CR_LF);
        out.flush();
        try {
            inputStream.close();
        } catch (final IOException e) {
            // Not important, just log it
            Log.w(TAG, "Cannot close input stream", e);
        }
    }

    /**
     * Creates the content type.
     *
     * @param type the type
     * @return the byte[]
     */
    private byte[] createContentType(String type) {
        String result = "Content-Type: " + type + "\r\n";
        return result.getBytes();
    }

    /**
     * Creates the content disposition.
     *
     * @param key the key
     * @return the byte[]
     */
    private byte[] createContentDisposition(final String key) {
        return ("Content-Disposition: form-data; name=\"" + key + "\"\r\n")
                .getBytes();
    }

    /**
     * Creates the content disposition.
     *
     * @param key the key
     * @param fileName the file name
     * @return the byte[]
     */
    private byte[] createContentDisposition(final String key, final String fileName) {
        return ("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n")
                .getBytes();
    }

    /**
     * Update progress.
     *
     * @param count the count
     */
    private void updateProgress(int count) {
        bytesWritten += count;
        responseListener.sendProgressMessage(bytesWritten, totalSize);
    }

    /**
     * The Class FilePart.
     */
    private class FilePart {
        
        /** The file. */
        public File file;
        
        /** The header. */
        public byte[] header;

        /**
         * Instantiates a new file part.
         *
         * @param key the key
         * @param file the file
         * @param type the type
         */
        public FilePart(String key, File file, String type) {
            header = createHeader(key, file.getName(), type);
            this.file = file;
        }

        /**
         * Creates the header.
         *
         * @param key the key
         * @param filename the filename
         * @param type the type
         * @return the byte[]
         */
        private byte[] createHeader(String key, String filename, String type) {
            ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
            try {
                headerStream.write(boundaryLine);

                // Headers
                headerStream.write(createContentDisposition(key, filename));
                headerStream.write(createContentType(type));
                headerStream.write(TRANSFER_ENCODING_BINARY);
                headerStream.write(CR_LF);
            } catch (IOException e) {
                // Can't happen on ByteArrayOutputStream
                Log.e(TAG, "createHeader ByteArrayOutputStream exception", e);
            }
            return headerStream.toByteArray();
        }

        /**
         * Gets the total length.
         *
         * @return the total length
         */
        public long getTotalLength() {
            long streamLength = file.length();
            return header.length + streamLength;
        }

        /**
         * Write to.
         *
         * @param out the out
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public void writeTo(OutputStream out) throws IOException {
            out.write(header);
            updateProgress(header.length);

            FileInputStream inputStream = new FileInputStream(file);
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = inputStream.read(tmp)) != -1) {
                out.write(tmp, 0, l);
                updateProgress(l);
            }
            out.write(CR_LF);
            updateProgress(CR_LF.length);
            out.flush();
            try {
                inputStream.close();
            } catch (final IOException e) {
                // Not important, just log it
                Log.w(TAG, "Cannot close input stream", e);
            }
        }
    }

    // The following methods are from the HttpEntity interface

    /**
     * 描述：获取长度.
     *
     * @return the content length
     * @see org.apache.http.HttpEntity#getContentLength()
     */
    @Override
    public long getContentLength() {
        long contentLen = out.size();
        for (FilePart filePart : fileParts) {
            long len = filePart.getTotalLength();
            if (len < 0) {
                return -1; // Should normally not happen
            }
            contentLen += len;
        }
        contentLen += boundaryEnd.length;
        return contentLen;
    }

    /**
     * 描述：获取类型.
     *
     * @version v1.0
     * @return the content type
     * @see org.apache.http.HttpEntity#getContentType()
     * @author: amsoft.cn
     * @date：2013-10-22 下午4:23:15
     */
    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    /**
     * 描述：TODO.
     *
     * @return true, if is chunked
     * @see org.apache.http.HttpEntity#isChunked()
     */
    @Override
    public boolean isChunked() {
        return false;
    }

    /**
     * 描述：TODO.
     *
     * @return true, if is repeatable
     * @see org.apache.http.HttpEntity#isRepeatable()
     */
    @Override
    public boolean isRepeatable() {
        return false;
    }

    /**
     * 描述：TODO.
     *
     * @return true, if is streaming
     * @see org.apache.http.HttpEntity#isStreaming()
     */
    @Override
    public boolean isStreaming() {
        return false;
    }

    /**
     * 描述：写入.
     *
     * @param outstream the outstream
     * @throws IOException Signals that an I/O exception has occurred.
     * @see org.apache.http.HttpEntity#writeTo(java.io.OutputStream)
     */
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        bytesWritten = 0;
        totalSize = (int) getContentLength();
        out.writeTo(outstream);
        updateProgress(out.size());

        for (FilePart filePart : fileParts) {
            filePart.writeTo(outstream);
        }
        outstream.write(boundaryEnd);
        updateProgress(boundaryEnd.length);
    }

    /**
     * 描述：获取编码.
     *
     * @return the content encoding
     * @see org.apache.http.HttpEntity#getContentEncoding()
     */
    @Override
    public Header getContentEncoding() {
        return null;
    }

    /**
     * 描述：TODO.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws UnsupportedOperationException the unsupported operation exception
     * @see org.apache.http.HttpEntity#consumeContent()
     */
    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    /**
     * 描述：获取内容流.
     *
     * @return the content
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws UnsupportedOperationException the unsupported operation exception
     * @see org.apache.http.HttpEntity#getContent()
     */
    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "getContent() is not supported. Use writeTo() instead.");
    }
}
