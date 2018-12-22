package sugtao4423.batterynotifier;

import android.app.Application;

import sugtao4423.batterynotifier.db.DBUtils;

public class App extends Application{

    private DBUtils dbUtils;
    private int previousBatteryPercent;

    public DBUtils getDbUtils(){
        if(dbUtils == null){
            dbUtils = new DBUtils(getApplicationContext());
        }
        return dbUtils;
    }

    public void closeDB(){
        if(dbUtils != null){
            dbUtils.dbClose();
            dbUtils = null;
        }
    }

    public void setPreviousBatteryPercent(int previousBatteryPercent){
        this.previousBatteryPercent = previousBatteryPercent;
    }

    public int getPreviousBatteryPercent(){
        return previousBatteryPercent;
    }

}
