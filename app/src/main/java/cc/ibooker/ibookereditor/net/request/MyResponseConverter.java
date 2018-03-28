package cc.ibooker.ibookereditor.net.request;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import cc.ibooker.ibookereditor.utils.AESUtil;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class MyResponseConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final Type type;

    public MyResponseConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(@NonNull ResponseBody responseBody) throws IOException {
        // 第一步，获取JSON字符串->JSON对象
        String responseStr = responseBody.string();
//        JsonObject jsonObject = new JsonParser().parse(responseStr).getAsJsonObject();
        // 第二步，获取加密aesStr，加密字符串
//        String aesStr = null;
//        JsonElement jsonElement = jsonObject.get("result");
//        if (jsonElement != null)
//            aesStr = jsonElement.getAsString();
        // 第三步，解密
//        String jsonStr = AESUtil.decrypt(aesStr);
        // 第四步，解析JSON
//        T result = gson.fromJson(jsonStr, type);

        // 第一步，获取JSON字符串
//        String responseStr = gson.newJsonReader(responseBody.charStream()).nextString();
        // 第二步，解密
        String jsonStr = AESUtil.decrypt(responseStr);
        try {
            return gson.fromJson(jsonStr, type);
        } finally {
            responseBody.close();
        }
    }
}
