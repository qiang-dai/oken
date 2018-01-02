package com.javahash.spring.download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javahash.spring.service.FileOperationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlDownloadAction {
    private FileOperationService fileOperationService;
    private String url;
    private String protocolUrl;
    private File localFile;
    private String downloadDir;
    private Boolean useProxy;

    public UrlDownloadAction(String url, FileOperationService fileOperationService, String subDir) {
        this.url = url;
        this.fileOperationService = fileOperationService;
        this.protocolUrl = "";
        this.localFile = null;

        String prefix = fileOperationService.getLogDir("");
        if (subDir.toLowerCase().contains(prefix.toLowerCase())) {
            this.downloadDir = subDir;
        } else {
            this.downloadDir = fileOperationService.getLogDir(subDir);
        }
        //log.info("this.downloadDir={}", this.downloadDir);

        if (! this.downloadDir.endsWith("/")) {
            this.downloadDir += "/";
        }
        fileOperationService.createDir(this.downloadDir);
    }

    public static CloseableHttpClient getIgnoreSslCertificateHttpClient() throws HttpException {

        SSLContext sslContext;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                @Override
                public boolean isTrusted(final X509Certificate[] arg0, final String arg1)
                        throws CertificateException {

                    return true;
                }
            }).build();
        } catch (Exception e) {
            throw new HttpException("can not create http client.", e);
        }
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                new NoopHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory).build();
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        return HttpClientBuilder.create().setSSLContext(sslContext).setConnectionManager(connMgr)
                .build();
//        return null;
    }

    public static String getResponse(String url, Map<String, String> headerMap) {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = getIgnoreSslCertificateHttpClient();
            CloseableHttpResponse response = null;
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
            for (String key : headerMap.keySet()) {
                httpget.setHeader(key, headerMap.get(key));
            }
            // 执行get请求.
            response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            String encoding =  null;
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(entity.getContent(), encoding);
            log.info("body:{}", body);

            httpclient.close();
            response.close();

            return body;
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }
    public Boolean isContentLengthEqual(CloseableHttpResponse response, String url) {
        Header header = response.getFirstHeader("content-length");
        if (header == null) {
            log.warn("get no 'content-length': {}", url);
            return true;
        }
        String length = header.getValue();
        Integer size = Integer.parseInt(length);

        HttpEntity httpEntity = response.getEntity();
        if (httpEntity.getContentLength() != size ){
            return false;
        }
        return true;
    }

}
