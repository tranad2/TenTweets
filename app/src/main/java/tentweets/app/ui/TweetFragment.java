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
import org.w3c.dom.Text;

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
    private TweetsAdapter tweetsAdapter;

    public TweetFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param screen_name Twitter Username
     * @return A new instance of fragment TweetFragment.
     */
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
        tweetsAdapter = new TweetsAdapter(this.getContext(),timeline);
        tweetRecView.setAdapter(tweetsAdapter);
        tweetRecView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));



        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(tweetRecView.getContext(),
                mLayoutManager.getOrientation());
        tweetRecView.addItemDecoration(dividerItemDecoration);

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
                Log.d("Success",response.toString());
                try{
                    Log.d("UserTimeLine", response.toString());
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

                    for(int i = 0; i < response.length(); i++){
                        JSONObject tweet = response.getJSONObject(i);
                        arr.add(tweet);
                    }
                    sortListDescending(arr);

                    for(int i = 0; i < 10; i++){
                        JSONObject obj = arr.get(i);
                        UserDetails detail = new UserDetails(obj);
                        timeline.add(detail);
                        Log.d(TAG,"Details: "+ detail.toString());
                        Log.d(TAG,"JSONObject "+obj.toString());
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
                    Log.d("Header", head.toString());
                }
                Log.d("TimelineFailure1","Status: "+statusCode+" Response: "+responseError.toString());
                try{
                    JSONArray errors = responseError.getJSONArray("errors");
                    JSONObject error = errors.getJSONObject(0);
                    String message = error.getString("message");
                    Log.d(TAG, message);
                    Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                throwable.printStackTrace();
                Log.d("TimelineFailure2","Status: "+statusCode+" Response: "+responseError.toString());
                Toast.makeText(getActivity(),responseError, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sortListDescending(ArrayList<JSONObject> arr){
        Collections.sort(arr, new Comparator<JSONObject>() {        // Sort in descending order of favorite_count

            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    return ((rhs.getInt("favorite_count") - (lhs.getInt("favorite_count"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    private class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.MyViewHolder>{
        private List<UserDetails> tweets_list;
        private Context context;

        //Adapter constructor
        public TweetsAdapter(Context context, List<UserDetails> tweets_list) {
            this.tweets_list = tweets_list;
            this.context = context;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tweetCreatedView, tweetTextView, tweetFavoritesView, tweetRankView;

            MyViewHolder(View view) {
                super(view);
                tweetRankView = (TextView) view.findViewById(R.id.tweet_rank);
                tweetFavoritesView = (TextView) view.findViewById(R.id.tweet_favorites);
                tweetCreatedView = (TextView) view.findViewById(R.id.tweet_created);
                tweetTextView = (TextView) view.findViewById(R.id.tweet_text);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_detail, parent, false);
            // Return a new holder instance
            Log.d("MyViewHolder","Items: "+getItemCount());
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final UserDetails tweet = tweets_list.get(position);
            int rank = position + 1;
            Log.d(TAG,"Size: "+tweets_list.size());

            holder.tweetRankView.setText(""+rank);
            holder.tweetFavoritesView.setText("Favorites: "+tweet.getFavoriteCount());
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
