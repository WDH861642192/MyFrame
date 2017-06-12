package frame.wdh.myframe.DB;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import frame.wdh.myframe.annotation.ColumnName;
import frame.wdh.myframe.annotation.Primarykey;

/**
 * Created by wangdonghai on 2017/4/6.
 */

public class DaoSupportImpl<T> implements IDaoSupport<T> {
//分页
    private SQLiteDatabase db;
    public DaoSupportImpl(SQLiteDatabase db) {
       this.db=db;
    }

    @Override
    public long insert(T t) {
        ContentValues contentValues=new ContentValues();
        getColumn(t,contentValues);

        return 1;
    }
    public String getTableName(){
//        TableName tableName=
        return  null;
    }
    //获取实体类
    public T getInstance(){

        // 实体是何时确定的

        // ①哪个孩子调用的该方法
        Class clazz = getClass();// 获取到了正在运行时的那个类，这里就会拿到实际在跑的那个impl类
        // System.out.println(clazz.toString());

        // ②获取该孩子的父类(是支持泛型的父类)
        // clazz.getSuperclass();// 这个方法不行，拿不到泛型
        Type genericSuperclass = clazz.getGenericSuperclass();// 可以拿到泛型

        // jdk会让泛型实现一个接口(参数化的类型--这个接口)，所有的泛型都会实现这个接口(ParameterizedType)，规定了泛型的通用操作

        if (genericSuperclass != null
                && genericSuperclass instanceof ParameterizedType) {
            Type[] arguments = ((ParameterizedType) genericSuperclass)
                    .getActualTypeArguments();

            try {
                return ((Class<T>) arguments[0]).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        // ③获取到泛型中的参数

        return null;
    }
    @Override
    public int delete(Serializable id) {
//        return db.delete(DBHelper.TABLE_BOOK_NAME, DBHelper.COLUMN_ID + "=?",
//                new String[] { String.valueOf(id) });
        return 0;
    }

    @Override
    public int update(T t) {
        return 0;
    }

    @Override
    public List findAll() {
        return null;
    }
    private String[] getPrimarykeyNameAndValue(T t){
        Field[] declaredFields = t.getClass().getDeclaredFields();
        for (Field item : declaredFields) {
            item.setAccessible(true);// 添加权限
            Primarykey annotation_id = item.getAnnotation(Primarykey.class);
            if (annotation_id != null) {
                try {
                    // return item.get(m).toString();

                    ColumnName columnName = item
                            .getAnnotation(ColumnName.class);
                    if (columnName != null) {
                         String[] result=new String[2];
                         result[0]=columnName.value();
                        result[1]=item.get(t).toString();
                        return result;
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }
    private void getColumn(T t, ContentValues values) {
        Field[] fields = t.getClass().getDeclaredFields();

        for (Field item : fields) {
            ColumnName columnName = item.getAnnotation(ColumnName.class);
            if (columnName != null) {
                String key = columnName.value();
                String value;
                try {
                    item.setAccessible(true);
                    Primarykey primaryKey = item
                            .getAnnotation(Primarykey.class);
                    if (primaryKey != null && primaryKey.autoincrement()) {
                        continue;
                    }
                    value = item.get(t).toString();
                    values.put(key, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void createTable(){

    }
    private void cteateTable(List<Object> l){

    }
}
