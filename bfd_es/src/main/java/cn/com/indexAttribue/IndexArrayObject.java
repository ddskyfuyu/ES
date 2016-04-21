package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;
import org.json.simple.JSONArray;

/**
 * Created by yu.fu on 2015/7/22.
 */
public interface IndexArrayObject {

    void addArrayJSONObject(JSONArray objArray, JSONObject json);

    void setJSONObject(JSONObject object, JSONArray value, String key);
}
