package tentweets.app.rest;

import android.util.Log;

import com.loopj.android.http.*;

public class RestApiClient {

    private final String BASE_URL = "https://api.twitter.com/";
    private AsyncHttpClient client = new AsyncHttpClient();

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setEnableRedirects(true);
        Log.v("URL",getAbsoluteUrl(url));
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.setEnableRedirects(true);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public void addHeader (String header, String value) {
        client.addHeader(header, value);
    }

    public void removeHeader(String header){client.removeHeader(header);}

    public String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
