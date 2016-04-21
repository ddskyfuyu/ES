package cn.com.indexUp;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.IndexBooleanAttribute;
import cn.com.indexAttribue.IndexIntAttribute;
import cn.com.indexAttribue.IndexStringAttribute;
import cn.com.utils.FileUtils;
import com.alibaba.fastjson.JSONObject;

import org.apache.hadoop.fs.FileSystem;
import org.json.simple.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yu.fu on 2015/7/23.
 * Fill Demographics attributes of userprofile
 */

public class IndexUpDgDimension implements IndexUpDimension {

    private IndexBooleanAttribute indexBooleanAttribute;
    private IndexStringAttribute indexStringAttribute;
    private IndexIntAttribute indexIntAttribute;

    private Map<String, Integer> ageMap;
    private Map<String, Integer> sexMap;

    public void setIndexBooleanAttribute(IndexBooleanAttribute indexBooleanAttribute){
        this.indexBooleanAttribute = indexBooleanAttribute;
    }

    public void setIndexStringAttribute(IndexStringAttribute indexStringAttribute){
        this.indexStringAttribute = indexStringAttribute;
    }

    public void setIndexIntAttribute(IndexIntAttribute indexIntAttribute){
        this.indexIntAttribute = indexIntAttribute;
    }

    public IndexUpDgDimension(IndexBooleanAttribute indexBooleanAttribute, IndexStringAttribute indexStringAttribute,
                              IndexIntAttribute indexIntAttribute, String age_fin, String sex_fin,FileSystem fs){

        //选择设置相关的属性的填充方法
        setIndexBooleanAttribute(indexBooleanAttribute);
        setIndexStringAttribute(indexStringAttribute);
        setIndexIntAttribute(indexIntAttribute);

        System.out.println("Age Path: " + age_fin);
        System.out.println("Sex Path: " + sex_fin);
        ageMap = new HashMap<String, Integer>();
        sexMap = new HashMap<String, Integer>();
        FileUtils.FillValMap(ageMap, age_fin, ",",fs);
        FileUtils.FillValMap(sexMap, sex_fin, ",",fs);
    }


    /*
     * Fill Demographics attributes of userprofile into json
     *
     * @param object an instance of demographics of userprofile
     * @param json the data of demographics of
     */
    public void fillUpDimension(Object object, JSONObject json) {

        if(!(object instanceof UserProfile2.DemographicInfo)){
            return;
        }
        UserProfile2.DemographicInfo dg_info = (UserProfile2.DemographicInfo)object;

        //填充人口属性值-是否有孩子：dg_info.children,取值为1
        if(dg_info.hasHasBaby()){
            indexBooleanAttribute.setIndexBooleanLevelAttribute(json, "dg_info.children", dg_info.getHasBaby());
        }
        //填充人口属性值-是否结婚: dg_info.marriage,取值为1
        if(dg_info.hasMarried()){
            indexBooleanAttribute.setIndexBooleanLevelAttribute(json, "dg_info.marriage", dg_info.getMarried());
        }
        //填充人口属性值-城市和省份: dg_info.city dg_info.province
        if(dg_info.getCityCount() != 0){
            String location = dg_info.getCity(0).getValue();
            String[] array = location.split("\\$");
            if(array.length == 2) {
                indexStringAttribute.setIndexStringAttribute(json, "dg_info.province", array[0]);
                indexStringAttribute.setIndexStringAttribute(json, "dg_info.city", array[1]);
            }
        }
        //填充人口属性值-互联网年龄: dg_info.internet_age
        if(dg_info.getAgesCount() != 0){
            if(ageMap.containsKey(dg_info.getAges(0).getValue())){
                indexIntAttribute.setIndexIntAttribute(json, "dg_info.internet_age", Integer.valueOf(ageMap.get(dg_info.getAges(0).getValue())));
            }
        }
        //填充人口属性值-互联网年龄: dg_info.internet_sex
        if(dg_info.getSexCount() != 0){
            if(sexMap.containsKey(dg_info.getSex(0).getValue())){
                indexIntAttribute.setIndexIntAttribute(json, "dg_info.internet_sex", Integer.valueOf(sexMap.get(dg_info.getSex(0).getValue())));
            }
        }
        //填充人口属性值-生物性别年龄: dg_info.natural_age
        if(dg_info.hasBioAge()){
            if(ageMap.containsKey(dg_info.getBioAge().getValue())){
                indexIntAttribute.setIndexIntAttribute(json, "dg_info.natural_age", Integer.valueOf(ageMap.get(dg_info.getBioAge().getValue())));
            }
        }

        //填充人口属性值-生物性别: dg_info.natural_sex
        if(dg_info.hasBioGender()){
            if(sexMap.containsKey(dg_info.getBioGender().getValue())){
                indexIntAttribute.setIndexIntAttribute(json, "dg_info.natural_sex", Integer.valueOf(sexMap.get(dg_info.getBioGender().getValue())));
            }
        }


    }

    public void fillUpDimension(Object object, JSONArray jsons) {

    }


    public void setUpDimension(JSONObject up, JSONObject value, String key) {

    }

    public void setUpDimension(JSONObject up, JSONArray value, String key) {

    }
}


