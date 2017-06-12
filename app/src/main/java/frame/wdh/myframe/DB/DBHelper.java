package frame.wdh.myframe.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangdonghai on 2017/4/6.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase db;
    private static final String NAME = "my.db";
    private static final SQLiteDatabase.CursorFactory FACTORY = null;
    private static final int VERSION = 1;

    private static DBHelper instance =null;
    private DBHelper(Context context) {
        super(context, NAME, FACTORY, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql = "create table " + TABLE_BOOK_NAME + "(" + COLUMN_ID
//                + " integer primary key autoincrement," + COLUMN_TITLE
//                + " varchar(50)," + COLUMN_SUMMARY + " varchar(200));";
//
//        System.out.println("sql=" + sql);
//
//        db.execSQL(sql);
    }
  synchronized public static DBHelper getInstance(Context context){
       return instance==null?new DBHelper(context):instance;
   }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
