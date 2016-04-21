package cn.com.indexUp;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.IndexArrayObject;
import cn.com.indexAttribue.IndexIntAttribute;
import cn.com.indexAttribue.IndexStringAttribute;
import com.alibaba.fastjson.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu.fu on 2015/7/23.
 * 填写All渠道的上网特征集合
 */
public class IndexUpOldInternetDimension implements IndexUpDimension {

    private IndexArrayObject indexArrayObject;
    private IndexIntAttribute indexIntAttribute;
    private IndexStringAttribute indexStringAttribute;


    public IndexUpOldInternetDimension(IndexArrayObject indexArrayObject, IndexIntAttribute indexIntAttribute, IndexStringAttribute indexStringAttribute){
        this.indexArrayObject = indexArrayObject;
        this.indexIntAttribute = indexIntAttribute;
        this.indexStringAttribute = indexStringAttribute;
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
        if(!(object instanceof UserProfile2.InternetFeatures)){
            return;
        }
        UserProfile2.InternetFeatures internet_ft = (UserProfile2.InternetFeatures) object;
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
        if(internet_ft.getTerminalTypesCount() != 0){
            buildListBySInfo(terminal_types, internet_ft.getTerminalTypesList());
            inter_json.put("terminal_types", terminal_types);
        }

        //上网特征-终端品牌属性
        List<String> terminal_brands= new ArrayList<String>();
        if(internet_ft.getTerminalBrandsCount() != 0){
            buildListBySInfo(terminal_brands, internet_ft.getTerminalBrandsList());
            inter_json.put("terminal_brands", terminal_types);
        }



        //上网特征-上网频次属性
        if(internet_ft.getFrequencyCount() != 0){
            indexIntAttribute.setIndexIntAttribute(inter_json,"frequency", Integer.valueOf(internet_ft.getFrequency(0).getValue()));
        }

        //上网特征-上网时段属性
        if(internet_ft.getInternetTimeCount() != 0){
            indexIntAttribute.setIndexIntAttribute(inter_json, "internet_time", Integer.valueOf(internet_ft.getInternetTime(0).getValue()));
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
        if(inter_json.size() > 0){
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
