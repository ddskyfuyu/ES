package cn.com.es;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.thrift2.generated.TColumn;
import org.apache.hadoop.hbase.thrift2.generated.TGet;
import org.apache.hadoop.hbase.thrift2.generated.THBaseService;
import org.apache.hadoop.hbase.thrift2.generated.TIOError;
import org.apache.hadoop.hbase.thrift2.generated.TResult;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.IndexBooleanLevelAttribute;
import cn.com.indexAttribue.IndexCategoryArrayObject;
import cn.com.indexAttribue.IndexDoubleLevelAttribute;
import cn.com.indexAttribue.IndexFifthCategoryNestedObject;
import cn.com.indexAttribue.IndexFirstCategoryNestedObject;
import cn.com.indexAttribue.IndexFloatLevelAttribute;
import cn.com.indexAttribue.IndexFourthCategoryNestedObject;
import cn.com.indexAttribue.IndexIntLevelAttribute;
import cn.com.indexAttribue.IndexLongLevelAttribute;
import cn.com.indexAttribue.IndexMethodCategoryNestedObject;
import cn.com.indexAttribue.IndexSecondCategoryNestedObject;
import cn.com.indexAttribue.IndexStringLevelAttribute;
import cn.com.indexAttribue.IndexThirdCategoryNestedObject;
import cn.com.indexAttribue.IndexTimeStampLevelAttribute;
import cn.com.indexUp.IndexUpCategoryDimension;
import cn.com.indexUp.IndexUpDgDimension;
import cn.com.indexUp.IndexUpInternetDimension;
import cn.com.indexUp.IndexUpMarketDimension;
import cn.com.indexUp.IndexUpOldInternetDimension;
import cn.com.utils.Pair;
import cn.com.utils.UpUtils;
/*
import com.bfd.data.Pair;
import com.bfd.data.UserProfile2;
import com.bfd.util.UpUtils;
import com.bfd.util.ParseProtobuf2;
*/

/**
 * version 20150817
 * @author BFD_642
 *
 */
public class TestESMapper extends Mapper<LongWritable,Text,Text, Text>{

	private Client client = null; //es客户端
	private TTransport transport = null;//thrift服务
	private THBaseService.Iface hbase_client = null;//hbase客户端
	
	private String hbase_thrift_ip = null;//hbase的thrift服务ip地址
	private int hbase_thrift_port ;//hbase的thrift服务端口
	private int scan_gid_size;//批处理从hbase中取数据条数
	private String hbase_table = null;//hbase表名
	private String family = null;//hbase列簇
	private String column = null;//hbase与列簇对应的列

	private String cluster_name = null;//es集群名称
	private String es_ip = null;//es的IP
	private int es_port ;//es的端口
	private String index = null;//es索引名称
	private String type = null;//es与索引名称相对应的索引类型
	
	//private ParseProtobuf2 parseProtobuf = new ParseProtobuf2();
	private Map<String, String> mapConfig = new HashMap<String,String>();//处理Protobuf需要的额外信息
	private List<String> gids= new ArrayList<String>();
	private Map<String, String> gidLabelMap = new HashMap<String,String>();
	private Map<String, String> missedGidLabelMap = new HashMap<String,String>();
	//private List<Map<String,String>> gid_labels= new ArrayList<Map<String, String>>();
	private List<String> missedrowkeys = new ArrayList<String>();
	private Text keyText = new Text();
	private Text valueText = new Text();

    private ParseProtobuf2JSON parseProtobuf2JSON = null;
    private String age_map_path = null;
    private String sex_map_path = null;
    private String internet_time_map_path = null;
    private String cid_fin = null;
    private FileSystem fs = null;
    
	@SuppressWarnings("resource")
	protected void setup(Context context) throws IOException, InterruptedException {

		Configuration conf = context.getConfiguration();
		fs=FileSystem.get(conf); 
		age_map_path = conf.get("age_map_path");
		sex_map_path = conf.get("sex_map_path");
		internet_time_map_path = conf.get("internet_time_map_path");
		cid_fin = conf.get("cid_fin");
		
		family = conf.get("family");
		column = conf.get("column");
		scan_gid_size = Integer.valueOf(conf.get("scan_gid_size"));
		
		cluster_name = conf.get("cluster_name");
		index = conf.get("es_index");
		type = conf.get("es_type");
		es_ip = conf.get("es_ip");
		es_port = Integer.valueOf(conf.get("es_port"));
		
		hbase_table = conf.get("hbase_table");
		hbase_thrift_ip = conf.get("hbase_thrift_ip");
		hbase_thrift_port = Integer.valueOf(conf.get("hbase_thrift_port"));
		
		mapConfig.put("CITY_LEVEL", conf.get("CITY_LEVEL"));
		mapConfig.put("OPER_SYSTEMS", conf.get("OPER_SYSTEMS"));
		mapConfig.put("BROWSER", conf.get("BROWSER"));
		
		transport = new TSocket(hbase_thrift_ip, hbase_thrift_port);
	    TProtocol protocol = new TBinaryProtocol(transport);
	    hbase_client = new THBaseService.Client(protocol); 
		System.out.println("hbasetable--------"+hbase_table+"-----family-------"+family+"------column-----"+column);
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster_name).build();
	    client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(es_ip,es_port));
	    parseProtobuf2JSON = getParseProtobuf2JSON();
	}
	
	protected void cleanup(Context context) throws IOException, InterruptedException {
		
		if (missedGidLabelMap.size() > 0) {
			indexHBase(missedGidLabelMap,context);
		}
		if (client !=null) {
			client.close();
		}
	}
	
	public List<TGet> getGets(List<String> gids){
		String gid = null;
		String rowkey = null;
		 List<TGet> gets = new ArrayList<TGet>();
		for (int i = 0; i < gids.size(); ++i) {
		        gid = gids.get(i);
		        if (gid == null || gid.isEmpty()){
		        	continue;
		        }
		        TGet g = new TGet();
		        rowkey = UpUtils.reverseString(gid);
		        g.setRow(rowkey.getBytes());
		        TColumn t = new TColumn();
		        t.setFamily(family.getBytes());
		        
		        if (column != null && column.trim().length() > 0) {
		        	t.setQualifier(column.getBytes());
				}else{
					t.setQualifier(Bytes.toBytes(""));
				}
		        g.addToColumns(t);
		        gets.add(g);
		 }
		return gets;
	}
	
	/**
	 * 
	 * 取得hbase表中column名称是空的记录
	 */
	public List<TGet> getColumnIsEmptyGets(List<String> gids){
		String rowkey = null;
		String gid = null;
		List<TGet> gets = new ArrayList<TGet>();
		for (int i = 0; i < gids.size(); ++i) {
		        gid = gids.get(i);
		        if (gid == null || gid.isEmpty()){
		        	continue;
		        }
		        TGet g = new TGet();
		        rowkey = UpUtils.reverseString(gid);
		        g.setRow(rowkey.getBytes());
		        TColumn t = new TColumn();
		        t.setFamily(family.getBytes());
				t.setQualifier(Bytes.toBytes(""));
		        g.addToColumns(t);
		        gets.add(g);
		 }
		return gets;
	}
	
	/**
	 *  从hbase表中得到索引记录 
	 */
	public ArrayList<Pair> getDocs(List<TGet> gets,Map<String, String> gidLabelMap,boolean columnIsEmpty,Context context) throws InterruptedException{
		ArrayList<Pair> docs = new ArrayList<Pair>();
		int retryCount = 0;
		List<String> findgids = new ArrayList<String>();
		Set<String> gidSet = gidLabelMap.keySet();
		List<String> gids = new ArrayList<String>(gidSet);
	
		if(gets.size() == 0){
	    }
        while(retryCount<5){
	        	try {
	        		retryCount++;
	            	transport.open();
	            	ByteBuffer table = ByteBuffer.wrap(hbase_table.getBytes());
	            	List<TResult> results = hbase_client.getMultiple(table, gets);
	            	transport.close();
	            	
	                for (int i = 0; i < results.size(); ++i) {
	                	String rowKey = null;
	                    UserProfile2.UserProfile up = null;
	                    if(!results.get(i).getColumnValues().isEmpty()){
	                    	rowKey = new String(results.get(i).getRow());
	                    	try{
	                    	up = UserProfile2.UserProfile
	            					.parseFrom(results.get(i).getColumnValues().get(0).getValue());
	                    	}catch(Error error){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set("getUserProfileError");
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}catch(Exception e){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set("getUserProfileError");
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}
	                    }
	                    if (up == null) {
	                    	continue;
	                    }
	                    try{
	                    	JSONObject doc = new JSONObject();
	                    	parseProtobuf2JSON.parse2JSon(gidLabelMap,up, doc);
	                    	String gid = up.getUid();
	                    	docs.add(new Pair(gid, doc.toString()));
	                    	}catch(Error error){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set("parseUserProfileError");
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}catch(Exception e){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set("parseUserProfileError");
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}
	                    findgids.add(UpUtils.reverseString(rowKey));
	                    //rowKey拿到是正常的
	                }
	                int totalNumber = gets.size();
	                int docsHitNumber = docs.size();
	                int gidNumber = gids.size();
	                int findHitNumber = findgids.size();
	                int hbaseHitNumber = results.size();
	                gids.removeAll(findgids);
	                int missHitNumber = 0;
	                int missgidLabelNumber = 0;
	                //记录下column名称为空的rowKey,在第二次查询中如果还未找到则输出
	                if (!columnIsEmpty) {
	                	missedrowkeys.clear();
	                	missedrowkeys.addAll(gids);

	                	missedGidLabelMap.clear();
	                	for (String missedRowkey : missedrowkeys) {
							missedGidLabelMap.put(missedRowkey,gidLabelMap.get(missedRowkey));
						}
	                	
						missHitNumber = missedrowkeys.size();
						missgidLabelNumber = missedGidLabelMap.size();
						System.out.println("First Get From Hbase [Total: " +totalNumber + ",missedgidlabel:"+missgidLabelNumber+",gid---------"+gidNumber+",docs Hit:" + docsHitNumber +", find:" + findHitNumber+", Miss:" + missHitNumber+",hbase result:" + hbaseHitNumber +"]" );
					}else{
						missedGidLabelMap.clear();
	                	for (String missedRowkey : gids) {
							missedGidLabelMap.put(missedRowkey,gidLabelMap.get(missedRowkey));
							keyText.set("missedkey");
                    		valueText.set(missedRowkey);
                    		context.write(keyText, valueText);
						}
	                	
						missHitNumber = gids.size();
						missgidLabelNumber = missedGidLabelMap.size();
						System.out.println("Second Get From Hbase [Total: " +totalNumber + ",missedgidlabel:"+missgidLabelNumber+",gid---------"+gidNumber+",docs Hit:" + docsHitNumber +", find:" + findHitNumber+", Miss:" + missHitNumber+",hbase result:" + hbaseHitNumber +"]" );

					}
	                break;
	            } catch (Exception  e) {
	            	transport.close();
	        		System.out.println("Get gids from HBase Faild!!! retry : " +retryCount+ "message : "+ e.getMessage());
	        		Thread.sleep(2000);
	                e.printStackTrace();
	            }
	        }
			return docs;
	}
	
	 ArrayList<Pair> getUserProfileFromHBase(Map<String, String> gidLabelMap,boolean columnIsEmpty,Context context) throws JSONException, TIOError, TException, InterruptedException, IOException {
		    List<TGet> gets = null;
		    Set<String> gidSet = gidLabelMap.keySet();
		    List<String> gids = new ArrayList<String>(gidSet);
		 	if (columnIsEmpty) {
				gets = getColumnIsEmptyGets(gids);
			}else{
				gets = getGets(gids);
			}
	        ArrayList<Pair> docs = getDocs(gets,gidLabelMap,columnIsEmpty,context);
	        return docs;
	    }

	/**
	 * 
	 * 在es中建立索引
	 */
	public void indexDocs(ArrayList<Pair> docs){
		if (!docs.isEmpty()) {
			BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
	  		for(int i=0; i<docs.size(); ++i) {
	  			bulkRequestBuilder.add(client.prepareIndex(index, type, docs.get(i).first).setSource(docs.get(i).second));
	  		}
	  		
	  		long start = System.currentTimeMillis();
	  		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
	  		/*
	  		if(bulkResponse.hasFailures()) {
	  			System.out.println(" faild messge : " + bulkResponse.buildFailureMessage());
	  		}
	  		*/
	  		System.out.println("[Upload docs number: " + docs.size() + "] [spent: " + (System.currentTimeMillis() - start) + "]");
		} 

	}

	/**
	 * 
	 * 针对gid数据先在hbase表中查找记录，再在es中建立索引，
	 * 如果column列名非空，针对在hbase中未匹配的数据要在空列名下再查找一次
	 */
	public void indexHBase(Map<String, String> gidLabelMap,Context context) {
		try {
			System.out.println("-------------begin---------------");
			ArrayList<Pair> docs  = getUserProfileFromHBase(gidLabelMap,false,context);
			indexDocs(docs);
			gids.clear();
			if (column != null && column.trim().length() > 0) {
				System.out.println("second index---"+hbase_table+":"+family+":"+column+" missed rowkeys size:"+missedGidLabelMap.size());
				if (missedGidLabelMap != null && missedGidLabelMap.size() > 0) {
					docs  = getUserProfileFromHBase(missedGidLabelMap,true,context);
					indexDocs(docs);
				}
			}
			System.out.println("---------------end-------------");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ParseProtobuf2JSON getParseProtobuf2JSON(){
		  IndexBooleanLevelAttribute indexBooleanLevelAttribute = new IndexBooleanLevelAttribute();
          IndexStringLevelAttribute indexStringLevelAttribute = new IndexStringLevelAttribute();
          IndexIntLevelAttribute indexIntLevelAttribute = new IndexIntLevelAttribute();
          IndexDoubleLevelAttribute indexDoubleLevelAttribute = new IndexDoubleLevelAttribute();
          IndexTimeStampLevelAttribute indexTimeStampLevelAttribute = new IndexTimeStampLevelAttribute();
          IndexFloatLevelAttribute indexFloatLevelAttribute = new IndexFloatLevelAttribute();
          IndexCategoryArrayObject indexCategoryArrayObject = new IndexCategoryArrayObject();
          IndexLongLevelAttribute indexLongLevelAttribute = new IndexLongLevelAttribute();
          IndexIntLevelAttribute indexIntAttribute = new IndexIntLevelAttribute();

          IndexMethodCategoryNestedObject indexMethodCategoryNestedObject = new IndexMethodCategoryNestedObject(indexFloatLevelAttribute,
                                                                                                                   indexStringLevelAttribute,
                                                                                                                   indexIntLevelAttribute,
                                                                                                                   indexTimeStampLevelAttribute);

          IndexFirstCategoryNestedObject indexFirstCategoryNestedObject = new IndexFirstCategoryNestedObject(indexFloatLevelAttribute,
                                                                                                             indexStringLevelAttribute,
                                                                                                             indexIntLevelAttribute,
                                                                                                             indexTimeStampLevelAttribute);

          IndexSecondCategoryNestedObject indexSecondCategoryNestedObject = new IndexSecondCategoryNestedObject(indexFloatLevelAttribute,
                                                                                                                indexStringLevelAttribute,
                                                                                                                indexIntLevelAttribute,
                                                                                                                indexTimeStampLevelAttribute);

          IndexThirdCategoryNestedObject indexThirdCategoryNestedObject = new IndexThirdCategoryNestedObject(indexFloatLevelAttribute,
                                                                                                             indexStringLevelAttribute,
                                                                                                             indexIntLevelAttribute,
                                                                                                             indexTimeStampLevelAttribute);

          IndexFourthCategoryNestedObject indexFourthCategoryNestedObject = new IndexFourthCategoryNestedObject(indexFloatLevelAttribute,
                                                                                                                indexStringLevelAttribute,
                                                                                                                indexIntLevelAttribute,
                                                                                                                indexTimeStampLevelAttribute);

          IndexFifthCategoryNestedObject indexFifthCategoryNestedObject = new IndexFifthCategoryNestedObject(indexFloatLevelAttribute,
                                                                                                             indexStringLevelAttribute,
                                                                                                             indexIntLevelAttribute,
                                                                                                             indexTimeStampLevelAttribute);


          //初始化IndexUpDgDimension对象
          IndexUpDgDimension upDgDimension = new IndexUpDgDimension(indexBooleanLevelAttribute,
                                                                    indexStringLevelAttribute,
                                                                    indexIntLevelAttribute,
                                                                    age_map_path,
                                                                    sex_map_path,fs);
          //初始化IndexUpCategoryDimension对象
          IndexUpCategoryDimension upCategoryDimension = new IndexUpCategoryDimension(indexCategoryArrayObject,
                                                                                      indexIntLevelAttribute,
                                                                                      indexDoubleLevelAttribute,
                                                                                      indexStringLevelAttribute,
                                                                                      indexMethodCategoryNestedObject,
                                                                                      indexFirstCategoryNestedObject,
                                                                                      indexSecondCategoryNestedObject,
                                                                                      indexThirdCategoryNestedObject,
                                                                                      indexFourthCategoryNestedObject,
                                                                                      indexFifthCategoryNestedObject);
          //初始化IndexUpInternetDimension
          IndexUpInternetDimension indexUpInternetDimension = new IndexUpInternetDimension(indexCategoryArrayObject,
                                                                                           indexIntLevelAttribute,
                                                                                           indexStringLevelAttribute,
                                                                                           internet_time_map_path,fs);
          //初始化IndexUpOldInternetDimension
          IndexUpOldInternetDimension indexUpOldInternetDimension = new IndexUpOldInternetDimension(indexCategoryArrayObject,
                                                                                                    indexIntLevelAttribute,
                                                                                                    indexStringLevelAttribute);
          //初始化IndexUpMarketDimension
          IndexUpMarketDimension indexUpMarketDimension = new IndexUpMarketDimension(indexIntLevelAttribute);
          
          ParseProtobuf2JSON parseProtobuf2JSON = new ParseProtobuf2JSON(cid_fin,
                  upDgDimension,
                  upCategoryDimension,
                  indexUpInternetDimension,
                  indexUpMarketDimension,
                  indexUpOldInternetDimension,fs);


		parseProtobuf2JSON.setIndexStringAttribute(indexStringLevelAttribute);
		parseProtobuf2JSON.setIndexLongAttribute(indexLongLevelAttribute);
		parseProtobuf2JSON.setIndexArrayObject(indexCategoryArrayObject);
        parseProtobuf2JSON.setIndexIntAttribute(indexIntAttribute);
		
        return parseProtobuf2JSON;

	}
	
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		try {
			String gid_label = value.toString();
			String[] split_vals = gid_label.split(",");
			String gid = null;
			String label = null;
			
			if (split_vals.length == 2) {
				gid = split_vals[0];
				label = split_vals[1];
				gidLabelMap.put(gid, label);
			}else{
				throw new Exception("LINE:"+key.get()+"---error gid_label:"+gid_label);
			}
			if (gidLabelMap.size() == scan_gid_size) {
				indexHBase(gidLabelMap,context);
				gidLabelMap.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
