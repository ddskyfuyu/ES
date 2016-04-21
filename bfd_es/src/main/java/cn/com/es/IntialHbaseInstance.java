package cn.com.es;

import org.apache.hadoop.conf.Configuration;

/**
 * Created by yu.fu on 2015/7/16.
 */
public interface IntialHbaseInstance {

    void IntialInstance(Configuration conf, String hb_name);
};
