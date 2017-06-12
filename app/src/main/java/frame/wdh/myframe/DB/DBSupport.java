package frame.wdh.myframe.DB;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangdonghai on 2017/4/9.
 */

public interface DBSupport {
    long insert(Object o);


    int delete(Serializable id);


    int update();


    List query();
}
