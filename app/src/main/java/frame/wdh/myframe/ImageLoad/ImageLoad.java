package frame.wdh.myframe.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by wangdonghai on 2017/6/8.
 */

public class ImageLoad {
    private static ImageLoad mInstance;
    private LruCache<String, Bitmap> mlruCache;
    //线程池
    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 1;
    //队列的调度方式
    private Type mType = Type.LIFO;

    public ImageLoad(int count, Type type) {
        init(count, type);
    }

    public enum Type {
        FIFO, LIFO;
    }

    // 任务队列
    private LinkedList<Runnable> mTaskQueue;
    //轮询
    private Thread mPoolThread;
    //通知线程池执行任务
    private Handler mPoolHandler;
    //向主线程发送消息的
    private Handler mUIHandler;
    private Semaphore mSemaphorePoolThreadhandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadpool;
    private boolean isDiskCacheEnable = true;
    private static final String TAG = "ImageLoad";

    private void init(int threadcount, Type type) {
        initBackthread();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mlruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        if (mUIHandler == null) {
            mUIHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    ImgBeanHolder imgBeanHolder = (ImgBeanHolder) msg.obj;
                    Bitmap bitmap = imgBeanHolder.bitmap;
                    ImageView imageView1 = imgBeanHolder.imageView;
                    String path = imgBeanHolder.path;
                    if (imageView1.getTag().toString().equals(path)) {
                        imageView1.setImageBitmap(bitmap);
                    }
                }
            };
        }
        mThreadPool = Executors.newFixedThreadPool(threadcount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadpool = new Semaphore(threadcount);

    }

    private void initBackthread() {
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadpool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mSemaphorePoolThreadhandler.release();
                        Looper.loop();
                    }

                };
            }
        };
        mPoolThread.start();

    }

    /**
     * 使用时调用的方法
     * 1.先给imageview设置Tag
     * 2.在缓存中查找
     * 3.生成下载任务
     * @param path
     * @param imageView
     * @param isFormNet
     */
    public void loadImage(String path, ImageView imageView, boolean isFormNet) {
        imageView.setTag(path);
        Bitmap bm = getBitmapForLRUCache(path);
        if (bm != null) {
            refreashBitmap(path, imageView, bm);
        } else {
            addTask(buildTask(path, imageView, isFormNet));
        }
    }

    /**
     * 图片下载策略
     * 1.从本地获取
     * 2.网上下载，若开启磁盘缓存，则下载到本地文件，再从本地获取，没有开启则直接加载到imageview
     * @param path
     * @param imageView
     * @param isFormNet
     * @return
     */
    private Runnable buildTask(final String path, final ImageView imageView, final boolean isFormNet) {
        return new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                if (isFormNet) {
                    File file = getDiskCacheDir(imageView.getContext(), path);
                    if (file.exists()) {
                        bm = loadImageFromLocal(file.getAbsolutePath(), imageView);
                    } else {
                        if (isDiskCacheEnable) {
                            boolean downstate = ImageDownloadUtil.downloadImgByUrl(path, file);
                            if (downstate) {
                                bm = loadImageFromLocal(file.getAbsolutePath(), imageView);
                            }
                        } else
                            bm = ImageDownloadUtil.downloadImgByUrl(path, imageView);
                    }
                } else {
                    bm = loadImageFromLocal(path, imageView);
                }
                addBitmapToLruCache(path, bm);
                refreashBitmap(path, imageView, bm);
                mSemaphoreThreadpool.release();
            }
        };
    }

    protected void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path) == null) {
            if (bm != null)
                mlruCache.put(path, bm);
        }
    }

    private Bitmap getBitmapFromLruCache(String key) {
        return mlruCache.get(key);
    }

    private Bitmap loadImageFromLocal(final String path,
                                      final ImageView imageView) {
        Bitmap bm;
        ImageSizeUtil.ImageSize imageSize = ImageSizeUtil.getImageSize(imageView);
        bm = decodeSampledBitmapFromPath(path, imageSize.width,
                imageSize.heigth);
        return bm;
    }

    protected Bitmap decodeSampledBitmapFromPath(String path, int width,
                                                 int height) {
        // 获得图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = ImageSizeUtil.caculateSize(options,
                width, height);

        // 使用获得到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        // if(mPoolThreadHandler==null)wait();
        try {
            if (mPoolHandler == null)
                mSemaphorePoolThreadhandler.acquire();
        } catch (InterruptedException e) {
        }
        mPoolHandler.sendEmptyMessage(0x110);
    }

    private void refreashBitmap(final String path, final ImageView imageView,
                                Bitmap bm) {
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    private Bitmap getBitmapForLRUCache(String key) {
        return mlruCache.get(key);
    }

    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else
            return mTaskQueue.removeLast();
    }

    public static ImageLoad getmInstance(int count, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoad.class) {
                if (mInstance == null)
                    mInstance = new ImageLoad(count, type);
            }
        }
        return mInstance;
    }

    public static ImageLoad getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoad.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoad(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
}
