package com.backend;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonUtil {

    private JsonUtil(){
        throw new IllegalStateException("JSon Utility class");
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static ResponseTransformer json() {
        return new ResponseTransformer() {
            public String render(Object object) throws Exception {
                return toJson(object);
            }
        };
    }
}