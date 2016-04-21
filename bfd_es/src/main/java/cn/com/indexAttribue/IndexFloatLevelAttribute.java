package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by yu.fu on 2015/7/21.
 */
public class IndexFloatLevelAttribute implements IndexFloatAttribute {
    public void setIndexFloatAttribute(JSONObject obj, String key, float value) {
        obj.put(key, value);
    }
}
