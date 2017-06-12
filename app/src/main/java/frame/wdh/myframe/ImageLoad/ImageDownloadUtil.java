package frame.wdh.myframe.ImageLoad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wangdonghai on 2017/6/8.
 */

public class ImageDownloadUtil {
    public static boolean   downloadImgByUrl(String Url,File file){
        FileOutputStream fos=null;
        InputStream is=null;
        try {
            URL url=new URL(Url);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            is=connection.getInputStream();
            fos=new FileOutputStream(file);
            byte[] buf=new byte[512];
            int len=0;
            while((len=is.read(buf))!=-1){
                fos.write(buf);
            }
            fos.flush();
            connection.disconnect();
            return  true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    public static Bitmap downloadImgByUrl(String Url, ImageView imageView){
        InputStream is=null;
        try {
            URL url =new URL(Url);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            is.mark(is.available());
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            Bitmap bitmap=BitmapFactory.decodeStream(is,null,options);
            ImageSizeUtil.ImageSize imageSize=ImageSizeUtil.getImageSize(imageView);
            options.inSampleSize=ImageSizeUtil.caculateSize(options,imageSize.width,imageSize.heigth);
            options.inJustDecodeBounds=false;
            is.reset();
            bitmap=BitmapFactory.decodeStream(is,null,options);
            connection.disconnect();
            return  bitmap;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return  null;
    }
}
