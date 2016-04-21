package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;
import org.json.simple.JSONArray;

/**
 * Created by yu.fu on 2015/7/22.
 */
public class IndexCategoryArrayObject implements IndexArrayObject {

    public void addArrayJSONObject(JSONArray objArray, JSONObject json){
        objArray.add(json);
    }

    public void setJSONObject(JSONObject object, JSONArray value, String key){
        object.put(key, value);
    }

}
