package tentweets.app.data;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDetails {

    private String idStr;
    private String text;
    private boolean truncated;

    private boolean isQuoteStatus;
    private String createdAt;
    private int retweetCount;
    private int favoriteCount;
    private boolean favorited;
    private boolean retweeted;
    private String lang;

    private JSONObject json;

    public UserDetails(JSONObject obj){
        try {
            this.json = obj;
            createdAt = obj.getString("created_at");
            idStr = obj.getString("id_str");
            text = obj.getString("text");
            retweetCount = obj.getInt("retweet_count");
            favoriteCount = obj.getInt("favorite_count");
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public UserDetails(String createdAt, String idStr, String text, int favouritesCount, int retweetCount){
        this.createdAt = createdAt;
        this.idStr = idStr;
        this.text = text;
        this.favoriteCount = favouritesCount;
        this.retweetCount = retweetCount;
    }

    public JSONObject getJSONObject(){
        return json;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favouritesCount) {
        this.favoriteCount = favouritesCount;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String toString(){
        String str = "UserDetails: \n" +
                "created_at: " + createdAt + "\n" +
                "id_str: " + idStr + "\n" +
                "text: " + text + "\n" +
                "retweet_count: " + retweetCount + "\n" +
                "favorite_count: " + favoriteCount + "\n";
        return str;
    }

}