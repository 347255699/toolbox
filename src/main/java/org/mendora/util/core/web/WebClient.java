package org.mendora.util.core.web;

import lombok.SneakyThrows;
import okhttp3.*;
import org.mendora.util.core.BeanUtil;

import java.util.Map;

public class WebClient {
    private OkHttpClient okHttpClient;

    private static class WebClientHolder {
        private static final WebClient INSTANCE = new WebClient();
    }

    private WebClient() {
        this.okHttpClient = new OkHttpClient();
    }

    public static WebClient getInstance() {
        return WebClient.WebClientHolder.INSTANCE;
    }

    @SneakyThrows
    public <T> Response postFormData(T t, HttpUrl url) {
        final Map<String, Object> body = BeanUtil.toMap(t);
        final FormBody.Builder formBuilder = new FormBody.Builder();
        body.forEach((k, v) -> formBuilder.add(k, String.valueOf(v)));
        final Request req = new Request.Builder().post(formBuilder.build()).url(url).build();
        return okHttpClient.newCall(req).execute();
    }

    @SneakyThrows
    public <T> Response get(T t, HttpUrl url){
        final HttpUrl.Builder builder = url.newBuilder();
        final Map<String, Object> body = BeanUtil.toMap(t);
        body.forEach((k, v) -> builder.addQueryParameter(k, String.valueOf(v)));
        final Request req = new Request.Builder().url(builder.build()).build();
        return okHttpClient.newCall(req).execute();
    }
}
