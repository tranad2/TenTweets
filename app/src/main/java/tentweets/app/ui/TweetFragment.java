package tentweets.app.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
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
        getUserTimeline(name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tweet, container, false);
    }

    public void getUserTimeline(String name){
        timeline = new ArrayList<>();
        SessionManager manager = new SessionManager(getActivity().getApplicationContext());
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
                    Log.d(TAG, "Timeline Length: "+response.length());
                    for(int i = 0; i < response.length(); i++){
                        JSONObject tweet = response.getJSONObject(i);
                        arr.add(tweet);
                        int fav_count = tweet.getInt("favorite_count");
                        //Log.d(TAG,"Favorites: " +fav_count);
                    }
                    Collections.sort(arr, new Comparator<JSONObject>() {        // Sort in descending order of favorite_count

                        @Override
                        public int compare(JSONObject lhs, JSONObject rhs) {
                            // TODO Auto-generated method stub

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
                    }

//                    for(UserDetails d : timeline){
//                        Log.d(TAG, d.toString());
//                    }
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

        public TweetsAdapter(List<UserDetails> tweets_list) {
            this.tweets_list = tweets_list;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView parking_id, parking_status;
            MyViewHolder(View view) {
                super(view);
                //parking_id = (TextView) view.findViewById(R.id.home_location_detail_space);
                //parking_status = (TextView) view.findViewById(R.id.home_location_detail_status);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View location_view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_item_detail, parent, false);
            // Return a new holder instance
            return new MyViewHolder(location_view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            /**final UserDetails tweet = tweets_list.get(position);
            holder.parking_id.setText("Slot ID: "+String.valueOf(location.getSpaceId()));
            holder.parking_status.setText(location.getStatus().substring(0, 1).toUpperCase() + location.getStatus().substring(1));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                int id = location.getId();
                int parking_id = location.getSpaceId();
                String status = location.getStatus();
                @Override
                public void onClick(View v) {
                    if(status.equals("unoccupied")) {
                        Toast.makeText(getActivity().getApplicationContext(), "" + id, Toast.LENGTH_LONG).show();

                        //Display dialog fragment
                    }
                }
            });
                */
        }

        @Override
        public int getItemCount() {
            return tweets_list.size();
        }
    }
}
