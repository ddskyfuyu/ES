package cn.com.es;

import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * Created by BFD_278 on 2015/7/19.
 */
public class IntialMongoDBAction implements IntialMongoDBInstance {

    public void IntialInstance(MongoClient client, String mongo_ip, int mongo_port) {
        try {
            client = new MongoClient(mongo_ip, mongo_port);
        } catch (UnknownHostException e) {
            client = null;
        }
    }
}
