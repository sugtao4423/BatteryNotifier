package sugtao4423.batterynotifier.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONException;

import java.util.ArrayList;

import sugtao4423.batterynotifier.Enum;
import sugtao4423.batterynotifier.data.Account;
import sugtao4423.batterynotifier.data.Notify;
import sugtao4423.batterynotifier.data.SlackAccount;
import sugtao4423.batterynotifier.data.TwitterAccount;

public class DBUtils{

    private SQLiteDatabase db;

    public DBUtils(Context context){
        db = new Database(context).getWritableDatabase();
    }

    public void dbClose(){
        db.close();
    }

    public ArrayList<? super Account> getAccounts() throws JSONException{
        ArrayList<? super Account> result = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT kind, json FROM accounts", null);
        while(c.moveToNext()){
            String kind = c.getString(0);
            String json = c.getString(1);
            if(kind.equals(Enum.AccountType.SLACK.toString())){
                result.add(new SlackAccount(json));
            }else if(kind.equals(Enum.AccountType.TWITTER.toString())){
                result.add(new TwitterAccount(json));
            }
        }
        c.close();
        return result;
    }

    public ArrayList<String> getAccountNames() throws JSONException{
        ArrayList<String> result = new ArrayList<>();
        for(Object object : getAccounts()){
            String name = "";
            if(object instanceof SlackAccount){
                SlackAccount slack = (SlackAccount)object;
                name = Enum.AccountType.SLACK.toString() + ": " + slack.getUsername() + " #" + slack.getChannel();
            }else if(object instanceof TwitterAccount){
                name = Enum.AccountType.TWITTER.toString() + ": " + ((TwitterAccount)object).getScreenName();
            }
            result.add(name);
        }
        return result;
    }

    public ArrayList<Notify> getNotifies() throws JSONException{
        ArrayList<Notify> result = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT accounts.kind, accounts.json, notifies.enable, notifies.threshold, notifies.text FROM notifies " +
                        "INNER JOIN accounts ON notifies.account_id = accounts.id",
                null);
        while(c.moveToNext()){
            Enum.AccountType accountType = (c.getString(0).equals(Enum.AccountType.SLACK.toString())) ? Enum.AccountType.SLACK : Enum.AccountType.TWITTER;
            Object account = null;
            if(accountType == Enum.AccountType.SLACK){
                account = new SlackAccount(c.getString(1));
            }else if(accountType == Enum.AccountType.TWITTER){
                account = new TwitterAccount(c.getString(1));
            }

            boolean isEnable = Boolean.valueOf(c.getString(2));
            int threshold = Integer.parseInt(c.getString(3));
            String text = c.getString(4);

            Notify notify = new Notify(accountType, account, isEnable, threshold, text);
            result.add(notify);
        }
        return result;
    }

    public ArrayList<Notify> getEnabledNotifies() throws JSONException{
        ArrayList<Notify> result = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT accounts.kind, accounts.json, notifies.enable, notifies.threshold, notifies.text FROM notifies " +
                        "INNER JOIN accounts ON notifies.account_id = accounts.id WHERE notifies.enable = 'true'",
                null);
        while(c.moveToNext()){
            Enum.AccountType accountType = (c.getString(0).equals(Enum.AccountType.SLACK.toString())) ? Enum.AccountType.SLACK : Enum.AccountType.TWITTER;
            Object account = null;
            if(accountType == Enum.AccountType.SLACK){
                account = new SlackAccount(c.getString(1));
            }else{
                account = new TwitterAccount(c.getString(1));
            }

            boolean isEnable = Boolean.valueOf(c.getString(2));
            int threshold = Integer.parseInt(c.getString(3));
            String text = c.getString(4);

            Notify notify = new Notify(accountType, account, isEnable, threshold, text);
            result.add(notify);
        }
        return result;
    }

    public void addAccount(Object account) throws SQLException{
        Enum.AccountType accountType = null;
        if(account instanceof SlackAccount){
            accountType = Enum.AccountType.SLACK;
        }else if(account instanceof TwitterAccount){
            accountType = Enum.AccountType.TWITTER;
        }else{
            return;
        }
        String json = ((Account)account).getJsonString();

        String sql = "INSERT INTO accounts (kind, json) VALUES (?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, accountType.toString());
        stmt.bindString(2, json);
        stmt.execute();
    }

    public void addNotify(Notify notify) throws SQLException{
        String sql = "INSERT INTO notifies (account_id, enable, threshold, text) VALUES (((SELECT id FROM accounts WHERE kind = ? AND json = ?)), ?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, notify.getAccountType().toString());
        stmt.bindString(2, ((Account)notify.getAccount()).getJsonString());
        stmt.bindString(3, String.valueOf(notify.getIsEnable()));
        stmt.bindLong(4, notify.getThreshold());
        stmt.bindString(5, notify.getText());
        stmt.execute();
    }

    public void updateNotifyEnable(Notify notify, boolean enable){
        String sql = "UPDATE notifies SET enable = ? WHERE account_id = (SELECT id FROM accounts WHERE kind = ? AND json = ?) " +
                "AND enable = ? AND threshold = ? AND text = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, String.valueOf(enable));
        stmt.bindString(2, notify.getAccountType().toString());
        stmt.bindString(3, ((Account)notify.getAccount()).getJsonString());
        stmt.bindString(4, String.valueOf(!enable));
        stmt.bindLong(5, notify.getThreshold());
        stmt.bindString(6, notify.getText());
        stmt.execute();
    }

}
