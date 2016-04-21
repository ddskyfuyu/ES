package cn.com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Created by BFD_278 on 2015/7/23.
 */
public class FileUtils {

	public static void FillValMap(Map<String, Integer> map, String fin, String sep,FileSystem fs){
        try{
            String line = null;
            InputStream in=fs.open(new Path(fin));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            while((line = br.readLine()) != null){
                String[] items = line.trim().split(sep);
                if(items.length != 2 ){
                    continue;
                }
                map.put(items[0], Integer.valueOf(items[1]));
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    
    public static void FillMap(Map<String, Integer> map, String fin, String sep,FileSystem fs){
        try{
            String line = null;
            InputStream in=fs.open(new Path(fin));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null){
                String[] items = line.trim().split(sep);
                if(items.length != 1 ){
                    continue;
                }
                map.put(items[0], 1);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    

    public static void FillStrValMap(Map<String, String> map, String fin, String sep,FileSystem fs){
        try{
        	String line = null;
            InputStream in=fs.open(new Path(fin));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null){
                String[] items = line.trim().split(sep);
                if(items.length != 2 ){
                    continue;
                }
                map.put(items[0], items[1]);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void FillValMap(Map<String, Integer> map, String fin, String sep){
        try{
            String line = null;
            BufferedReader br = new BufferedReader(new FileReader(new File(fin)));
            while((line = br.readLine()) != null){
                String[] items = line.trim().split(sep);
                if(items.length != 2 ){
                    continue;
                }
                map.put(items[0], Integer.valueOf(items[1]));
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void FillMap(Map<String, Integer> map, String fin, String sep){
        try{
            String line = null;
            BufferedReader br = new BufferedReader(new FileReader(new File(fin)));
            while((line = br.readLine()) != null){
                String[] items = line.trim().split(sep);
                if(items.length != 1 ){
                    continue;
                }
                map.put(items[0], 1);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}
