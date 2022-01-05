package com.dimension.maskbook.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonRPCUtil {

    public static JSONObject createMessage(Integer messageId, @NonNull String methodName, @NonNull List<String> params) throws JSONException {
        return createMessage(messageId.toString(), methodName, params);
    }

    public static JSONObject createMessage(String messageId, @NonNull String methodName, @NonNull List<String> params) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("jsonrpc", "2.0");
        message.put("method", methodName);
        message.put("params", new JSONArray(params));
        message.put("id", messageId);
        return message;
    }


    public static JSONObject createMessage(Integer messageId, @NonNull String methodName, @NonNull JSONObject params) throws JSONException {
        return createMessage(messageId.toString(), methodName, params);
    }

    public static JSONObject createMessage(String messageId, @NonNull String methodName, @NonNull JSONObject params) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("jsonrpc", "2.0");
        message.put("method", methodName);
        message.put("params", params);
        message.put("id", messageId);
        return message;
    }
}
