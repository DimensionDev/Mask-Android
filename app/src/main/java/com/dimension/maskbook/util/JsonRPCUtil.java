/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
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
