package org.lwd.frame.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.util.http")
public class HttpImpl implements Http, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private TimeHash timeHash;
    @Inject
    private Logger logger;
    @Value("${frame.util.http.pool.max:256}")
    private int max;
    @Value("${frame.util.http.connect.time-out:5000}")
    private int connectTimeout;
    @Value("${frame.util.http.read.time-out:20000}")
    private int readTimeout;
    private PoolingHttpClientConnectionManager manager;
    private ThreadLocal<Integer> statusCode = new ThreadLocal<>();

    @Override
    public String get(String url, Map<String, String> headers, Map<String, String> parameters, String charset) {
        if (validator.isEmpty(parameters))
            return get(url, headers, "", charset);

        StringBuilder sb = new StringBuilder();
        parameters.forEach((name, value) -> sb.append('&').append(name).append('=').append(converter.encodeUrl(parameters.get(name), charset)));

        return get(url, headers, sb.substring(1), charset);
    }

    @Override
    public String get(String url, Map<String, String> headers, String parameters, String charset) {
        if (validator.isEmpty(url))
            return null;

        if (!validator.isEmpty(parameters))
            url = url + (url.indexOf('?') == -1 ? '?' : '&') + parameters;

        if (logger.isDebugEnable())
            logger.debug("使用GET访问[{}]。", url);

        String content = null;
        try {
            HttpGet get = new HttpGet(url);
            get.setConfig(getRequestConfig());
            content = execute(get, headers, charset);
        } catch (Exception e) {
            logger.warn(e, "使用GET访问[{}]时发生异常！", url);
        }

        if (logger.isDebugEnable())
            logger.debug("使用GET访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public String post(String url, Map<String, String> headers, Map<String, String> parameters, String charset) {
        if (validator.isEmpty(parameters))
            return postByEntity(url, headers, null, charset);

        try {
            List<NameValuePair> nvps = new ArrayList<>();
            parameters.forEach((key, value) -> nvps.add(new BasicNameValuePair(key, value)));

            return postByEntity(url, headers, new UrlEncodedFormEntity(nvps, context.getCharset(charset)), charset);
        } catch (Exception e) {
            logger.warn(e, "使用POST访问[{}]时发生异常！", url);

            return null;
        }
    }

    @Override
    public String post(String url, Map<String, String> headers, String content, String charset) {
        try {
            return postByEntity(url, headers, validator.isEmpty(content) ? null : new StringEntity(content, context.getCharset(charset)), charset);
        } catch (Exception e) {
            logger.warn(e, "使用POST访问[{}]时发生异常！", url);

            return null;
        }
    }

    @Override
    public String upload(String url, Map<String, String> headers, Map<String, String> parameters, Map<String, File> files, String charset) {
        if (validator.isEmpty(files))
            return post(url, headers, parameters, charset);

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        ContentType contentType = ContentType.create("text/plain", context.getCharset(charset));
        if (!validator.isEmpty(parameters))
            parameters.forEach((key, value) -> entity.addTextBody(key, value, contentType));
        files.forEach(entity::addBinaryBody);

        return postByEntity(url, headers, entity.build(), charset);
    }

    private String postByEntity(String url, Map<String, String> headers, HttpEntity entity, String charset) {
        if (validator.isEmpty(url))
            return null;

        if (logger.isDebugEnable())
            logger.debug("使用POST访问{}[{}]。", url, entity);

        String content = null;
        try {
            HttpPost post = new HttpPost(url);
            post.setConfig(getRequestConfig());
            if (entity != null)
                post.setEntity(entity);
            content = execute(post, headers, charset);
        } catch (Exception e) {
            logger.warn(e, "使用POST访问{}[{}]时发生异常！", url, entity);
        }

        if (logger.isDebugEnable())
            logger.debug("使用POST访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public Map<String, String> download(String url, Map<String, String> headers, Map<String, String> parameters, String charset, String dest) {
        if (validator.isEmpty(parameters))
            return download(url, headers, "", dest);

        StringBuilder sb = new StringBuilder();
        parameters.forEach((name, value) -> sb.append('&').append(name).append('=').append(converter.encodeUrl(parameters.get(name), charset)));

        return download(url, headers, sb.substring(1), dest);
    }

    @Override
    public Map<String, String> download(String url, Map<String, String> headers, String parameters, String dest) {
        if (validator.isEmpty(url))
            return null;

        try {
            if (!new File(dest.substring(0, dest.lastIndexOf('/'))).mkdirs())
                return null;

            Map<String, String> map = download(url, headers, parameters, new FileOutputStream(dest));

            if (logger.isDebugEnable())
                logger.debug("使用GET下载文件[{}]到[{}]。", url, dest);

            return map;
        } catch (Exception e) {
            logger.warn(e, "使用GET下载文件[{}]时发生异常！", url);

            return null;
        }
    }

    @Override
    public Map<String, String> download(String url, Map<String, String> headers, String parameters, OutputStream outputStream) {
        if (validator.isEmpty(url))
            return null;

        if (!validator.isEmpty(parameters))
            url = url + (url.indexOf('?') == -1 ? '?' : '&') + parameters;

        if (logger.isDebugEnable())
            logger.debug("使用GET下载文件[{}]。", url);

        try {
            HttpGet get = new HttpGet(url);
            get.setConfig(getRequestConfig());
            CloseableHttpResponse response = execute(get, headers);
            Map<String, String> map = toMap(response.getAllHeaders());
            InputStream inputStream = response.getEntity().getContent();
            io.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            response.close();

            if (logger.isDebugEnable())
                logger.debug("下载文件Header[{}]信息。", converter.toString(map));

            return map;
        } catch (Exception e) {
            logger.warn(e, "使用GET下载文件[{}]时发生异常！", url);

            return null;
        }
    }

    private Map<String, String> toMap(Header[] headers) {
        Map<String, String> map = new HashMap<>();
        for (Header header : headers)
            map.put(header.getName(), header.getValue());

        return map;
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
    }

    private String execute(HttpUriRequest request, Map<String, String> headers, String charset) throws IOException {
        CloseableHttpResponse response = execute(request, headers);
        statusCode.set(response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity(), context.getCharset(charset));
        response.close();

        return content;
    }

    private CloseableHttpResponse execute(HttpUriRequest request, Map<String, String> headers) throws IOException {
        if (!validator.isEmpty(headers)) {
            headers.forEach((name, value) -> {
                if (!name.toLowerCase().equals("content-length"))
                    request.addHeader(name, value);
            });
        }
        request.addHeader("time-hash", converter.toString(timeHash.generate()));

        return HttpClients.custom().setConnectionManager(manager).build().execute(request, HttpClientContext.create());
    }

    @Override
    public int getStatusCode() {
        Integer code = statusCode.get();

        return code == null ? 0 : code;
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, null);
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory).build();
            manager = new PoolingHttpClientConnectionManager(registry);
            manager.setMaxTotal(max);
            manager.setDefaultMaxPerRoute(max >> 3);
        } catch (Exception e) {
            logger.warn(e, "初始化HTTP/S客户端时发生异常！");
        }
    }
}
