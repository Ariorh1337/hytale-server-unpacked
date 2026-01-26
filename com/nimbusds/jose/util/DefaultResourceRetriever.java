/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.AbstractRestrictedResourceRetriever;
import com.nimbusds.jose.util.BoundedInputStream;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.RestrictedResourceRetriever;
import com.nimbusds.jose.util.StandardCharset;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

@ThreadSafe
public class DefaultResourceRetriever
extends AbstractRestrictedResourceRetriever
implements RestrictedResourceRetriever {
    private boolean disconnectAfterUse;
    private final SSLSocketFactory sslSocketFactory;
    private Proxy proxy;

    public DefaultResourceRetriever() {
        this(0, 0);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout) {
        this(connectTimeout, readTimeout, 0);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit) {
        this(connectTimeout, readTimeout, sizeLimit, true);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit, boolean disconnectAfterUse) {
        this(connectTimeout, readTimeout, sizeLimit, disconnectAfterUse, null);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit, boolean disconnectAfterUse, SSLSocketFactory sslSocketFactory) {
        super(connectTimeout, readTimeout, sizeLimit);
        this.disconnectAfterUse = disconnectAfterUse;
        this.sslSocketFactory = sslSocketFactory;
    }

    public boolean disconnectsAfterUse() {
        return this.disconnectAfterUse;
    }

    public void setDisconnectsAfterUse(boolean disconnectAfterUse) {
        this.disconnectAfterUse = disconnectAfterUse;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Resource retrieveResource(URL url) throws IOException {
        URLConnection con = null;
        try {
            String content;
            con = "file".equals(url.getProtocol()) ? this.openFileConnection(url) : this.openConnection(url);
            con.setConnectTimeout(this.getConnectTimeout());
            con.setReadTimeout(this.getReadTimeout());
            if (con instanceof HttpsURLConnection && this.sslSocketFactory != null) {
                ((HttpsURLConnection)con).setSSLSocketFactory(this.sslSocketFactory);
            }
            if (con instanceof HttpURLConnection && this.getHeaders() != null && !this.getHeaders().isEmpty()) {
                for (Map.Entry<String, List<String>> entry : this.getHeaders().entrySet()) {
                    for (String value : entry.getValue()) {
                        con.addRequestProperty(entry.getKey(), value);
                    }
                }
            }
            try (InputStream inputStream = this.getInputStream(con, this.getSizeLimit());){
                content = IOUtils.readInputStreamToString(inputStream, StandardCharset.UTF_8);
            }
            if (con instanceof HttpURLConnection) {
                HttpURLConnection httpCon = (HttpURLConnection)con;
                int statusCode = httpCon.getResponseCode();
                String statusMessage = httpCon.getResponseMessage();
                if (statusCode > 299 || statusCode < 200) {
                    throw new IOException("HTTP " + statusCode + ": " + statusMessage);
                }
            }
            String contentType = con instanceof HttpURLConnection ? con.getContentType() : null;
            Resource resource = new Resource(content, contentType);
            return resource;
        }
        catch (Exception e) {
            if (e instanceof IOException) {
                throw e;
            }
            throw new IOException("Couldn't open URL connection: " + e.getMessage(), e);
        }
        finally {
            if (this.disconnectAfterUse && con instanceof HttpURLConnection) {
                ((HttpURLConnection)con).disconnect();
            }
        }
    }

    @Deprecated
    protected HttpURLConnection openConnection(URL url) throws IOException {
        return this.openHTTPConnection(url);
    }

    protected HttpURLConnection openHTTPConnection(URL url) throws IOException {
        if (this.proxy != null) {
            return (HttpURLConnection)url.openConnection(this.proxy);
        }
        return (HttpURLConnection)url.openConnection();
    }

    protected URLConnection openFileConnection(URL url) throws IOException {
        return url.openConnection();
    }

    private InputStream getInputStream(URLConnection con, int sizeLimit) throws IOException {
        InputStream inputStream = con.getInputStream();
        return sizeLimit > 0 ? new BoundedInputStream(inputStream, this.getSizeLimit()) : inputStream;
    }
}

