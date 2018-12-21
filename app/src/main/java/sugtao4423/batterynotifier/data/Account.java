package sugtao4423.batterynotifier.data;

import org.json.JSONException;
import org.json.JSONObject;

import sugtao4423.batterynotifier.Enum;

public abstract class Account{

    private Enum.AccountType accountType;
    private String json;

    public Account(Enum.AccountType accountType, String json) throws JSONException{
        this.accountType = accountType;
        this.json = json;
        JSONObject jsonObject = new JSONObject(json);
        decodeJson(jsonObject);
    }

    abstract protected void decodeJson(JSONObject json) throws JSONException;

    public Enum.AccountType getAccountType(){
        return accountType;
    }

    public String getJsonString(){
        return json;
    }

}
