package cn.com.es;

import cn.bfd.protobuf.UserProfile2;
import cn.com.indexAttribue.*;
import cn.com.indexUp.*;
import cn.com.utils.Pair;
import cn.com.utils.UpUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by yu.fu on 2015/7/19.
 */
public class Main {

    private  static String family ;
    private  static String column ;
    private  static String index_name;
    private  static String type_name;
    private Client client = null;                  //初始化Elasticsearch客户端
    private Configuration conf = null;             //初始化hbase客户端
    private MongoClient mongoClient;               //初始化mongodb客户端



    private IntialElasticsearchInstance es_intailIns;       //ES初始化操作
    private IntialHbaseInstance hb_initalIns;
    private IntialMongoDBInstance mg_initalIns;


    private ParseProtobuf2JSON parseProtobuf2JSON;
    private IndexDocsInES indexDocsInES;
    private static HTable table = null;



    public final void setEsIntailIns(IntialElasticsearchInstance inst){
        es_intailIns = inst;
    }

    public final void setParseProtobuf2JSON(ParseProtobuf2JSON parseProtobuf2JSON){
        this.parseProtobuf2JSON = parseProtobuf2JSON;
    }


    public final void setHbIntailIns(IntialHbaseInstance inst){
        hb_initalIns = inst;
    }

    public final void setMgIntailIns(IntialMongoDBInstance inst){
        mg_initalIns = inst;
    }

    public final void setIndexDocsInES(IndexDocsInES indexDocsInES){
        this.indexDocsInES = indexDocsInES;
    }

    public final void intialElastic(String cluster_name, String es_ip, int es_port, String mg_ip, int mg_port, String hb_name){
        es_intailIns.IntialInstance(client, es_ip, es_port, cluster_name);
        hb_initalIns.IntialInstance(conf, hb_name);
        mg_initalIns.IntialInstance(mongoClient, mg_ip, mg_port);

    }



    public Main(IntialElasticsearchInstance es_inst, IntialHbaseInstance hb_inst, IntialMongoDBInstance mg_inst){
        setEsIntailIns(es_inst);
        setHbIntailIns(hb_inst);
        setMgIntailIns(mg_inst);
    }

    public Main(IntialElasticsearchInstance es_inst, IntialHbaseInstance hb_inst, IntialMongoDBInstance mg_inst,
                String family, String column, String index_name, String type_name){
        this(es_inst, hb_inst,mg_inst);
        this.family = family;
        this.column = column;
        this.index_name = index_name;
        this.type_name = type_name;
    }

    public Main(){

    }

    public Configuration getConf(){
        return conf;
    }

    public final ArrayList<Pair> getUserProfileFromHbase(ArrayList<String> rowkeys) throws JSONException {
        int total = rowkeys.size();
        ArrayList<Pair> docs = new ArrayList<Pair>();
        ArrayList<Get> gets = new ArrayList<Get>();
        HashSet<String> rowKeySet = new HashSet<String>();
        for (int i = 0; i < rowkeys.size(); ++i) {
            String rowkey = rowkeys.get(i);
            Get g = new Get(rowkey.getBytes());
            g.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
            gets.add(g);
            rowKeySet.add(rowkey);
        }
        try {
            long getstart = System.currentTimeMillis();
            Result[] results = table.get(gets);
            System.out.println("Hbase: " + rowkeys.size() + " docs: " + docs.size() + " spent: " + (System.currentTimeMillis() - getstart));
            for (int i = 0; i < results.length; ++i) {
                UserProfile2.UserProfile up = null;
                if(!results[i].isEmpty()){
                    up = UserProfile2.UserProfile.parseFrom(results[i].list().get(0).getValue());
                }
                if (up == null) {
                    System.out.println("parse protobuf value error ");
                    continue;
                }
                JSONObject doc = new JSONObject();
                parseProtobuf2JSON.parse2JSon(up, doc);
                String gid = up.getUid();
                docs.add(new Pair(gid, doc.toString()));
                rowKeySet.remove(UpUtils.reverseString(gid));
            }
            for(String rowkey : rowKeySet){
                System.out.println("Parse Failed. rowkey:" + rowkey);
            }
            System.out.println("Total: " + total + ",Hbase Hit: " + docs.size() + "] " + " [Others Hit: " + (total - docs.size()) + "]");
        } catch (IOException e) {
            System.out.println("Get gids from HBase Faild!!!" + e.getMessage());
            return docs;
        }
        return docs;
    }

    public final void run(String gid_fin, String htable_name,String cluster_name, String es_ip, int es_port, int batch_num){

        try {
            FileReader fr = null;
            fr = new FileReader(gid_fin);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            if(conf == null){
                conf = HBaseConfiguration.create();
                conf.set("hbase.zookeeper.quorum", "192.168.48.12,192.168.48.13,192.168.48.14");
                conf.set("zookeeper.znode.parent", "/dp/bfdhbase");
                conf.set("hbase.rootdir", "/hbase");
            }
            if(client == null){
                Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster_name).build();
                client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(es_ip,es_port));
            }
            table = new HTable(conf, htable_name);
            ArrayList<String> rowkeys = new ArrayList<String>();
            while((line = br.readLine()) != null){
                String rowkey = line.trim();
                rowkeys.add(rowkey);
                if(rowkeys.size() == batch_num){
                    ArrayList<Pair> docs = getUserProfileFromHbase(rowkeys);
                    rowkeys.clear();
                    indexDocsInES.indexDocs(client,index_name,type_name,docs);
                }
            }
            if(rowkeys.size() > 0){
                ArrayList<Pair> docs = getUserProfileFromHbase(rowkeys);
                rowkeys.clear();
                indexDocsInES.indexDocs(client, index_name, type_name, docs);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(0);
        }


    }
    public static void main(String[] args) {

        try {
            PropertiesConfiguration config = new PropertiesConfiguration("conf/conf.properties");
            if(args.length != 8){
                System.out.println("Usage: <gid_file> <cid_file> <age_map_path> <sex_map_path> <internet_time_map_path> <batch_num> <index> <type>");
                System.exit(0);
            }

            String gid_fin = args[0];             //gid输入文件名
            String cid_fin = args[1];             //建立索引的CID名称的文件名
            String age_map_path = args[2];        //年龄映射表文件名
            String sex_map_path = args[3];        //性别映射表文件名
            String internet_time_map_path = args[4];        //上网时段映射表文件名
            int batch_num = Integer.valueOf(args[5]);    //批量个数
            String index_name = args[6];                 //索引的名称
            String type_name = args[7];                  //类型的名称


            String cluster_name = config.getString("elasticsearch.cluster_name");        //集群名称
            String es_ip = config.getString("elasticsearch.ip");                        //elasticsearch 客户端IP
            int es_port = config.getInt("elasticsearch.port");                      //elasticsearch 客户端端口
            String mg_ip = config.getString("mongo.ip");                            //mongodb 客户端IP
            int mg_port = config.getInt("mongo.port");                              //mongodb 客户端port
            String hb_name = config.getString("hbase.table.name");                 //hbase表名
            String hb_family = config.getString("hbase.table.family");             //hbase 列簇的名字
            String hb_column = config.getString("hbase.table.column");                 //hbase 列的名字

            //DEBUG
            if(hb_column.equals("")){
                System.out.println("The column is empty. ");
            }


            Main main_inst = new Main(new IntialElasticsearchAction(), new IntailHbaseAction(), new IntialMongoDBNoAction(),
                                      hb_family, hb_column, index_name, type_name);

            main_inst.intialElastic(cluster_name, es_ip,es_port, mg_ip,mg_port, hb_name);
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
                                                                      sex_map_path,null);
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
                                                                                             internet_time_map_path,null);
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
                                                                           indexUpOldInternetDimension,null);


            parseProtobuf2JSON.setIndexStringAttribute(indexStringLevelAttribute);
            parseProtobuf2JSON.setIndexLongAttribute(indexLongLevelAttribute);
            parseProtobuf2JSON.setIndexArrayObject(indexCategoryArrayObject);

            IndexBatchDocs indexBatchDocs = new IndexBatchDocs();
            main_inst.setParseProtobuf2JSON(parseProtobuf2JSON);
            main_inst.setIndexDocsInES(indexBatchDocs);
            main_inst.run(gid_fin, hb_name, cluster_name,es_ip,es_port,batch_num);

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }









}
