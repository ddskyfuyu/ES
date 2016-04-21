package cn.com.es;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.*;
import cn.com.indexUp.IndexUpDimension;
import cn.com.utils.FileUtils;
import com.alibaba.fastjson.JSONObject;

import org.apache.hadoop.fs.FileSystem;
import org.json.simple.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yu.fu on 2015/7/21.
 */
public class ParseProtobuf2JSON implements ParseAlgorithm {

    private IndexStringAttribute indexStringAttribute;
    private IndexLongAttribute indexLongAttribute;
    private IndexIntAttribute indexIntAttribute;
    private IndexArrayObject indexArrayObject;

    private IndexUpDimension upDgDimension;
    private IndexUpDimension upCategoryDimension;
    private IndexUpDimension upInternetDimension;
    private IndexUpDimension upMarketDimension;
    private IndexUpDimension upOldInternetDimension;




    private Map<String, Integer> cidMap;



    public ParseProtobuf2JSON(String cid_fin, IndexUpDimension upDgDimension, IndexUpDimension upCategoryDimension,
                              IndexUpDimension upInternetDimension, IndexUpDimension upMarketDimension,FileSystem fs){

        cidMap = new HashMap<String, Integer>();
        FileUtils.FillMap(cidMap, cid_fin, ",",fs);
        this.upDgDimension = upDgDimension;
        this.upCategoryDimension = upCategoryDimension;
        this.upInternetDimension = upInternetDimension;
        this.upMarketDimension = upMarketDimension;

    }

    public ParseProtobuf2JSON(String cid_fin, IndexUpDimension upDgDimension, IndexUpDimension upCategoryDimension,
                              IndexUpDimension upInternetDimension, IndexUpDimension upMarketDimension,
                              IndexUpDimension upOldInternetDimension,FileSystem fs){

        cidMap = new HashMap<String, Integer>();
        FileUtils.FillMap(cidMap, cid_fin, ",",fs);
        this.upDgDimension = upDgDimension;
        this.upCategoryDimension = upCategoryDimension;
        this.upInternetDimension = upInternetDimension;
        this.upMarketDimension = upMarketDimension;
        this.upOldInternetDimension = upOldInternetDimension;
    }


    public void setIndexStringAttribute(IndexStringAttribute indexStringAttribute){
        this.indexStringAttribute = indexStringAttribute;
    }

    public void setIndexLongAttribute(IndexLongAttribute indexLongAttribute){
        this.indexLongAttribute = indexLongAttribute;
    }

    public void setIndexIntAttribute(IndexIntAttribute indexIntAttribute){
        this.indexIntAttribute = indexIntAttribute;
    }

    public void setIndexArrayObject(IndexArrayObject indexArrayObject){
        this.indexArrayObject = indexArrayObject;
    }

    public void parse2JSon(Object obj, JSONObject json){
    	 if(obj == null || json == null){
             System.out.println("The input is wrong. ");
             return;
         }
         UserProfile2.UserProfile up = (UserProfile2.UserProfile) obj;
         String gid = up.getUid();
         indexStringAttribute.setIndexStringAttribute(json, "gid", gid);
         
         indexLongAttribute.setIndexLongAttribute(json,"update_time", Long.valueOf(up.getUpdateTime()));

         if(up.hasDgInfo()){
             upDgDimension.fillUpDimension(up.getDgInfo(),json);
         }

         //用户画像-上网特征维度
         JSONArray internet_jsons = new JSONArray();
         if(up.getInterFtsCount() != 0){
             //上网特征按照渠道划分-PC，Mobile以及All
             upInternetDimension.fillUpDimension(up, internet_jsons);
         }
//         else if(up.hasInterFt()){
//             //上网特征原始的上网特征，不按照渠道划分
//             upOldInternetDimension.fillUpDimension(up.getInterFt(), internet_jsons);
//         }
         if(internet_jsons.size() != 0){
             indexArrayObject.setJSONObject(json, internet_jsons, "inter_ft");
         }

         //用户画像品类偏好维度
         JSONArray category_jsons = new JSONArray();
         for(int i = 0; i < up.getCidInfoCount(); ++i){
             UserProfile2.CidInfo cidInfo = up.getCidInfo(i);
             String cid = cidInfo.getCid();
             //填充指定CID(客户)的品类偏好信息
             if(!cidMap.containsKey(cid)){
                 continue;
             }
             upCategoryDimension.fillUpDimension(cidInfo,category_jsons);
         }
         if(category_jsons.size() != 0){
             indexArrayObject.setJSONObject(json, category_jsons, "category");
         }

         //用户画像营销特征维度,level1
         if(up.hasMarketFt()){
             upMarketDimension.fillUpDimension(up.getMarketFt(),json);
         }
    }

    public void parse2JSon(Map<String, String> gidLabelMap,Object obj, JSONObject json) {
        if(obj == null || json == null){
            System.out.println("The input is wrong. ");
            return;
        }
        UserProfile2.UserProfile up = (UserProfile2.UserProfile) obj;
        String gid = up.getUid();
        indexStringAttribute.setIndexStringAttribute(json, "gid", gid);
        String label = gidLabelMap.get(gid);
        
        if (label.equals("1")) {
        	indexIntAttribute.setIndexIntAttribute(json, "opinion_leader", 0);
		}else if (label.equals("2")) {
			indexIntAttribute.setIndexIntAttribute(json, "opinion_leader", 1);
		}
        indexLongAttribute.setIndexLongAttribute(json,"update_time", Long.valueOf(up.getUpdateTime()));

        if(up.hasDgInfo()){
            upDgDimension.fillUpDimension(up.getDgInfo(),json);
        }

        //用户画像-上网特征维度
        JSONArray internet_jsons = new JSONArray();
        if(up.getInterFtsCount() != 0){
            //上网特征按照渠道划分-PC，Mobile以及All
            upInternetDimension.fillUpDimension(up, internet_jsons);
        }
//        else if(up.hasInterFt()){
//            //上网特征原始的上网特征，不按照渠道划分
//            upOldInternetDimension.fillUpDimension(up.getInterFt(), internet_jsons);
//        }
        if(internet_jsons.size() != 0){
            indexArrayObject.setJSONObject(json, internet_jsons, "inter_ft");
        }

        //用户画像品类偏好维度
        JSONArray category_jsons = new JSONArray();
        for(int i = 0; i < up.getCidInfoCount(); ++i){
            cn.bfd.protobuf.UserProfile2.CidInfo cidInfo = up.getCidInfo(i);
            String cid = cidInfo.getCid();
            //填充指定CID(客户)的品类偏好信息
            if(!cidMap.containsKey(cid)){
                continue;
            }
            upCategoryDimension.fillUpDimension(cidInfo,category_jsons);
        }
        if(category_jsons.size() != 0){
            indexArrayObject.setJSONObject(json, category_jsons, "category");
        }

        //用户画像营销特征维度,level1
        if(up.hasMarketFt()){
            upMarketDimension.fillUpDimension(up.getMarketFt(),json);
        }
  }
}
