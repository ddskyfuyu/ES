package cn.com.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by BFD_278 on 2015/7/21.
 */
public class UpUtils {

   public static final void getMappingFromFile(String fin, Map<String,Integer> map){
        try{
            String line;
            BufferedReader br = new BufferedReader(new FileReader(new File(fin)));
            while ((line = br.readLine()) != null) {
                String[] array = line.trim().split(",");
                if(array.length == 2){
                    map.put(array[0], Integer.valueOf(array[1]));
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String reverseString(String str) {
        char c[] = str.toCharArray();
        char t;
        for (int i = 0; i < (str.length() + 1) / 2; i++) {
            t = c[i];
            c[i] = c[c.length - i - 1];
            c[c.length - i - 1] = t;
        }
        String str2 = new String(c);
        return str2;
    }

    public static final void printOutput(ArrayList<Pair> lists){

        for(Pair item : lists){
            System.out.println(item.first + "," + item.second);
        }
    }

    public static final void printMap(Map<String, Integer> map){
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
    }

}
