package frame.wdh.myframe.DB;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangdonghai on 2017/4/6.
 */

public interface IDaoSupport<T> {

    long insert(T t);


    int delete(Serializable id);


    int update(T t);


    List<T> findAll();
}
