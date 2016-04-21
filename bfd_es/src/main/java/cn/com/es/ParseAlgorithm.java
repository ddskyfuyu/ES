package cn.com.es;


import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yu.fu on 2015/7/21.
 */
public interface ParseAlgorithm  {
    void parse2JSon(Object obj, JSONObject output);
    void parse2JSon(Map<String, String> gidLabelMap,Object obj, JSONObject json) ;
}
