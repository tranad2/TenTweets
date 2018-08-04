package tentweets.app.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;
import tentweets.app.R;
import tentweets.app.rest.RestApiClient;
import tentweets.app.util.SessionManager;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private TextView mDescView;
    private EditText mSearchView;
    private Button btnSearch;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mDescView = view.findViewById(R.id.app_desc);
        mDescView.setText("This tool will find 10 of your most popular recent Tweets. Perfect for finding the best of your recent contents.");
        mSearchView = view.findViewById(R.id.screen_name);
        btnSearch = view.findViewById(R.id.tweet_search_button);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = getScreenName();
                Log.d(TAG, name);
                Log.d(TAG, "Button pressed");
                //Close keyboard on button press
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                TweetFragment fragment = TweetFragment.newInstance(name);
                getFragmentManager().beginTransaction().replace(R.id.search_fragment, fragment).addToBackStack(null).commit();
            }
        });
        return view;
    }

    public String getScreenName(){
        return mSearchView.getText().toString();
    }

}
