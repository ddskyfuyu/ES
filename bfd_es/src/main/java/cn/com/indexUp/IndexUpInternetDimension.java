package cn.com.indexUp;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.IndexArrayObject;
import cn.com.indexAttribue.IndexIntAttribute;
import cn.com.indexAttribue.IndexStringAttribute;
import cn.com.utils.FileUtils;
import com.alibaba.fastjson.JSONObject;

import org.apache.hadoop.fs.FileSystem;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yu.fu on 2015/7/23.
 */
public class IndexUpInternetDimension implements IndexUpDimension {

    private IndexArrayObject indexArrayObject;
    private IndexIntAttribute indexIntAttribute;
    private IndexStringAttribute indexStringAttribute;
    private Map<String, Integer> internet_time_map;


    public IndexUpInternetDimension(IndexArrayObject indexArrayObject, IndexIntAttribute indexIntAttribute, IndexStringAttribute indexStringAttribute){
        this.indexArrayObject = indexArrayObject;
        this.indexIntAttribute = indexIntAttribute;
        this.indexStringAttribute = indexStringAttribute;

    }

    public IndexUpInternetDimension(IndexArrayObject indexArrayObject, IndexIntAttribute indexIntAttribute,
                                    IndexStringAttribute indexStringAttribute, String internet_time_path,FileSystem fs){
        this.indexArrayObject = indexArrayObject;
        this.indexIntAttribute = indexIntAttribute;
        this.indexStringAttribute = indexStringAttribute;

        internet_time_map = new HashMap<String, Integer>();
        FileUtils.FillValMap(internet_time_map, internet_time_path, ",",fs);

    }



    public void setIndexArrayObject(IndexArrayObject indexArrayObject){
        this.indexArrayObject = indexArrayObject;
    }

    public void setIndexIntAttribute(IndexIntAttribute indexIntAttribute) {this.indexIntAttribute = indexIntAttribute;}

    public void setIndexStringAttribute(IndexStringAttribute indexStringAttribute){
        this.indexStringAttribute = indexStringAttribute;
    }


    private void buildListBySInfo(List<String> list, List<UserProfile2.SInfo> sInfos){
        for(UserProfile2.SInfo sInfo : sInfos){
            list.add(sInfo.getValue());
        }
    }




    public void fillUpDimension(Object object, JSONObject json) {

    }

    public void fillUpDimension(Object object, JSONArray jsons) {
        if(!(object instanceof UserProfile2.UserProfile)){
            return;
        }
        UserProfile2.UserProfile up = (UserProfile2.UserProfile) object;
        for(int i = 0; i < up.getInterFtsCount(); ++i){
            UserProfile2.InternetFeatures internet_ft = up.getInterFts(i);
            JSONObject inter_json = new JSONObject();

            //上网特征-浏览器
            List<String> browsers = new ArrayList<String>();
            if(internet_ft.getBrowserCount() != 0){
                buildListBySInfo(browsers, internet_ft.getBrowserList());
                inter_json.put("browsers", browsers);
            }

            //上网特征-操作系统属性
            List<String> systems = new ArrayList<String>();
            if(internet_ft.getOperSystemsCount() != 0){
                buildListBySInfo(systems, internet_ft.getOperSystemsList());
                inter_json.put("oper_systems", systems);
            }

            //上网特征-终端类型属性
            List<String> terminal_types = new ArrayList<String>();
            if(internet_ft.getTerminalBrandsCount() != 0){
                buildListBySInfo(terminal_types, internet_ft.getTerminalTypesList());
                inter_json.put("terminal_types", terminal_types);
            }

            //上网特征-上网频次属性
            if(internet_ft.getFrequencyCount() != 0){
                indexIntAttribute.setIndexIntAttribute(inter_json, "frequency", Integer.valueOf(internet_ft.getFrequency(0).getValue()));
            }

            //上网特征-上网时段属性
            if(internet_ft.getInternetTimeCount() != 0){
                if(internet_time_map.containsKey(internet_ft.getInternetTime(0).getValue())){
                    indexIntAttribute.setIndexIntAttribute(inter_json, "internet_time", Integer.valueOf(internet_time_map.get(internet_ft.getInternetTime(0).getValue())));
                }

            }

            //上网特征-上网时长属性
            if(internet_ft.getOnlineTimeCount() != 0){
                indexIntAttribute.setIndexIntAttribute(inter_json, "online_time", Integer.valueOf(internet_ft.getOnlineTime(0).getValue()));
            }

            //上网特征-渠道类型属性
            if(internet_ft.hasChanel()){
                indexStringAttribute.setIndexStringAttribute(inter_json, "channel", internet_ft.getChanel().getValue());
            }

            //上网特征-进入方式属性
            if(internet_ft.getAccessWayCount() != 0){
                indexStringAttribute.setIndexStringAttribute(inter_json, "access_way", internet_ft.getAccessWay(0).getValue());
            }
            indexArrayObject.addArrayJSONObject(jsons, inter_json);
        }
    }

    public void setUpDimension(JSONObject up, JSONObject value, String key) {
        up.put(key, value);
    }

    public void setUpDimension(JSONObject up, JSONArray value, String key) {
        up.put(key,value);
    }
}
