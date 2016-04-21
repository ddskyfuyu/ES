package cn.com.indexAttribue;

import cn.com.utils.TimeUtils;
import com.alibaba.fastjson.JSONObject;


/**
 * Created by yu.fu on 2015/7/21.
 */
public class IndexTimeStampLevelAttribute implements IndexTimeStampAttribute {

    public void setIndexTimeStampAttribute(JSONObject obj, String key, long value) {
        obj.put(key, TimeUtils.timestamp2Date(value));
    }
}
