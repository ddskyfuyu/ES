package cn.com.es;

import com.mongodb.MongoClient;

/**
 * Created by yu.fu on 2015/7/19.
 */
public class IntialMongoDBNoAction implements IntialMongoDBInstance {

    public void IntialInstance(MongoClient client, String mongo_ip, int mongo_port) {
        client = null;
    }
}
