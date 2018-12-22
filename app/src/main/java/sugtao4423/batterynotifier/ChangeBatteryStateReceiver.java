package sugtao4423.batterynotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import sugtao4423.batterynotifier.data.Notify;
import sugtao4423.batterynotifier.data.SlackAccount;
import sugtao4423.batterynotifier.data.TwitterAccount;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ChangeBatteryStateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPercent = (int)(level / (float)scale * 100);

        App app = (App)context.getApplicationContext();
        if(app.getPreviousBatteryPercent() == batteryPercent){
            return;
        }else{
            app.setPreviousBatteryPercent(batteryPercent);
        }

        ArrayList<Notify> notify;
        try{
            notify = ((App)context.getApplicationContext()).getDbUtils().getEnabledNotifies();
        }catch(JSONException e){
            Toast.makeText(context.getApplicationContext(), R.string.error_parse_json, Toast.LENGTH_LONG).show();
            return;
        }

        for(Notify f : notify){
            if(batteryPercent != f.getThreshold()){
                continue;
            }

            if(f.getAccountType() == Enum.AccountType.SLACK){
                sendSlack((SlackAccount)f.getAccount(), f.getText());
            }else if(f.getAccountType() == Enum.AccountType.TWITTER){
                sendTwitter((TwitterAccount)f.getAccount(), f.getText());
            }
        }
    }

    public void sendSlack(SlackAccount slackAccount, String text){
        final Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("slack.com");
        uriBuilder.path("/api/chat.postMessage");
        uriBuilder.appendQueryParameter("token", slackAccount.getToken());
        uriBuilder.appendQueryParameter("channel", slackAccount.getChannel());
        if(slackAccount.getIconType() == Enum.SlackIconType.EMOJI){
            uriBuilder.appendQueryParameter("icon_emoji", slackAccount.getIcon());
        }else if(slackAccount.getIconType() == Enum.SlackIconType.URL){
            uriBuilder.appendQueryParameter("icon_url", slackAccount.getIcon());
        }
        uriBuilder.appendQueryParameter("username", slackAccount.getUsername());
        uriBuilder.appendQueryParameter("text", text);

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... Void){
                try{
                    URL url = new URL(uriBuilder.build().toString());
                    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String str = "";
                    String body;
                    while((body = reader.readLine()) != null){
                        str += body + "\n";
                    }
                    return str;
                }catch(IOException e){
                }
                return null;
            }
        }.execute();
    }

    public void sendTwitter(TwitterAccount twitterAccount, final String text){
        Configuration conf = new ConfigurationBuilder()
                .setOAuthConsumerKey(twitterAccount.getConsumerKey())
                .setOAuthConsumerSecret(twitterAccount.getConsumerSecret())
                .build();
        AccessToken accessToken = new AccessToken(twitterAccount.getAccessToken(), twitterAccount.getAccessTokenSecret());
        final Twitter twitter = new TwitterFactory(conf).getInstance(accessToken);
        new AsyncTask<Void, Void, Status>(){
            @Override
            protected twitter4j.Status doInBackground(Void... Void){
                try{
                    return twitter.updateStatus(text);
                }catch(TwitterException e){
                }
                return null;
            }
        }.execute();
    }

}
