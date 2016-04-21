package cn.com.es;

import org.elasticsearch.client.Client;

/**
 * Created by BFD_278 on 2015/7/19.
 */
public class IntialElasticsearchNoAction implements IntialElasticsearchInstance{

    public void IntialInstance(Client client, String es_ip, int es_port, String cluster_name) {
        client = null;
    }

}
