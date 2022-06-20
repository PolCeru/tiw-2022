package it.polimi.tiw.project4.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import it.polimi.tiw.project4.schemas.ErrorResponse;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import java.io.IOException;
import java.util.Date;

public class JsonHelper {
    private static final Moshi moshi;
    private static final JsonAdapter<ErrorResponse> errorResponseAdapter;

    static {
        moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter().nullSafe())
                .build();
        errorResponseAdapter = moshi.adapter(ErrorResponse.class);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return moshi.adapter(clazz).fromJson(json);
    }

    public static <T> String toJson(T object, Class<T> clazz) {
        return moshi.adapter(clazz).toJson(object);
    }

    public static String errorToJson(String error) {
        return errorResponseAdapter.toJson(new ErrorResponse(error));
    }

    public static <T> JsonAdapter<T> getJsonAdapter(Class<T> clazz) {
        return moshi.adapter(clazz);
    }
}
