package frame.wdh.myframe.Model;

import frame.wdh.myframe.annotation.ColumnName;
import frame.wdh.myframe.annotation.Primarykey;
import frame.wdh.myframe.annotation.TableName;

/**
 * Created by wangdonghai on 2017/4/6.
 */
@TableName("t_book")
public class Book {
    @Primarykey(autoincrement = true)
    @ColumnName("id")
    private int id;
    @ColumnName("title")
    private String tittle;
    @ColumnName("summary")
    private String summary;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", tittle=" + tittle + ", summary=" + summary
                + "]";
    }

    public Book(int id, String tittle, String summary) {
        super();
        this.id = id;
        this.tittle = tittle;
        this.summary = summary;
    }

    public Book() {
        super();
    }
}
