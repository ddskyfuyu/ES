package cn.com.es;

import org.elasticsearch.client.Client;

/**
 * Created by BFD_278 on 2015/7/19.
 */
public interface IntialElasticsearchInstance {
    void IntialInstance(Client es_client, String es_ip, int es_port, String cluster_name);
}
