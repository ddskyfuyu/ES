package cn.com.indexUp;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.IndexIntAttribute;
import com.alibaba.fastjson.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu.fu on 2015/7/23.
 * Fill Market attributes of userprofile
 */

public class IndexUpMarketDimension implements IndexUpDimension {
    private IndexIntAttribute indexIntAttribute;


    public IndexUpMarketDimension(IndexIntAttribute indexIntAttribute){
        this.indexIntAttribute = indexIntAttribute;
    }


  public void setIndexIntAttribute(IndexIntAttribute indexIntAttribute){
        this.indexIntAttribute = indexIntAttribute;
    }

    private void buildListBySInfo(List<String> list, List<UserProfile2.SInfo> sInfos){
        for(UserProfile2.SInfo sInfo : sInfos){
            list.add(sInfo.getValue());
        }
    }


    /*
     * Fill Market attributes of userprofile into json
     *
     * @param object an instance of demographics of userprofile
     * @param json the data of demographics of userprofile
     */
    public void fillUpDimension(Object object, JSONObject json) {

        if(!(object instanceof UserProfile2.MarketingFeatures)){
            return;
        }
        UserProfile2.MarketingFeatures mk_ft = (UserProfile2.MarketingFeatures)object;

        //营销价值-消费周期 con_period_new
        if (mk_ft.getConPeriodNewCount() > 0) {
            List<Integer> list = new ArrayList<Integer>();
            for (UserProfile2.IInfo iInfo : mk_ft.getConPeriodNewList()) {
                list.add(iInfo.getValue());
            }
            if(list.size() != 0){
                json.put("market_info.cyc_info", list);
            }
        }

        //营销价值-消费水平 con_level_new
        if(mk_ft.getConLevelNewCount() != 0){
            indexIntAttribute.setIndexIntAttribute(json,"market_info.price_level",Integer.valueOf(mk_ft.getConLevelNew(0).getValue()));
        }else{
        	if(mk_ft.getPriceLevelCount() > 0){
        		indexIntAttribute.setIndexIntAttribute(json,"market_info.price_level",Integer.valueOf(mk_ft.getPriceLevel(0).getValue()));
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


