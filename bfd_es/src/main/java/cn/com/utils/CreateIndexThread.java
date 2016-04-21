package cn.com.utils;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateIndexThread implements Runnable{
	private static Logger LOG = Logger.getLogger(CreateIndexThread.class);
	private String index;
	private String type;
	private List<Pair> docs;
	private Client client;
	public CreateIndexThread(Client client, String index, String type, List<Pair> docs) {
		this.index = index;
		this.type = type;
		this.docs = docs;
		this.client = client;
	}


	public void run() {
		long start = System.currentTimeMillis();
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
		for(int i=0; i<docs.size(); ++i) {
			bulkRequestBuilder.add(this.client.prepareIndex(index, type, docs.get(i).first).setSource(docs.get(i).second));
		}
		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
		if(bulkResponse.hasFailures()) {
			LOG.error(" faild messge : " + bulkResponse.buildFailureMessage());
		}
		LOG.info("[Upload docs number: " + docs.size() + "] [spent: " + (System.currentTimeMillis() - start) + "]");
		System.out.println("[Upload docs number: " + docs.size() + "] [spent: " + (System.currentTimeMillis() - start) + "]");
		
	}
	public static void main(String[] args) throws IOException {
		// 配置你的es,现在这里只配置了集群的名,默认是elasticsearch,跟服务器的相同
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "bfd_index").build();
		// 这里可以同时连接集群的服务器,可以多个,并且连接服务是可访问的
		Client client = new TransportClient(settings).addTransportAddress( new InetSocketTransportAddress("192.168.61.89", 9300));
		FileReader fr = new FileReader("sample.txt");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		List<Pair> list = new ArrayList<Pair>();
		while((line=br.readLine()) != null) {
			String[] array = line.split(" ");
			String gid = array[0];
			String doc = array[1];
			list.add(new Pair(gid, doc));
			if (list.size() > 100) {
				CreateIndexThread my = new CreateIndexThread(client, "user_v2", "user", list);
				new Thread(my).start();
				list.clear();
			}
		}
		new Thread(new CreateIndexThread(client, "user_v2", "user", list)).start();
	}
}
