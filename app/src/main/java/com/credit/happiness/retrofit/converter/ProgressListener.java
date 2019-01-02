package com.credit.happiness.retrofit.converter;

/**
 * Created by dayu-jiang on 2017/12/22.
 */

public interface ProgressListener {
    /**
     * @param url      上传或下载任务url
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     */
    void onProgress(String url, long progress, long total, boolean done);
}
