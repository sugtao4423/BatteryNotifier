package sugtao4423.batterynotifier;

import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import sugtao4423.batterynotifier.data.Account;
import sugtao4423.batterynotifier.data.Notify;
import sugtao4423.batterynotifier.data.SlackAccount;
import sugtao4423.batterynotifier.data.TwitterAccount;

public class AddNotifyActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notify);

        final Spinner accountSpinner = (Spinner)findViewById(R.id.account_spinner);
        final EditText threshold = (EditText)findViewById(R.id.threshold);
        final EditText postContent = (EditText)findViewById(R.id.post_text);

        final ArrayList<? super Account> accounts;
        try{
            accounts = ((App)getApplicationContext()).getDbUtils().getAccounts();
        }catch(JSONException e){
            Toast.makeText(getApplicationContext(), R.string.error_parse_json, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(accounts.size() <= 0){
            Toast.makeText(getApplicationContext(), R.string.error_no_accounts, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayList<String> accountNames;
        try{
            accountNames = ((App)getApplicationContext()).getDbUtils().getAccountNames();
        }catch(JSONException e){
            Toast.makeText(getApplicationContext(), R.string.error_parse_json, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accountNames);
        accountSpinner.setAdapter(spinnerAdapter);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int spinnerPos = accountSpinner.getSelectedItemPosition();
                Account account = (Account)accounts.get(spinnerPos);
                Enum.AccountType accountType = null;
                if(account instanceof SlackAccount){
                    accountType = Enum.AccountType.SLACK;
                }else if(account instanceof TwitterAccount){
                    accountType = Enum.AccountType.TWITTER;
                }

                boolean isEnable = false;
                int thresh = Integer.parseInt(threshold.getText().toString());
                String text = postContent.getText().toString();

                Notify notify = new Notify(accountType, account, isEnable, thresh, text);
                try{
                    ((App)getApplicationContext()).getDbUtils().addNotify(notify);
                    finish();
                }catch(SQLException e){
                    Toast.makeText(getApplicationContext(), R.string.error_sql_fail_add, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
