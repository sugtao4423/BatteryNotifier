package sugtao4423.batterynotifier.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{

    public Database(Context context){
        super(context, "database.sqlite3", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE accounts(id INTEGER PRIMARY KEY, kind TEXT NOT NULL, json TEXT NOT NULL, UNIQUE(kind, json))");
        db.execSQL("CREATE TABLE notifies(account_id INTEGER NOT NULL, enable TEXT NOT NULL, threshold INTEGER NOT NULL, text TEXT NOT NULL, UNIQUE(account_id, threshold))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
