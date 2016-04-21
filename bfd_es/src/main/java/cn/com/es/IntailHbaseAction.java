package cn.com.es;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * Created by BFD_278 on 2015/7/19.
 */
public class IntailHbaseAction implements IntialHbaseInstance{

    public void IntialInstance(Configuration conf, String hb_name) {
        Configuration hb_conf = HBaseConfiguration.create();
        hb_conf.set("hbase.zookeeper.quorum", "192.168.48.12,192.168.48.13,192.168.48.14");
        hb_conf.set("zookeeper.znode.parent", "/dp/bfdhbase");
        hb_conf.set("hbase.rootdir", "/hbase");
        if(hb_conf == null){
            System.out.println("################ InitalAction hb_conf is null ######################");
        }
        else{
            System.out.println("##################### IntialAction hb_conf is not null ####################");
        }
        conf = hb_conf;
        System.out.println("################ IntailHbaseAction ######################");
    }
}
