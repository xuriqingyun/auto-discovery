package cn.pioneeer.dcim.saas.autodiscovery.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:json转换工具
 * User: zhouxu
 * Date: 2018-11-27 11:51
 */
public class GsonUtils {
    private static Gson gson = null;
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtils() {
    }

    /**
     * @Author zhouxu
     * @Description //object转换成json
     * @Date 2018/11/27 11:54
     * @Param [object]
     * @return java.lang.String
     * @throws
     **/
    public static String toString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * @Author zhouxu
     * @Description //string转object
     * @Date 2018/11/27 11:54
     * @Param [str, cls]
     * @return T
     * @throws
     **/
    public static <T> T parese(String str, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(str, cls);
        }
        return t;
    }

    /**
     * @Author zhouxu
     * @Description //string转list对象集合
     * @Date 2018/11/27 11:55
     * @Param [str, cls]
     * @return java.util.List<T>
     * @throws
     **/
    public static <T> List<T> parseList(String str, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(str, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * @Author zhouxu
     * @Description //string转List<Map<String, T>>
     * @Date 2018/11/27 11:56
     * @Param [str]
     * @return java.util.List<java.util.Map<java.lang.String,T>>
     * @throws
     **/
    public static <T> List<Map<String, T>> parseListMaps(String str) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(str,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * @Author zhouxu
     * @Description //string 转map
     * @Date 2018/11/27 11:56
     * @Param [str]
     * @return java.util.Map<java.lang.String,T>
     * @throws
     **/
    public static <T> Map<String, T> parseMap(String str) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(str, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }
}
