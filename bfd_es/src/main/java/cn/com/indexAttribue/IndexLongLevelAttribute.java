package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by BFD_278 on 2015/7/21.
 */
public class IndexLongLevelAttribute implements IndexLongAttribute{
    public void setIndexLongAttribute(JSONObject obj, String key, long value) {
        obj.put(key, value);
    }
}
