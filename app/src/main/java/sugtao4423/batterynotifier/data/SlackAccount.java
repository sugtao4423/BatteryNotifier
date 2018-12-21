package sugtao4423.batterynotifier.data;

import org.json.JSONException;
import org.json.JSONObject;

import sugtao4423.batterynotifier.Enum;

public class SlackAccount extends Account{

    public static final String KEY_TOKEN = "token";
    public static final String KEY_CHANNEL = "channel";
    public static final String KEY_ICON = "icon";
    public static final String KEY_ICON_TYPE = "iconType";
    public static final String KEY_USERNAME = "username";

    private String token;
    private String channel;
    private String icon;
    private Enum.SlackIconType iconType;
    private String username;

    public SlackAccount(String json) throws JSONException{
        super(Enum.AccountType.SLACK, json);
    }

    @Override
    protected void decodeJson(JSONObject json) throws JSONException{
        token = json.getString(KEY_TOKEN);
        channel = json.getString(KEY_CHANNEL);
        icon = json.getString(KEY_ICON);
        iconType = (json.getString(KEY_ICON_TYPE).equalsIgnoreCase(Enum.SlackIconType.EMOJI.toString())) ? Enum.SlackIconType.EMOJI : Enum.SlackIconType.URL;
        username = json.getString(KEY_USERNAME);
    }

    public String getToken(){
        return token;
    }

    public String getChannel(){
        return channel;
    }

    public String getIcon(){
        return icon;
    }

    public Enum.SlackIconType getIconType(){
        return iconType;
    }

    public String getUsername(){
        return username;
    }

}
