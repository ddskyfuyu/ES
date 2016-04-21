package cn.com.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by BFD_278 on 2015/7/19.
 */
public class IntialElasticsearchAction implements IntialElasticsearchInstance{

    public void IntialInstance(Client client, String es_ip, int es_port, String cluster_name) {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster_name).build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(es_ip,es_port));
    }
}
