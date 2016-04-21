package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by yu.fu on 2015/7/21.
 */
public class IndexDoubleLevelAttribute implements IndexDoubleAttribute{

    public void setIndexDoubleAttribute(JSONObject obj, String key, Double value) {
        obj.put(key, value);
    }

}
