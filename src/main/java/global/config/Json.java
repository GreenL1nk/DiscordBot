package global.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

public class Json {

    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, UuidAdapter.INSTANCE)
            .registerTypeAdapter(Date.class, DateAdapter.INSTANCE)
            .create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> JsonElement parse(String json) {
        return JsonParser.parseString(json);
    }
    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(parse(json), clazz);
    }
    public static <T> T fromJson(JsonElement json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
    public static <T> T fromJson(String json, Type clazz) {
        return fromJson(parse(json), clazz);
    }
    public static <T> T fromJson(JsonElement json, Type clazz) {
        return GSON.fromJson(json, clazz);
    }

}