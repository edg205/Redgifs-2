package ru.zlsl.redgifs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class Redgifs extends Application {

    private static Application instance;
    public static String token = "";
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3346.8 Safari/537.36 Redgifs/" + BuildConfig.VERSION_NAME;
    public static Context getContext() {
        return instance;
    }

    static boolean debug = false;
    static String C__cf_bm = "";


    public static OkHttpClient httpclient = new OkHttpClient.Builder()
            .connectTimeout(100000, TimeUnit.MILLISECONDS)
            .readTimeout(100000, TimeUnit.MILLISECONDS)
            .writeTimeout(100000, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.addHeader("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
                requestBuilder.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                requestBuilder.addHeader("cache-control", "max-age=0");
                requestBuilder.addHeader("upgrade-insecure-requests", "1");
                requestBuilder.addHeader("user-agent", Redgifs.USER_AGENT);
                requestBuilder.addHeader("cookie", getCookies());
                requestBuilder.addHeader("X-CustomHeader", "https://www.redgifs.com/");
                requestBuilder.addHeader("authorization", "Bearer " + Redgifs.token);

                Request request = requestBuilder.build();

                long t1 = 0;
                if (debug) {
                    t1 = System.nanoTime();
                    Log.i("OKHTTP", String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

                    RequestBody requestBody = request.body();
                    if (requestBody != null) {
                        try {
                            String subtype = Objects.requireNonNull(requestBody.contentType()).subtype();
                            if (subtype.contains("form")) {
                                String form = "";
                                try {
                                    final Buffer buffer = new Buffer();
                                    requestBody.writeTo(buffer);
                                    form = buffer.readUtf8();
                                } catch (final IOException e) {
                                    e.printStackTrace();
                                }

                                Log.i("OKHTTP", String.format("FORM %s", form));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                Response response = chain.proceed(request);
                if (debug) {
                    long t2 = System.nanoTime();
                    Log.i("OKHTTP", String.format("%s Received response for %s in %.1fms%n%s", response.code(), response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                }

                return response;
            })
            .build();

    public static String getCookies() {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(Redgifs.getContext());
        StringBuilder coo = new StringBuilder();
        C__cf_bm = sPref.getString("__cf_bm", "");

        if (!C__cf_bm.isEmpty()) {
            coo.append("__cf_bm=").append(C__cf_bm).append(";");
        }
        return coo.toString();
    }

    public static Request getRequestBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .header("User-Agent", Redgifs.USER_AGENT)
                .addHeader("Cookie", getCookies())
                .addHeader("referer", Base64.encodeToString(url.getBytes(), Base64.NO_WRAP))
                .addHeader("origin", "https://www.redgifs.com/")
                .build();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

}