package com.credit.happiness.retrofit.converter;

import com.credit.happiness.retrofit.util.NetUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by dayu-jiang on 2017/12/13.
 */

public class StringConverter implements Converter<ResponseBody, String> {

    public static final StringConverter INSTANCE = new StringConverter();
    private final Gson gson = new Gson();

    @Override
    public String convert(ResponseBody value) throws IOException {
        String back = "";
        try {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            back = value.string();
        } finally {
            value.close();
        }
        return back;
    }
}