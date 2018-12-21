package sugtao4423.batterynotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

public class SettingRuleActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_rule);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager)findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AddRuleFragment fragment = (AddRuleFragment)sectionsPagerAdapter.findFragmentByPosition(viewPager, viewPager.getCurrentItem());
                fragment.clickFloatButton(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        public SectionsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            return AddRuleFragment.newInstance(position);
        }

        public Fragment findFragmentByPosition(ViewPager viewPager, int position){
            return (Fragment)instantiateItem(viewPager, position);
        }

        @Override
        public int getCount(){
            return 2;
        }
    }

    public static class AddRuleFragment extends Fragment{

        private static final String ARG_TAB_NUMBER = "tab_number";

        public AddRuleFragment(){
        }

        public static AddRuleFragment newInstance(int tabNumber){
            AddRuleFragment fragment = new AddRuleFragment();
            Bundle args = new Bundle();
            if(tabNumber == 0){
                args.putSerializable(ARG_TAB_NUMBER, Enum.RuleType.NOTIFY);
            }else if(tabNumber == 1){
                args.putSerializable(ARG_TAB_NUMBER, Enum.RuleType.ACCOUNT);
            }
            fragment.setArguments(args);
            return fragment;
        }

        private Enum.RuleType type;
        private App app;

        private NotifyListAdapter notifyAdapter;
        private ArrayAdapter<String> accountAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            app = (App)container.getContext().getApplicationContext();
            type = (Enum.RuleType)getArguments().get(ARG_TAB_NUMBER);
            ListView lv = new ListView(container.getContext());
            ViewCompat.setNestedScrollingEnabled(lv, true);

            if(type == Enum.RuleType.NOTIFY){
                notifyAdapter = new NotifyListAdapter(container.getContext(), true);
                lv.setAdapter(notifyAdapter);
            }else if(type == Enum.RuleType.ACCOUNT){
                accountAdapter = new ArrayAdapter<>(container.getContext(), android.R.layout.simple_list_item_1);
                lv.setAdapter(accountAdapter);
            }

            return lv;
        }

        public void clickFloatButton(View view){
            if(type == Enum.RuleType.NOTIFY){
                startActivity(new Intent(view.getContext(), AddNotifyActivity.class));
            }else if(type == Enum.RuleType.ACCOUNT){
                startActivity(new Intent(view.getContext(), AddAccountActivity.class));
            }
        }

        @Override
        public void onResume(){
            super.onResume();
            try{
                if(type == Enum.RuleType.NOTIFY){
                    notifyAdapter.clear();
                    notifyAdapter.addAll(app.getDbUtils().getNotifies());
                }else if(type == Enum.RuleType.ACCOUNT){
                    accountAdapter.clear();
                    accountAdapter.addAll(app.getDbUtils().getAccountNames());
                }
            }catch(JSONException e){
                Toast.makeText(app.getApplicationContext(), R.string.error_parse_json, Toast.LENGTH_LONG).show();
            }
        }

    }

}
