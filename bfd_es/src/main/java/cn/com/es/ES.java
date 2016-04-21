package cn.com.es;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import cn.com.utils.FileUtils;

public class ES extends Configured implements Tool{
	
	public static void main(String[] args) throws Exception {
		if (args.length < 7 ) {
			String str = "usage <config_file> <hbase_table> <family> <column>";
			str += " <scan_gid_size>  <inputPath> <outputPath>";
			System.out.println(str);
			System.exit(-1);
		}
		int result = ToolRunner.run(new ES(), args);
		System.exit(result);
	}

	public int run(String[] args) throws Exception {
		Configuration configuration = getConf();
		
		FileSystem fs = FileSystem.get(configuration); 

        String config_file = args[0];
        String hbase_table = args[1];
        String family = args[2];
        String column = args[3];
        String scan_gid_size = args[4];
        String inputPath = args[5];
        String outputPath = args[6];
        String min_split=args[7];
        String max_split=args[8];
		
		Map<String,String> map = new HashMap<String,String>();
		FileUtils.FillStrValMap(map, config_file, "=", fs);
	    configuration.set("age_map_path", map.get("age_map_path"));
	    configuration.set("sex_map_path", map.get("sex_map_path"));
	    configuration.set("internet_time_map_path", map.get("internet_time_map_path"));
	    configuration.set("cid_fin", map.get("cid_fin"));
	    // hbase configuration
        configuration.set("hbase_thrift_ip", map.get("hbase_thrift_ip"));
        configuration.set("hbase_thrift_port", map.get("hbase_thrift_port"));
		
        configuration.set("mapred.textoutputformat.separator", ",");
		//conf.set("hbase.zookeeper.quorum", "192.168.112.11,192.168.112.12,192.168.112.13");
		//conf.set("zookeeper.znode.parent", "/bfdhbase");

        configuration.set("cluster_name", map.get("cluster_name"));
        configuration.set("es_ip", map.get("es_ip"));
        configuration.set("es_port", map.get("es_port"));
        configuration.set("es_index", map.get("es_index"));
        configuration.set("es_type", map.get("es_type"));
      
		configuration.set("hbase_table", hbase_table);
		configuration.set("family", family);
        configuration.set("column", column);
        configuration.set("scan_gid_size", scan_gid_size);
		
        Job job = Job.getInstance(configuration, "CreateIndex");

        job.setJarByClass(ES.class);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(ESMapper.class);
		if (args.length>7) {
			System.out.println("MinSplitSize before set:"+FileInputFormat.getMinSplitSize(job));
			FileInputFormat.setMinInputSplitSize(job, Long.parseLong(min_split));
			System.out.println("MinSplitSize after set:"+FileInputFormat.getMinSplitSize(job));
		}
		if (args.length>8) {
			System.out.println("MaxSplitSize before set:"+FileInputFormat.getMaxSplitSize(job));
			FileInputFormat.setMaxInputSplitSize(job, Long.parseLong(max_split));
			System.out.println("MaxSplitSize after set:"+FileInputFormat.getMaxSplitSize(job));
		}
		job.setNumReduceTasks(0);
		return job.waitForCompletion(true) ? 0 : 1;
	}

}
