package tentweets.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

import tentweets.app.rest.*;
import tentweets.app.ui.SearchFragment;
import tentweets.app.util.SessionManager;

public class TenTweetsApp extends AppCompatActivity {

    public final static String TAG = "Response";

    private final static String CONSUMER_KEY = "3U0Fl5DHoHesGncx5Zs9HHTph";
    private final static String CONSUMER_SECRET = "tyRGnA9d5jLdbKGoxzsw7V8cy6ftea06tsqe5dr9l00Ozorp56";
    private String TWEET_FRAGMENT = "tweet_fragment";
    private SessionManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_app);

        manager = new SessionManager(this.getApplicationContext());

        if(manager.getToken() == null)
            getAuthToken();
        //Add if(manager.isLogged())
        SearchFragment fragment = new SearchFragment();
        getFragmentManager().beginTransaction().replace(R.id.tweets_container, fragment).commit();

        //getUserTimeline("AlxT116");
    }

    public void getAuthToken(){
        RestApiClient client = new RestApiClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("grant_type", "client_credentials");
        client.addHeader("Authorization", "Basic " + Base64.encodeToString((CONSUMER_KEY + ":" + CONSUMER_SECRET).getBytes(), Base64.NO_WRAP));
        client.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        client.post("oauth2/token", requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Success",response.toString());
                try {
                        manager.createLoginSession(response.getString("access_token"));     // Save access_token in manager
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                throwable.printStackTrace();
                Log.v("Failure",responseError.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                throwable.printStackTrace();
                Log.v("Failure",responseError.toString());
            }
        });
    }



}
