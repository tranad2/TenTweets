package tentweets.app;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import tentweets.app.data.UserDetails;

@RunWith(AndroidJUnit4.class)
public class UserDetailsUnitTest {

    private UserDetails mUserDetails;


    @Before
    public void createUserDetails() {
        JSONObject tweet = new JSONObject();
        try {
            tweet.put("created_at", "Thu Apr 06 15:28:43 +0000 2017");
            tweet.put("id_str","12345");
            tweet.put("text", "This is a test Tweet!");
            tweet.put("retweet_count", 284);
            tweet.put("favorite_count", 399);
            mUserDetails = new UserDetails(tweet);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testToString() {
        String expected = "UserDetails: \n" +
                "created_at: " + "Thu Apr 06 15:28:43 +0000 2017" + "\n" +
                "id_str: " + "12345" + "\n" +
                "text: " + "This is a test Tweet!" + "\n" +
                "retweet_count: " + 284 + "\n" +
                "favorite_count: " + 399 + "\n";
        Assert.assertEquals(expected, mUserDetails.toString());
    }
}
