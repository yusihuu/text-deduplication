package com.yusihu.text.dedup.http;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author yusihu
 * @date 2024-07-04 17:49
 */
@Slf4j
public class HttpDelegator {

    /**
     * 最多同时连接20个请求
     */
    protected static final int MAX = 600;
    /**
     * 每个路由最大连接数，路由指IP+PORT或者域名
     */
    protected static final int MAX_PER_ROUTE = 400;
    /**
     * 创建连接时间
     */
    protected static final int TIMEOUT = 1000;
    /**
     * 从连接池获取连接时间
     */
    protected static final int CONNECTION_TIMEOUT = 3000;
    /**
     * 数据传输时间
     */
    protected static final int SOCKET_TIMEOUT = 10 * TIMEOUT;

    @Getter
    @Setter
    protected Map<String, String> headers = new HashMap<>();

    protected HttpClient client;




    /**
     *  constructor
     */
    public HttpDelegator() {
        this(TIMEOUT);
    }
    public HttpDelegator(int timeout) {
        this(timeout, CONNECTION_TIMEOUT, SOCKET_TIMEOUT);
    }
    public HttpDelegator(int timeout, int connectionTimeout, int socketTimeout) {
        this(timeout, connectionTimeout, socketTimeout, MAX, MAX_PER_ROUTE);
    }
    public HttpDelegator(int timeout, int connectionTimeout, int socketTimeout, int maxRoute, int maxPerRoute ) {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(maxRoute);
        manager.setDefaultMaxPerRoute(maxPerRoute);
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        client = HttpClientBuilder
                .create()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(config)
                .build();
    }



    // ====================================================== Method ===================================================

    /**
     *
     * @param url
     * @return
     */
    public String get(String url) {
        HttpGet request = getRequest(url);
        addHeadersToRequest(request);
        try {
            HttpResponse resp = client.execute(request);
            HttpEntity entity = resp.getEntity();

            return this.doParseEntity(entity, request);
        } catch (Exception e) {
            if(log.isErrorEnabled()) {
                log.error(url, e);
            }
        }
        return null;
    }

    /**
     * get ByteArray data object from HTTP server
     * @param url the url string to get ByteArray
     * @return ByteArray object
     */
    public byte[] getBytes(String url) {
        HttpGet request = getRequest(url);
        try {
            HttpResponse resp = client.execute(request);
            HttpEntity entity = resp.getEntity();

            if (entity != null) {
                InputStream inStream = entity.getContent();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int size = 0;
                byte[] buffer = new byte[1024];
                while ((size = inStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, size);
                }

                return bos.toByteArray();
            }

        } catch (Exception e) {
            if(log.isErrorEnabled()) {
                log.error(url, e);
            }
        }
        return null;
    }

    /**
     *
     * @param url
     * @param params
     * @return
     */
    public String post(String url, Map<String, ?> params) {
        HttpPost request = postRequest(url);
        addHeadersToRequest(request);
        HttpEntity entity;
        List<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
        for (Map.Entry<String, ?> param : params.entrySet()) {
            if (param.getValue() != null) {
                list.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
            }
        }
        try {
            entity = new UrlEncodedFormEntity(list, "UTF-8");
            request.setEntity(entity);
        } catch (UnsupportedEncodingException e2) {
            if(log.isErrorEnabled()) {
                log.error(url, e2);
            }
        }

        return this.doPost(request, url);
    }

    /**
     * POST data object in ByteArray to HTTP server
     * @param url the url of server to post data
     * @param params the data to be posted, all values are in ByteArray
     * @return response from server
     */
    public String postBytes(String url, Map<String, byte[]> params) {
        HttpPost request = postRequest(url);
        HttpEntity entity;

        List<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
        for (Map.Entry<String, byte[]> param : params.entrySet()) {
            if (param.getValue() != null) {
                list.add(new BasicNameValuePair(param.getKey(), new String(param.getValue(), Charset.forName("latin1"))));
            }
        }
        try {
            entity = new UrlEncodedFormEntity(list, "latin1");
            request.setEntity(entity);

        } catch (UnsupportedEncodingException e2) {
            if(log.isErrorEnabled()) {
                log.error(url, e2);
            }
        }

        return this.doPost(request, url);
    }

    public String post(String url, String json) {
        HttpPost request = postRequest(url);
        addHeadersToRequest(request);
        HttpEntity entity;
        entity = new StringEntity(json, "UTF-8");
        request.setEntity(entity);

        return this.doPost(request, url);
    }

    public HttpResponse getResponse(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        return client.execute(request);
    }
    public synchronized void addHeader(String header, String value) {
        headers.put(header, value);
    }

    public synchronized void cleanHeader(){
        headers.clear();
    }







    // ---------------------------------------------- Tool -----------------------------------------------------


    private void addHeadersToRequest(HttpRequestBase request) {
        if (!headers.isEmpty()){
            for (String key: headers.keySet()){
                request.addHeader(key, headers.get(key));
            }
        }
    }

    private HttpGet getRequest(String url) {
        return new HttpGet(url);
    }

    private HttpPost postRequest(String url) {
        return new HttpPost(url);
    }


    /**
     *
     * @param request
     * @param url
     * @return
     */
    private String doPost(HttpPost request, String url) {

        HttpEntity entity = null;

        try {
            HttpResponse resp = client.execute(request);
            entity = resp.getEntity();

            return doParseEntity(entity, request);
        } catch (ClientProtocolException e) {
            if(log.isErrorEnabled()) {
                log.error(url, e);
            }
        } catch (IOException e) {
            if (e instanceof java.net.SocketTimeoutException) {
                if(log.isInfoEnabled()) {
                    log.info(url, e);
                }
            } else {
                if(log.isErrorEnabled()) {
                    log.error(url, e);
                }
            }
        }

        return null;
    }

    /**
     * 支持  Content-type 为  application/json 的post提交
     * @param url
     * @param jsonStr
     * @return
     */
    public String postJson(String url, String jsonStr) {
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        HttpEntity entity;

        try {
            ContentType contentType = ContentType.create("application/json", Charset.forName("utf-8"));
            entity = new StringEntity(jsonStr, contentType);

            request.setEntity(entity);
        } catch (Exception e2) {
            if (log.isErrorEnabled()) {
                log.error(url, e2);
            }
        }

        try {
            HttpResponse resp = client.execute(request);
            entity = resp.getEntity();

            String respStr = doParseEntity(entity, request);
            return respStr;
        } catch (ClientProtocolException e) {
            if (log.isErrorEnabled()) {
                log.error(url, e);
            }
        } catch (IOException e) {
            if (e instanceof java.net.SocketTimeoutException) {
                if (log.isInfoEnabled()) {
                    log.info(url, e);
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error(url, e);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param entity
     * @param request
     * @return
     * @throws IOException
     */
    private String doParseEntity(HttpEntity entity, HttpRequestBase request) throws IOException{

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
                // do something useful with the response
                StringBuffer sb = new StringBuffer();
                char[] buffer = new char[1024];
                int size;
                while ((size = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, size);
                }
                return sb.toString();
            } catch (IOException ex) {
                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;
            } catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                request.abort();
                throw ex;
            } finally {
                // Closing the input stream will trigger connection release
                instream.close();
            }
        }

        return null;
    }

}
