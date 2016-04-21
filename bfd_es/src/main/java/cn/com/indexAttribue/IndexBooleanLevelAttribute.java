package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by BFD_278 on 2015/7/21.
 */
public class IndexBooleanLevelAttribute implements IndexBooleanAttribute{


    public void setIndexBooleanLevelAttribute(JSONObject obj, String key, Boolean value) {
        if(value){
            obj.put(key, 1);
        }
    }
}
