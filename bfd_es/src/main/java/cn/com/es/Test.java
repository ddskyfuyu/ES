package cn.com.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test {
	public static void test(String gid_label) throws IOException{
		Map<String, String> map = new HashMap<String,String>();
		String[] split_vals = gid_label.split(",");
		String gid = null;
		String label = null;
		if (split_vals.length == 2) {
			gid = split_vals[0];
			label = split_vals[1];
			map.put(gid, label);
			System.out.println("gid:"+gid);
			System.out.println("label:"+label);
		}
		
		 Set<String> gidSet = map.keySet();
		    List<String> gids = new ArrayList<String>(gidSet);
		   for (int i = 0; i < gids.size(); i++) {
			System.out.println(gids.get(i));
		}
     
	}
	
	public static void main(String[] args) throws IOException {
		test("b26decf4bbcd4bec000060990124173355c1e783,2");
	}
}
