package cn.com.es;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.alibaba.fastjson.JSONException;

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
public class GetGidESMapper extends Mapper<LongWritable,Text,Text, Text>{

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
	private List<String> missedrowkeys = new ArrayList<String>();
	private Text keyText = new Text();
	private Text valueText = new Text();

    private ParseProtobuf2JSON parseProtobuf2JSON = null;
    private String age_map_path = null;
    private String sex_map_path = null;
    private String internet_time_map_path = null;
    private String cid_fin = null;
    private FileSystem fs = null;
    
    //
    
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
		
		if (gids.size() > 0) {
			indexHBase(gids,context);
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
	public void getDocs(List<TGet> gets,List<String> gids,boolean columnIsEmpty,Context context) throws InterruptedException{
		int retryCount = 0;
		List<String> findgids = new ArrayList<String>();
		if(gets.size() == 0){
        		return;
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
	                    		keyText.set(error.getMessage());
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}catch(Exception e){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set(e.getMessage());
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}
	                    }
	                    if (up == null) {
	                    	continue;
	                    }
	                    try{
	                    	//JSONObject doc  = parseProtobuf.parse2doc(up,mapConfig);
	                    	//to do
	                    	int length = up.toByteString().size();
	                    	System.out.println("field--------"+up.getAllFields().size());
	                    	System.out.println("size-----"+up.getSerializedSize());
	                    	System.out.println("length--------"+length);
	                    	//JSONObject doc = new JSONObject();
	                    	//parseProtobuf2JSON.parse2JSon(up, doc);
	                    	String gid = up.getUid();
	                    	keyText.set(gid);
                    		valueText.set(""+length);
                    		context.write(keyText, valueText);
	                    	}catch(Error error){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set(error.getMessage());
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}catch(Exception e){
	                    		findgids.add(UpUtils.reverseString(rowKey));
	                    		keyText.set(e.getMessage());
	                    		valueText.set(rowKey);
	                    		context.write(keyText, valueText);
	                    		continue;
	                    	}
	                    findgids.add(UpUtils.reverseString(rowKey));
	                }
	                gids.removeAll(findgids);
	                //记录下column名称为空的rowKey,在第二次查询中如果还未找到则输出
	                if (!columnIsEmpty) {
	                	missedrowkeys.clear();
						missedrowkeys.addAll(gids);
					}else{
						for (String missedRowkey : gids) {
							keyText.set("missedkey");
                    		valueText.set(missedRowkey);
                    		context.write(keyText, valueText);
						}
					}
	                break;
	            } catch (Exception  e) {
	            	transport.close();
	        		System.out.println("Get gids from HBase Faild!!! retry : " +retryCount+ "message : "+ e.getMessage());
	        		Thread.sleep(2000);
	                e.printStackTrace();
	            }
	        }
	}
	
	 void getUserProfileFromHBase(List<String> gids,boolean columnIsEmpty,Context context) throws JSONException, TIOError, TException, InterruptedException, IOException {
		    List<TGet> gets = null;
		 	if (columnIsEmpty) {
				gets = getColumnIsEmptyGets(gids);
			}else{
				gets = getGets(gids);
			}
	        getDocs(gets,gids,columnIsEmpty,context);
	    }

	
	/**
	 * 
	 * 针对gid数据先在hbase表中查找记录，再在es中建立索引，
	 * 如果column列名非空，针对在hbase中未匹配的数据要在空列名下再查找一次
	 */
	public void indexHBase(List<String> gids,Context context) {
		try {
			getUserProfileFromHBase(gids,false,context);
			gids.clear();
			if (column != null && column.trim().length() > 0) {
				if (missedrowkeys != null && missedrowkeys.size() > 0) {
					getUserProfileFromHBase(missedrowkeys,true,context);
				}
			}
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
        
        return parseProtobuf2JSON;

	}
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		try {
			String gid = value.toString();
			gids.add(gid);
			if (gids.size() == scan_gid_size) {
				indexHBase(gids,context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
