package frame.wdh.myframe.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import frame.wdh.myframe.annotation.ColumnName;
import frame.wdh.myframe.annotation.Primarykey;
import frame.wdh.myframe.annotation.TableName;

/**
 * Created by wangdonghai on 2017/4/9.
 */

public class LiteDB implements DBSupport {
    private SQLiteDatabase db;
    private static LiteDB  instance=null;
    private LiteDB(Context context){
        this.db = DBHelper.getInstance(context).getWritableDatabase();
    }
    synchronized  public static LiteDB getSingleInstance(Context context){
        return instance==null?new LiteDB(context.getApplicationContext()):instance;
    }
    @Override
    public long insert(Object o) {
        return 0;
    }
    public  long insert(List list){
        return  0;
    }
    @Override
    public int delete(Serializable id) {
        return 0;
    }

    @Override
    public int update() {
        return 0;
    }

    @Override
    public List query() {
        return null;
    }
    private void createTable(Class clazz){

    }
    private void createTable(List<Class> list){

    }
    private String getCreateTableSql(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        //获取表名
        String tabName =clazz.getAnnotation(TableName.class).value();
        //.append(" (id  INTEGER PRIMARY KEY AUTOINCREMENT, ")
        sb.append("create table ").append(tabName);
        //得到类中所有属性对象数组
        Field[] fields = clazz.getDeclaredFields();
        for (Field fd : fields) {
            String fieldName = fd.getName();
            Primarykey annotation_id = fd.getAnnotation(Primarykey.class);
            if (annotation_id!=null) {
                sb.append(fd.getAnnotation(ColumnName.class).value()).append("  INTEGER PRIMARY KEY  ");
                if(fd.getAnnotation(Primarykey.class).autoincrement())
                    sb.append("AUTOINCREMENT,");
                else
                 sb.append(",");
                continue;
            } else {
                sb.append(fieldName).append(Utils.getColumnType(fieldType)).append(", ");
            }
        }
        int len = sb.length();
        sb.replace(len - 2, len, ")");
        return sb.toString();
    }

}
