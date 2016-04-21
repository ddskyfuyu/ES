package cn.com.es;

import cn.com.utils.Pair;
import org.elasticsearch.client.Client;

import java.util.List;

/**
 * Created by yu.fu on 2015/7/25.
 */
public interface IndexDocsInES {

    void indexDocs(Client client, String index_name, String type_name, List<Pair> docs);
}
