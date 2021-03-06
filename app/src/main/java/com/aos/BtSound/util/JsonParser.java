package com.aos.BtSound.util;

import android.util.Log;

import com.aos.BtSound.VoiceCellApplication;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * created by collin on 2015-08-05.
 * modify by collin on 2015-10-15.
 */

public class JsonParser {
    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    // 混合模式下语法识别返回语法理解CollinWang
    public static String parseMixNameResult(String json) {
        String contact = "";
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            contact = joResult.getJSONObject("semantic").getJSONObject("slots").getString("name");
        } catch (Exception e) {
            Log.i("CollinWang", "Catch=", e);
        }

        return contact;

    }

    // 混合模式下语法识别返回语法理解拿到textCollinWang
    public static String parseMixNameResultText(String json) {
        String text = "";
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            text = joResult.getString("text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static boolean isHaveCallOrText(String json) {
        String operation = "";
        String text = "";
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            operation = joResult.optString("operation");
            text = joResult.optString("text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (operation.equals("CALL") || text.contains("拍照") || text.contains("录音")) {
            return true;
        } else {
            return false;
        }
    }

    public static String parseGrammarResult(String json, String engType) {
        StringBuffer firstName = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            VoiceCellApplication.mSc = joResult.getInt("sc");
            if ("cloud".equals(engType)) {
                for (int i = 0; i < words.length(); i++) {
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject obj = items.getJSONObject(j);
                        if (obj.getString("w").contains("nomatch")) {
                            firstName.append("没有匹配结果.");
                            return firstName.toString();
                        }
                    }
                }
            } else if ("local".equals(engType)) {
                for (int i = 0; i < words.length(); i++) {
                    JSONObject wsItem = words.getJSONObject(i);
                    JSONArray items = wsItem.getJSONArray("cw");
                    if ("<contact>".equals(wsItem.getString("slot"))) {
                        firstName.append("【");
                        for (int j = 0; j < items.length(); j++) {
                            JSONObject obj = items.getJSONObject(j);
                            if (obj.getString("w").contains("nomatch")) {
                                firstName.append("没有匹配结果");
                                return firstName.toString();
                            }
                            firstName.append(obj.getString("w"));
                            break;
                        }
                        firstName.append('】');
                    } else {
                        JSONObject obj = items.getJSONObject(0);
                        if (obj.getString("w").contains("nomatch")) {
                            firstName.append("没有匹配结果");
                            return firstName.toString();
                        }
                        firstName.append(obj.getString("w"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            firstName.append("没有匹配结果.");
        }
        return firstName.toString();
    }

    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject obj = items.getJSONObject(j);
                    if (obj.getString("w").contains("nomatch")) {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append("【结果】" + obj.getString("w"));
                    ret.append("【置信度】" + obj.getInt("sc"));
                    ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }
}
