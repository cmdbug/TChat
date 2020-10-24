package com.netease.nim.uikit.wzteng.translation;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by WZTENG on 2017/04/13 0013.
 */

public class TranslationResult {


    /**
     * translation : ["error"]
     * basic : {"phonetic":"gʊd","uk-phonetic":"gʊd","us-phonetic":"ɡʊd","explains":["mistake","error","fault","slip","inaccuracy"]}
     * query : 错误
     * errorCode : 0
     * web : [{"value":["Error","mistake","error"],"key":"错误"},{"value":["wrong","by mistake","mistaken"],"key":"错误的"},{"value":["Error reporting service","Error Reporting","Bug report"],"key":"错误报告"}]
     */

    private BasicBean basic;
    private String query;
    private int errorCode;
    private List<String> translation;
    private List<WebBean> web;

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
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

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public List<WebBean> getWeb() {
        return web;
    }

    public void setWeb(List<WebBean> web) {
        this.web = web;
    }

    public static class BasicBean {
        /**
         * phonetic : gʊd
         * uk-phonetic : gʊd
         * us-phonetic : ɡʊd
         * explains : ["mistake","error","fault","slip","inaccuracy"]
         */

        private String phonetic;
        @JSONField(name = "uk-phonetic")
        private String ukphonetic;
        @JSONField(name = "us-phonetic")
        private String usphonetic;
        private List<String> explains;

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getUkphonetic() {
            return ukphonetic;
        }

        public void setUkphonetic(String ukphonetic) {
            this.ukphonetic = ukphonetic;
        }

        public String getUsphonetic() {
            return usphonetic;
        }

        public void setUsphonetic(String usphonetic) {
            this.usphonetic = usphonetic;
        }

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }
    }

    public static class WebBean {
        /**
         * value : ["Error","mistake","error"]
         * key : 错误
         */

        private String key;
        private List<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
