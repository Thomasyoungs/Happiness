package com.haipay.credit.retrofit.listener;

/**
 * 下载文件进度更新 多任务异步时 需要自己根据url进行区分
 * Created by dayu-jiang on 2017/12/23.
 */

public interface ProgressLisenter {
    void progressUpdate(String url, long totalProgress, long progress, boolean doneflag);
}
