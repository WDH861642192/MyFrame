package frame.wdh.myframe.ImageLoad;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by wangdonghai on 2017/6/7.
 * 根据需求计算图片尺寸
 */

public class ImageSizeUtil {
    public static int caculateSize(BitmapFactory.Options options,int reqWidth,int reqHeigth){
        int width=options.outWidth;
        int heigth=options.outHeight;
        int inSampleSize=1;
        if(width>reqWidth||heigth>reqHeigth){
            int widthRadio=Math.round(width*1.0f/reqWidth);
            int heigthRadio=Math.round((heigth*1.0f/reqHeigth));
            inSampleSize=Math.max(widthRadio,heigthRadio);
        }
        return inSampleSize;
    }
    public static ImageSize getImageSize(ImageView imageView){
        ImageSize imageSize=new ImageSize();
        //获取屏幕尺寸
        DisplayMetrics displayMetrics=imageView.getContext().getResources().getDisplayMetrics();
        LayoutParams lp=imageView.getLayoutParams();
        int width=imageView.getWidth();
        int heigth=imageView.getHeight();
        if(width<=0){
            width=lp.width;
        }
        if(width<=0){
            width=getImageViewFiedValue(imageView,"mMaxWidth");
        }
        if(width<=0){
            width=displayMetrics.widthPixels;
        }
        if(heigth<=0){
            heigth=lp.height;
        }
        if(heigth<=0){
            heigth=getImageViewFiedValue(imageSize,"mMaxHeigth");
        }
        if(heigth<=0){
            heigth=displayMetrics.heightPixels;
        }
        imageSize.width=width;
        imageSize.heigth=heigth;
        return imageSize;
    }
    public static class ImageSize{
        int width;
        int heigth;
    }
    private static int getImageViewFiedValue(Object o,String f){
        int value=0;
        try {
            Field field= ImageView.class.getDeclaredField(f);
            field.setAccessible(true);
            int fvalue=field.getInt(o);
            if(fvalue>0&&fvalue<Integer.MAX_VALUE)
                value=fvalue;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }
}
