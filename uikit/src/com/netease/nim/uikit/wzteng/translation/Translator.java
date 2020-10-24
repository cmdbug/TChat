package com.netease.nim.uikit.wzteng.translation;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by WZTENG on 2017/04/13 0013.
 */

public class Translator {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String BASIC_URL = "http://fanyi.youdao.com/openapi.do";

    private static final String DEFAULT_API_KEY_NAME = "HTTP-TESTdddaa";
    private static final String DEFAULT_API_KEY_VALUE = "702271149";

    public static final int ERROR_CODE_NONE = 0;//正常
    public static final int ERROR_CODE_QUERY_TOO_LONG = 20;//要翻译的文本过长
    public static final int ERROR_CODE_FAIL = 30;//无法进行有效的翻译
    public static final int ERROR_CODE_UNSUPPORTED_LANG = 40;//不支持的语言类型
    public static final int ERROR_CODE_INVALID_KEY = 50;//无效的key
    public static final int ERROR_CODE_NO_RESULT = 60;//无词典结果，仅在获取词典结果生效
    public static final int ERROR_CODE_RESTRICTED = -10;//受限

    public static final int ERROR_CODE_NETWORD_ERROR = -20;//网络异常

    TranslationResult result = new TranslationResult();

    public Translator() {
    }

    /**
     * 查询翻译
     *
     * @param query    目标字符串
     * @param callback 回调
     */
    public void query(String query, TranslateCallback callback) {
        if (Utils.isEmptyOrBlankString(query)) {
            if (callback != null) {
                callback.onQuery(query, null);
            }
            return;
        }
        query = Utils.splitWord(query);//拆分单词
        queryRequest(query, callback);
    }

    static String getQueryUrl(String query) {
        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String apiKeyName = DEFAULT_API_KEY_NAME;
        String apiKeyValue = DEFAULT_API_KEY_VALUE;
        return BASIC_URL + "?type=data&doctype=json&version=1.1&keyfrom=" + apiKeyName + "&key=" +
                apiKeyValue + "&q=" + encodedQuery;
    }

    private void queryRequest(String mQuery, final TranslateCallback tCallback) {
        final String query = mQuery;
        final String url = getQueryUrl(query);
        try {
            //创建okHttpClient对象
            OkHttpClient mOkHttpClient = new OkHttpClient();
            //创建一个Request
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            //请求加入调度
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    result.setErrorCode(ERROR_CODE_NETWORD_ERROR);
                    if (tCallback != null) {
                        tCallback.onQuery(query, result);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //该回调是子线程，非主线程
                    result = JSON.parseObject(response.body().string(), TranslationResult.class);
                    if (tCallback != null) {
                        tCallback.onQuery(query, result);
                    }
                }
            });
        } catch (Exception e) {
            result.setErrorCode(ERROR_CODE_FAIL);
        }

    }

    /**
     * 翻译回调接口
     */
    public interface TranslateCallback {
        /**
         * 翻译结束后的回调方法
         *
         * @param query  查询字符串
         * @param result 翻译结果
         */
        void onQuery(@Nullable String query, @Nullable TranslationResult result);
    }
}
