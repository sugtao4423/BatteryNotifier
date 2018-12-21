package sugtao4423.batterynotifier;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import sugtao4423.batterynotifier.data.Notify;
import sugtao4423.batterynotifier.data.SlackAccount;
import sugtao4423.batterynotifier.data.TwitterAccount;

public class NotifyListAdapter extends ArrayAdapter<Notify>{

    private Context context;
    private App app;
    private boolean showEnableSwitch;
    private LayoutInflater inflater;

    public NotifyListAdapter(Context context, boolean showEnableSwitch){
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        this.app = (App)context.getApplicationContext();
        this.showEnableSwitch = showEnableSwitch;
        this.inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder{
        TextView postAccount;
        TextView threshold;
        TextView postText;
        Switch enable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_notify, null);

            holder = new ViewHolder();
            holder.postAccount = (TextView)convertView.findViewById(R.id.post_account);
            holder.threshold = (TextView)convertView.findViewById(R.id.threshold);
            holder.postText = (TextView)convertView.findViewById(R.id.post_text);
            holder.enable = (Switch)convertView.findViewById(R.id.enable);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Notify item = getItem(position);

        String accountName = null;
        if(item.getAccountType() == Enum.AccountType.SLACK){
            SlackAccount slack = (SlackAccount)item.getAccount();
            accountName = Enum.AccountType.SLACK.toString() + ": " + slack.getUsername() + " #" + slack.getChannel();
        }else if(item.getAccountType() == Enum.AccountType.TWITTER){
            accountName = Enum.AccountType.TWITTER.toString() + ": " + ((TwitterAccount)item.getAccount()).getScreenName();
        }
        holder.postAccount.setText(accountName);

        holder.threshold.setText(item.getThreshold() + " " + context.getResources().getString(R.string.percent));
        holder.postText.setText(item.getText());

        if(showEnableSwitch){
            holder.enable.setVisibility(View.VISIBLE);
            holder.enable.setChecked(item.getIsEnable());
            holder.enable.setOnCheckedChangeListener(switchEnableButton(item));
        }else{
            holder.enable.setVisibility(View.GONE);
        }

        return convertView;
    }

    public CompoundButton.OnCheckedChangeListener switchEnableButton(final Notify item){
        return new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                app.getDbUtils().updateNotifyEnable(item, isChecked);
            }
        };
    }

}
