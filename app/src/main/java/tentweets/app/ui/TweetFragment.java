package tentweets.app.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import tentweets.app.R;
import tentweets.app.data.UserDetails;
import tentweets.app.rest.RestApiClient;
import tentweets.app.util.SessionManager;

public class TweetFragment extends Fragment {
    private static final String TAG = "TweetFragment";
    private static final String SCREEN_NAME = "screen_name";

    private String name;
    private List<UserDetails> timeline;
    private SessionManager manager;
    private RecyclerView tweetRecView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TweetsAdapter tweetsAdapter;

    public TweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param screen_name Twitter User Name
     * @return A new instance of fragment TweetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TweetFragment newInstance(String screen_name) {
        TweetFragment fragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putString(SCREEN_NAME, screen_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(SCREEN_NAME);
        }
        timeline = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);
        getUserTimeline(name);

        tweetRecView = (RecyclerView) view.findViewById(R.id.recycler_tweet_view);

        // specify an adapter (see also next example)
        tweetsAdapter = new TweetsAdapter(timeline);
        tweetRecView.setAdapter(tweetsAdapter);
        tweetRecView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));

        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        tweetRecView.setLayoutManager(mLayoutManager);

        return view;
    }

    public void getUserTimeline(String name){
        manager = new SessionManager(getActivity().getApplicationContext());
        RestApiClient client = new RestApiClient();
        RequestParams params = new RequestParams();
        params.put("screen_name", name);
        String value = "Bearer " + manager.getToken();
        client.addHeader("Authorization", value);

        client.get("1.1/statuses/user_timeline.json", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("Success",response.toString());
                try{
                    Log.v("UserTimeLine", response.toString());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try{
                    // Pull out events on the public timeline
                    ArrayList<JSONObject> arr = new ArrayList<>();
                    //Log.d(TAG, "Timeline Length: "+response.length());
                    for(int i = 0; i < response.length(); i++){
                        JSONObject tweet = response.getJSONObject(i);
                        arr.add(tweet);
                    }
                    Collections.sort(arr, new Comparator<JSONObject>() {        // Sort in descending order of favorite_count

                        @Override
                        public int compare(JSONObject lhs, JSONObject rhs) {
                            try {
                                return ((rhs.getInt("favorite_count") - (lhs.getInt("favorite_count"))));
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    for(int i = 0; i < 10; i++){
                        JSONObject obj = arr.get(i);
                        UserDetails detail = new UserDetails(obj);
                        timeline.add(detail);
                        Log.d(TAG,"Details: "+ detail.toString());
                    }
                    tweetsAdapter.notifyDataSetChanged();
                    Log.d(TAG,"Tweets Size: "+timeline.size());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                throwable.printStackTrace();
                for(Header head: headers){
                    Log.v("Header", head.toString());
                }
                Log.v("TimelineFailure1","Status: "+statusCode+" Response: "+responseError.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                throwable.printStackTrace();
                Log.v("TimelineFailure2","Status: "+statusCode+" Response: "+responseError.toString());
            }
        });
    }

    private class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.MyViewHolder>{
        private List<UserDetails> tweets_list;

        //Adapter constructor
        public TweetsAdapter(List<UserDetails> tweets_list) {
            this.tweets_list = tweets_list;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tweetCreatedView, tweetTextView, tweetFavoritesView;

            MyViewHolder(View view) {
                super(view);
                tweetFavoritesView = (TextView) view.findViewById(R.id.tweet_favorites);
                tweetCreatedView = (TextView) view.findViewById(R.id.tweet_created);
                tweetTextView = (TextView) view.findViewById(R.id.tweet_text);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_item_detail, parent, false);
            // Return a new holder instance
            Log.d("MyViewHolder","Items: "+getItemCount());
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final UserDetails tweet = tweets_list.get(position);
            Log.d(TAG,"Size: "+tweets_list.size());

            holder.tweetFavoritesView.setText((position+1) +". Tweet: "+tweet.getFavoriteCount());
            holder.tweetCreatedView.setText("Date: "+tweet.getCreatedAt());
            holder.tweetTextView.setText("Message: \n"+tweet.getText());
        }

        @Override
        public int getItemCount() {
            Log.d(TAG,"Adapter Size"+tweets_list.size());
            return tweets_list.size();
        }
    }
}
