package com.netease.nim.uikit.wzteng.translation.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by WZTENG on 2017/04/13 0013.
 */

public class QueryResult implements Serializable {
    public static final int ERROR_CODE_NONE = 0;//正常
    public static final int ERROR_CODE_QUERY_TOO_LONG = 20;//要翻译的文本过长
    public static final int ERROR_CODE_FAIL = 30;//无法进行有效的翻译
    public static final int ERROR_CODE_UNSUPPORTED_LANG = 40;//不支持的语言类型
    public static final int ERROR_CODE_INVALID_KEY = 50;//无效的key
    public static final int ERROR_CODE_NO_RESULT = 60;//无词典结果，仅在获取词典结果生效
    public static final int ERROR_CODE_RESTRICTED = -10;//受限

    private String query;//查询的文本
    private int errorCode;//返回的结果码
    private String[] translation;//有道翻译结果

    private String message;//自定义消息类型

    private BasicExplain basicExplain;//有道翻译
    private WebExplain[] webExplains;//网络释义

    public QueryResult() {
    }

    public QueryResult(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getTranslation() {
        return translation;
    }

    public void setTranslation(String[] translation) {
        this.translation = translation;
    }

    public BasicExplain getBasicExplain() {
        return basicExplain;
    }

    public void setBasicExplain(BasicExplain basicExplain) {
        this.basicExplain = basicExplain;
    }

    public WebExplain[] getWebExplains() {
        return webExplains;
    }

    public void setWebExplains(WebExplain[] webExplains) {
        this.webExplains = webExplains;
    }

    public void checkError() {
        if (errorCode == ERROR_CODE_NONE && (translation == null || translation.length == 0) &&
                (basicExplain == null || basicExplain.getExplains() == null || basicExplain.getExplains().length == 0))
            errorCode = ERROR_CODE_FAIL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + errorCode;
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryResult other = (QueryResult) obj;
        if (errorCode != other.errorCode)
            return false;
        if (query == null) {
            if (other.query != null)
                return false;
        } else if (!query.equals(other.query))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "query='" + query + '\'' +
                ", errorCode=" + errorCode +
                ", translation=" + Arrays.toString(translation) +
                ", message='" + message + '\'' +
                ", basicExplain=" + basicExplain +
                ", webExplains=" + Arrays.toString(webExplains) +
                '}';
    }
}
