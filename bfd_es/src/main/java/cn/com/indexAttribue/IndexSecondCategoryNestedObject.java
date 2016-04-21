package cn.com.indexAttribue;


import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by BFD_278 on 2015/7/22.
 */
public class IndexSecondCategoryNestedObject implements IndexNestedObject {
    private IndexFloatAttribute indexFloatAttribute;
    private IndexStringAttribute indexStringAttribute;
    private IndexIntAttribute indexIntAttribute;
    private IndexTimeStampAttribute indexTimeStampAttribute;


    public IndexSecondCategoryNestedObject(IndexFloatAttribute indexFloatAttribute, IndexStringAttribute indexStringAttribute,
                                           IndexIntAttribute indexIntAttribute, IndexTimeStampAttribute indexTimeStampAttribute){

        this.indexFloatAttribute = indexFloatAttribute;
        this.indexStringAttribute = indexStringAttribute;
        this.indexIntAttribute = indexIntAttribute;
        this.indexTimeStampAttribute = indexTimeStampAttribute;
    }

    public void setFloatAttr(IndexFloatAttribute indexFloatAttribute){
        this.indexFloatAttribute = indexFloatAttribute;
    }

    public void setIndexStringAttribute(IndexStringAttribute indexStringAttribute){
        this.indexStringAttribute = indexStringAttribute;
    }

    public void setIndexIntAttribute(IndexIntAttribute indexIntAttribute){
        this.indexIntAttribute = indexIntAttribute;
    }

    public void setIndexTimeStampAttribute(IndexTimeStampAttribute indexTimeStampAttribute){
        this.indexTimeStampAttribute = indexTimeStampAttribute;
    }

    public void setNestedObject(JSONObject obj, String key, JSONObject nested_obj) {
        obj.put(key, nested_obj);
    }

    public void fillNestedObject(JSONObject obj, Map<String, Map<String, String>> value) {

        for(Map.Entry<String, Map<String, String>> entry : value.entrySet()){
            //填充整形字段
            if(entry.getKey().equals("Integer")){
                for(Map.Entry<String, String> subEntry : entry.getValue().entrySet()){
                    indexIntAttribute.setIndexIntAttribute(obj, subEntry.getKey(), Integer.valueOf(subEntry.getValue()));
                }
            }

            //填充字符串字段
            if(entry.getKey().equals("String")){
                for(Map.Entry<String, String> subEntry : entry.getValue().entrySet()){
                    if(subEntry.getKey().equals("third_category") ||subEntry.getKey().equals("fourth_category")
                            || subEntry.getKey().equals("fifth_category")){
                        continue;
                    }
                    indexStringAttribute.setIndexStringAttribute(obj,subEntry.getKey(), subEntry.getValue());
                }
            }

            //填充浮点型字段
            if(entry.getKey().equals("Float")){
                for(Map.Entry<String, String> subEntry : entry.getValue().entrySet()){
                    indexFloatAttribute.setIndexFloatAttribute(obj, subEntry.getKey(), Float.valueOf(subEntry.getValue()));
                }
            }

            //填充时间戳字段
            if(entry.getKey().equals("TimeStamp")){
                for(Map.Entry<String, String> subEntry : entry.getValue().entrySet()){
                    indexTimeStampAttribute.setIndexTimeStampAttribute(obj,subEntry.getKey(), Long.valueOf(subEntry.getValue()));
                }
            }
        }
    }
}
