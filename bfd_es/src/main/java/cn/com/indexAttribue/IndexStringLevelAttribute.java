package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by yu.fu on 2015/7/21.
 */
public class IndexStringLevelAttribute implements IndexStringAttribute {
    public void setIndexStringAttribute(JSONObject obj, String key, String value) {
        obj.put(key, value);
    }
}


