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
import org.lwd.frame.bean.ContextRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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
    private Coder coder;
    @Inject
    private Numeric numeric;
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
    public String get(String url, Map<String, String> requestHeaders, Map<String, String> parameters) {
        return get(url, requestHeaders, parameters, null);
    }

    @Override
    public String get(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset) {
        return get(url, requestHeaders, toStringParameters(parameters, charset));
    }

    private String toStringParameters(Map<String, String> parameters, String charset) {
        if (validator.isEmpty(parameters))
            return "";

        StringBuilder sb = new StringBuilder();
        parameters.forEach((name, value) -> sb.append('&').append(name).append('=').append(coder.encodeUrl(value, charset)));

        return sb.substring(1);
    }

    @Override
    public String get(String url, Map<String, String> requestHeaders, String parameters) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        get(url, requestHeaders, parameters, null, outputStream);
        String content = outputStream.toString();

        if (logger.isDebugEnable())
            logger.debug("使用GET访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public void get(String url, Map<String, String> requestHeaders, String parameters,
                    Map<String, String> responseHeaders, OutputStream outputStream) {
        if (validator.isEmpty(url))
            return;

        if (!validator.isEmpty(parameters))
            url = url + (url.indexOf('?') == -1 ? '?' : '&') + parameters;

        if (logger.isDebugEnable())
            logger.debug("使用GET访问[{}]。", url);

        HttpGet get = new HttpGet(url);
        get.setConfig(getRequestConfig());
        execute(get, requestHeaders, responseHeaders, outputStream);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, Map<String, String> parameters) {
        return post(url, requestHeaders, parameters, null);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset) {
        return post(url, requestHeaders, toEntiry(parameters, charset));
    }

    @Override
    public void post(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset,
                     Map<String, String> responseHeaders, OutputStream outputStream) {
        postByEntity(url, requestHeaders, toEntiry(parameters, charset), responseHeaders, outputStream);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, String content) {
        return post(url, requestHeaders, content, null);
    }

    @Override
    public String post(String url, Map<String, String> requestHeaders, String content, String charset) {
        return post(url, requestHeaders, toEntiry(content, charset));
    }

    private String post(String url, Map<String, String> requestHeaders, HttpEntity entity) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        postByEntity(url, requestHeaders, entity, null, outputStream);
        String content = outputStream.toString();
        if (logger.isDebugEnable())
            logger.debug("使用POST访问[{}]结果[{}]。", url, content);

        return content;
    }

    @Override
    public void post(String url, Map<String, String> requestHeaders, String content, String charset,
                     Map<String, String> responseHeaders, OutputStream outputStream) {
        postByEntity(url, requestHeaders, toEntiry(content, charset), responseHeaders, outputStream);
    }

    @Override
    public String upload(String url, Map<String, String> requestHeaders, Map<String, String> parameters, Map<String, File> files) {
        return upload(url, requestHeaders, parameters, files, null);
    }

    @Override
    public String upload(String url, Map<String, String> requestHeaders, Map<String, String> parameters,
                         Map<String, File> files, String charset) {
        if (validator.isEmpty(files))
            return post(url, requestHeaders, parameters, charset);

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        ContentType contentType = ContentType.create("text/plain", context.getCharset(charset));
        if (!validator.isEmpty(parameters))
            parameters.forEach((key, value) -> entity.addTextBody(key, value, contentType));
        files.forEach(entity::addBinaryBody);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        postByEntity(url, requestHeaders, entity.build(), null, outputStream);

        return outputStream.toString();
    }

    private HttpEntity toEntiry(String parameters, String charset) {
        if (validator.isEmpty(parameters))
            return null;

        return new StringEntity(parameters, context.getCharset(charset));
    }

    private HttpEntity toEntiry(Map<String, String> map, String charset) {
        if (validator.isEmpty(map))
            return null;

        List<NameValuePair> nvps = new ArrayList<>();
        map.forEach((key, value) -> nvps.add(new BasicNameValuePair(key, value)));

        try {
            return new UrlEncodedFormEntity(nvps, context.getCharset(charset));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e, "转化参数[{}:{}]时发生异常！", converter.toString(map), charset);

            return null;
        }
    }

    private void postByEntity(String url, Map<String, String> requestHeaders, HttpEntity entity,
                              Map<String, String> responseHeaders, OutputStream outputStream) {
        if (validator.isEmpty(url))
            return;

        if (logger.isDebugEnable())
            logger.debug("使用POST访问{}[{}]。", url, entity);

        HttpPost post = new HttpPost(url);
        post.setConfig(getRequestConfig());
        if (entity != null)
            post.setEntity(entity);
        execute(post, requestHeaders, responseHeaders, outputStream);
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
    }

    private void execute(HttpUriRequest request, Map<String, String> requestHeaders, Map<String, String> responseHeaders,
                         OutputStream outputStream) {
        if (!validator.isEmpty(requestHeaders))
            requestHeaders.keySet().stream().filter(key -> !key.toLowerCase().equals("content-length"))
                    .forEach(key -> request.addHeader(key, requestHeaders.get(key)));
        request.addHeader("time-hash", numeric.toString(timeHash.generate(), "0"));
        try {
            CloseableHttpResponse response = HttpClients.custom().setConnectionManager(manager).build()
                    .execute(request, HttpClientContext.create());
            if (responseHeaders != null)
                for (Header header : response.getAllHeaders())
                    responseHeaders.put(header.getName(), header.getValue());
            statusCode.set(response.getStatusLine().getStatusCode());
            io.copy(response.getEntity().getContent(), outputStream);
            response.close();
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "执行HTTP请求时发生异常！");
        }
    }

    @Override
    public int getStatusCode() {
        return numeric.toInt(statusCode.get());
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
