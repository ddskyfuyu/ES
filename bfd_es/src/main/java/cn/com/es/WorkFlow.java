package cn.com.es;

import cn.com.protobuf.BFDUserProfile;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by BFD_278 on 2015/7/19.
 */
interface WorkFlow {
    void Process(BFDUserProfile.UserProfile up, JSONObject result);
}
