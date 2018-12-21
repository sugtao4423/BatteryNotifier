package sugtao4423.batterynotifier;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity{

    private boolean isRunningBatteryWatchService = false;

    private Button serviceToggleButton;
    private NotifyListAdapter listAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceToggleButton = (Button)findViewById(R.id.serviceToggleButton);
        ListView listView = (ListView)findViewById(R.id.listview);
        listAdapter = new NotifyListAdapter(getApplicationContext(), false);
        listView.setAdapter(listAdapter);

        BroadcastReceiver pong = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                isRunningBatteryWatchService = true;
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(pong, new IntentFilter("pong"));
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(new Intent("ping"));

        setToggleButtonText();
    }

    public void startBatteryWatchService(){
        Intent intent = new Intent(this, BatteryWatchService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
        isRunningBatteryWatchService = true;
    }

    public void stopBatteryWatchService(){
        Intent intent = new Intent(this, BatteryWatchService.class);
        stopService(intent);
        isRunningBatteryWatchService = false;
    }

    public void setToggleButtonText(){
        if(isRunningBatteryWatchService){
            serviceToggleButton.setText(R.string.service_running);
            serviceToggleButton.setBackgroundResource(R.drawable.button_running);
        }else{
            serviceToggleButton.setText(R.string.service_not_running);
            serviceToggleButton.setBackgroundResource(R.drawable.button_not_running);
        }
    }

    public void clickServiceToggleButton(View view){
        if(!isRunningBatteryWatchService){
            startBatteryWatchService();
        }else{
            stopBatteryWatchService();
        }
        setToggleButtonText();
    }

    @Override
    public void onResume(){
        super.onResume();
        listAdapter.clear();
        try{
            listAdapter.addAll(((App)getApplicationContext()).getDbUtils().getEnabledNotifies());
        }catch(JSONException e){
            Toast.makeText(getApplicationContext(), R.string.error_parse_json, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuItem add = menu.add(0, Menu.FIRST, Menu.NONE, R.string.rule);
        add.setIcon(android.R.drawable.ic_menu_add);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == Menu.FIRST){
            startActivity(new Intent(this, SettingRuleActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ((App)getApplicationContext()).closeDB();
    }

}
