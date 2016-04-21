package cn.com.es;

import com.mongodb.MongoClient;

/**
 * Created by BFD_278 on 2015/7/19.
 */
public interface IntialMongoDBInstance {
    void IntialInstance(MongoClient client, String mongo_ip, int mongo_port);
}
