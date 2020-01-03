package cn.gddiyi.cash.utils.netutils;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadUtil {
    String TAG=getClass().getSimpleName();
    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;
    boolean downLoadAgain=true;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     * 可能导致图片不完全下载
     */
    public synchronized void download(final String url, final String saveDir, final OnDownloadListener listener) {
        {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
                okHttpClient.newCall(request).enqueue(getResponseCallback(url, saveDir, listener));
        }
    }

    @NonNull
    public Callback getResponseCallback(final String url, final String saveDir, final OnDownloadListener listener)  {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                Log.d(TAG, "onFailure: ");
                listener.onDownloadFailed();
                e.printStackTrace();
                downLoadAgain=true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int progress=0;
                InputStream is = null;
//                byte[] buf = new byte[2048 * 2];
                // TODO: 2019/9/16 ,减少下载出错可能的出错
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        progress = (int) (sum * 1.0f / total * 100);
                        // 下载中，这里需要优化，后续后好一秒刷新一次
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                    downLoadAgain = false;
                    Log.d(TAG, "onResponse:downLoadAgain "+downLoadAgain);
                    Log.d(TAG, "onResponse:progress= "+progress);
                    if (progress!=100){
                        Log.d(TAG, "onResponse:progress=需要重新下载 "+progress);
                        downLoadAgain=true;
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onResponse:onDownloadFailed "+e.toString());
                    listener.onDownloadFailed();
                    downLoadAgain = true;

                } finally {

                    try {
                        if (is != null)
                        { is.close();}
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                        { fos.close();}
                    } catch (IOException e) {
                    }
                }
            }
        };
    }


    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    public  String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
    public synchronized void  downloadByPost(final String url,String postArgs, final String saveDir,
                                             final OnDownloadListener listener) {
        Request request = new Request.Builder()
                .post(createRequestBody(postArgs))
                .url(url)
                .build()
                ;
        downLoadProgress(url, saveDir, listener, request);
    }

    private void downLoadProgress(final String url, final String saveDir,
                                  final OnDownloadListener listener, Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
                Log.i(TAG, "onFailure: "+e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048*2];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    listener.onDownloadFailed();
                    Log.i(TAG, "onResponse:onDownloadFailed() "+e.toString());
                    Log.d(TAG, "onResponse: "+e.toString());
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                        {   is.close();}
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                        {   fos.close();}
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public RequestBody createRequestBody(String content){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody=RequestBody.create(JSON, content);
        return  requestBody;
    }
}
