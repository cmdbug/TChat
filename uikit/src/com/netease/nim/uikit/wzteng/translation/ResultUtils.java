package com.netease.nim.uikit.wzteng.translation;

import java.util.List;

/**
 * Created by WZTENG on 2017/04/14 0014.
 */

public class ResultUtils {
    static String sTotal = "";

    public static String resultDeal(TranslationResult result) {
        if (result.getErrorCode() == Translator.ERROR_CODE_NONE) {
            //正常
            sTotal = "<font color=\"#DF6A56\">" + result.getQuery() + "</font>";
            if (result.getBasic() != null) {
                //读音
                if (result.getBasic().getPhonetic() != null) {
                    //中文拼音，英文读音
                    sTotal = sTotal + "&nbsp;&nbsp;&nbsp;<font color=\"#CC7832\">["
                            + result.getBasic().getPhonetic() + "]</font><br/>";
                } else {
                    sTotal = sTotal + "<br/>";
                }
            } else {
                sTotal = sTotal + "<br/>";
            }

            if (result.getTranslation() != null) {
                //直接翻译
                List<String> t = result.getTranslation();
                String r = "";
                for (int i = 0; i < t.size(); i++) {
                    r = r + "<font color=\"#FFARRA\">" + t.get(i) + "</font>";
                    if (i < t.size() - 1) {
                        r = r + "<font color=\"#45C01A\">,</font>";
                    }
                }
                sTotal = sTotal + r + "<br/><br/>";
            }

            if (result.getBasic() != null) {
                //有道释义
                if (result.getBasic().getExplains() != null) {
                    List<String> explainList = result.getBasic().getExplains();
                    String r = "";
                    for (int i = 0; i < explainList.size(); i++) {
                        String e = explainList.get(i);
                        String[] s = Utils.splitExplain(e);
                        if (s[0] == null) {//没有词性时为null
                            r = r + "<font color=\"#3A6BAE\">" + s[1] + "</font><br/>";//FFC66D
                        } else {
                            r = r + "<font color=\"#9876AA\">" + s[0] + "</font> "//E9B0C4
                                    + "<font color=\"#3A6BAE\">" + s[1] + "</font><br/>";
                        }
                    }
                    sTotal = sTotal + r + "<br/>";
                }
            }

            if (result.getWeb() != null) {
                //网络释义
                List<TranslationResult.WebBean> webBeanList = result.getWeb();
                String r = "<font color=\"#808080\">网络释义</font><br/>";
                for (int n = 0; n < webBeanList.size(); n++) {
                    TranslationResult.WebBean webBean = webBeanList.get(n);
                    r = r + "<font color=\"#64B666\">" + webBean.getKey() + "</font>"
                            + "<font color=\"#808080\"> - </font>";
                    List<String> valueList = webBean.getValue();
                    String s = "";
                    for (int x = 0; x < valueList.size(); x++) {
                        s = s + "<font color=\"#6A8759\">" + valueList.get(x) + "</font>";
                        if (x < valueList.size() - 1) {
                            //最后一个不加逗号
                            s = s + "<font color=\"#6A8759\">,</font>";
                        }
                    }
                    r = r + s + "<br/>";
                }
                sTotal = sTotal + r;
            }

        } else if (result.getErrorCode() == Translator.ERROR_CODE_QUERY_TOO_LONG) {
            sTotal = "<font color=\"#FF0000\">翻译的文本过长!</font><br/>";
        } else if (result.getErrorCode() == Translator.ERROR_CODE_FAIL) {
            sTotal = "<font color=\"#FF0000\">无法进行有效的翻译!</font><br/>";
        } else if (result.getErrorCode() == Translator.ERROR_CODE_UNSUPPORTED_LANG) {
            sTotal = "<font color=\"#FF0000\">不支持的语言类型!</font><br/>";
        } else if (result.getErrorCode() == Translator.ERROR_CODE_INVALID_KEY) {
            sTotal = "<font color=\"#FF0000\">无效的KEY!</font><br/>";
        } else if (result.getErrorCode() == Translator.ERROR_CODE_NO_RESULT) {
            sTotal = "<font color=\"#FF0000\">无词典结果，仅在获取词典结果生效!</font><br/>";
        } else if (result.getErrorCode() == Translator.ERROR_CODE_RESTRICTED) {
            sTotal = "<font color=\"#FF0000\">使用频率过大，请尝试更换KEY!</font><br/>";
        } else if (result.getErrorCode() == Translator.ERROR_CODE_NETWORD_ERROR) {
            sTotal = "<font color=\"#FF0000\">网络异常!</font><br/>";
        }
        return sTotal;
    }
}
