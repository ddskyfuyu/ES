package cn.com.es;

import cn.com.utils.Pair;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import java.util.List;

/**
 * Created by yu.fu on 2015/7/25.
 */
public class IndexBatchDocs implements IndexDocsInES {

    public void indexDocs(Client client, String index_name, String type_name, List<Pair> docs) {
        long start = System.currentTimeMillis();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for(Pair doc : docs){
            bulkRequestBuilder.add(client.prepareIndex(index_name, type_name, doc.first).setSource(doc.second));
        }
        BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
        if(bulkResponse.hasFailures()) {
            System.out.println(" faild messge : " + bulkResponse.buildFailureMessage());
        }
        System.out.println("[Upload docs number: " + docs.size() + "] [spent: " + (System.currentTimeMillis() - start) + "]");
    }
}
