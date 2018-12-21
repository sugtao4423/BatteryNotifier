package sugtao4423.batterynotifier.data;

import org.json.JSONException;
import org.json.JSONObject;

import sugtao4423.batterynotifier.Enum;

public class TwitterAccount extends Account{

    public static final String KEY_SCREEN_NAME = "screenName";
    public static final String KEY_CONSUMER_KEY = "consumerKey";
    public static final String KEY_CONSUMER_SECRET = "consumerSecret";
    public static final String KEY_ACCESS_TOKEN = "accessToken";
    public static final String KEY_ACCESS_TOKEN_SECRET = "accessTokenSecret";

    private String screenName;
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public TwitterAccount(String json) throws JSONException{
        super(Enum.AccountType.TWITTER, json);
    }

    @Override
    protected void decodeJson(JSONObject json) throws JSONException{
        screenName = json.getString(KEY_SCREEN_NAME);
        consumerKey = json.getString(KEY_CONSUMER_KEY);
        consumerSecret = json.getString(KEY_CONSUMER_SECRET);
        accessToken = json.getString(KEY_ACCESS_TOKEN);
        accessTokenSecret = json.getString(KEY_ACCESS_TOKEN_SECRET);
    }

    public String getScreenName(){
        return screenName;
    }

    public String getConsumerKey(){
        return consumerKey;
    }

    public String getConsumerSecret(){
        return consumerSecret;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public String getAccessTokenSecret(){
        return accessTokenSecret;
    }

}
