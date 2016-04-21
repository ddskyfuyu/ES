package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by yu.fu on 2015/7/21.
 */
public class IndexIntLevelAttribute implements IndexIntAttribute{

    public void setIndexIntAttribute(JSONObject obj, String key, int value) {
        obj.put(key, value);
    }


}
