package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by yu.fu on 2015/7/21.
 */
public interface IndexNestedObject {
    void fillNestedObject(JSONObject obj, Map<String, Map<String,String>> value);

    void setNestedObject(JSONObject obj, String key, JSONObject nested_obj);
}
