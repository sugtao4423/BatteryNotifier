package sugtao4423.batterynotifier;

import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import sugtao4423.batterynotifier.data.SlackAccount;
import sugtao4423.batterynotifier.data.TwitterAccount;

public class AddAccountActivity extends AppCompatActivity{

    private Enum.AccountType accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(spinner.getSelectedItemPosition() == Enum.AccountType.SLACK.getPosition()){
                    accountType = Enum.AccountType.SLACK;
                }else if(spinner.getSelectedItemPosition() == Enum.AccountType.TWITTER.getPosition()){
                    accountType = Enum.AccountType.TWITTER;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, AddAccountFragment.newInstance(accountType), accountType.toString())
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try{
                    ((AddAccountFragment)getSupportFragmentManager().findFragmentByTag(accountType.toString())).clickFloatButton();
                }catch(JSONException e){
                    Toast.makeText(getApplicationContext(), R.string.error_convert_json, Toast.LENGTH_LONG).show();
                }catch(SQLException e){
                    Toast.makeText(getApplicationContext(), R.string.error_sql_fail_add, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public static class AddAccountFragment extends Fragment{

        private static final String ARG_ACCOUNT_TYPE = "account_type";

        public AddAccountFragment(){
        }

        public static AddAccountFragment newInstance(Enum.AccountType accountType){
            AddAccountFragment fragment = new AddAccountFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_ACCOUNT_TYPE, accountType);
            fragment.setArguments(args);
            return fragment;
        }

        private Enum.AccountType type;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            type = (Enum.AccountType)getArguments().get(ARG_ACCOUNT_TYPE);
            View rootView = null;
            if(type == Enum.AccountType.SLACK){
                rootView = inflater.inflate(R.layout.fragment_add_account_slack, container, false);
            }else if(type == Enum.AccountType.TWITTER){
                rootView = inflater.inflate(R.layout.fragment_add_account_twitter, container, false);
            }
            return rootView;
        }

        public void clickFloatButton() throws JSONException, SQLException{
            if(type == Enum.AccountType.SLACK){
                addSlack();
            }else if(type == Enum.AccountType.TWITTER){
                addTwitter();
            }
        }

        public void addSlack() throws JSONException, SQLException{
            View v = getView();
            String token = ((EditText)v.findViewById(R.id.token)).getText().toString();
            String channel = ((EditText)v.findViewById(R.id.channel)).getText().toString();
            String icon = ((EditText)v.findViewById(R.id.icon)).getText().toString();
            boolean iconIsEmoji = ((CheckBox)v.findViewById(R.id.icon_is_emoji)).isChecked();
            String username = ((EditText)v.findViewById(R.id.username)).getText().toString();

            channel = channel.replaceAll("#", "");

            if(token.isEmpty() || channel.isEmpty() || icon.isEmpty() || username.isEmpty()){
                showEmptyError();
                return;
            }

            JSONObject json = new JSONObject();
            json.put(SlackAccount.KEY_TOKEN, token);
            json.put(SlackAccount.KEY_CHANNEL, channel);
            json.put(SlackAccount.KEY_ICON, icon);
            json.put(SlackAccount.KEY_ICON_TYPE, iconIsEmoji ? Enum.SlackIconType.EMOJI.toString() : Enum.SlackIconType.URL.toString());
            json.put(SlackAccount.KEY_USERNAME, username);

            SlackAccount account = new SlackAccount(json.toString());
            ((App)getContext().getApplicationContext()).getDbUtils().addAccount(account);
            getActivity().finish();
        }

        public void addTwitter() throws JSONException, SQLException{
            View v = getView();
            String screenName = ((EditText)v.findViewById(R.id.screen_name)).getText().toString();
            String consumerKey = ((EditText)v.findViewById(R.id.consumer_key)).getText().toString();
            String consumerSecret = ((EditText)v.findViewById(R.id.consumer_secret)).getText().toString();
            String accessToken = ((EditText)v.findViewById(R.id.access_token)).getText().toString();
            String accessTokenSecret = ((EditText)v.findViewById(R.id.access_token_secret)).getText().toString();

            if(screenName.isEmpty() || consumerKey.isEmpty() || consumerSecret.isEmpty() || accessToken.isEmpty() || accessTokenSecret.isEmpty()){
                showEmptyError();
                return;
            }

            JSONObject json = new JSONObject();
            json.put(TwitterAccount.KEY_SCREEN_NAME, screenName);
            json.put(TwitterAccount.KEY_CONSUMER_KEY, consumerKey);
            json.put(TwitterAccount.KEY_CONSUMER_SECRET, consumerSecret);
            json.put(TwitterAccount.KEY_ACCESS_TOKEN, accessToken);
            json.put(TwitterAccount.KEY_ACCESS_TOKEN_SECRET, accessTokenSecret);

            TwitterAccount account = new TwitterAccount(json.toString());
            ((App)getContext().getApplicationContext()).getDbUtils().addAccount(account);
            getActivity().finish();
        }

        public void showEmptyError(){
            Snackbar.make(getView(), R.string.fill_all_input_box, Snackbar.LENGTH_LONG).show();
        }
    }

}
