package retrofit.utils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import utils.PropertyScanner;

public class RetrofitGetter {

    private HttpLoggingInterceptor logger;
    private PropertyScanner ps;
    private OkHttpClient client;
    private String baseUrl;

    public RetrofitGetter() throws IOException {
        ps = new PropertyScanner();
        logger = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        baseUrl = ps.getProperty("mini.market.url");
        client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .build();
    }

    public Retrofit getInstance() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

}