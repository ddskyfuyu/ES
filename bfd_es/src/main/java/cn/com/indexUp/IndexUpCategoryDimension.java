package cn.com.indexUp;

import cn.bfd.protobuf.UserProfile2;
import cn.bfd.protobuf.UserProfile2.FirstCategory;
import cn.com.indexAttribue.IndexArrayObject;
import cn.com.indexAttribue.IndexDoubleAttribute;
import cn.com.indexAttribue.IndexDoubleLevelAttribute;
import cn.com.indexAttribue.IndexIntAttribute;
import cn.com.indexAttribue.IndexNestedObject;
import cn.com.indexAttribue.IndexStringAttribute;

import com.alibaba.fastjson.JSONObject;
import org.json.simple.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by yu.fu on 2015/7/23.
 *
 *
 */
public class IndexUpCategoryDimension implements IndexUpDimension {
	
    private IndexArrayObject indexArrayObject;
    private IndexIntAttribute indexIntAttribute;
    private IndexDoubleAttribute indexDoubleAttribute;
    private IndexStringAttribute indexStringAttribute;
    private IndexNestedObject indexNestedObject;
    private IndexNestedObject indexFirstNestedObject;
    private IndexNestedObject indexSecondNestedObject;
    private IndexNestedObject indexThirdNestedObject;
    private IndexNestedObject indexFourthNestedObject;
    private IndexNestedObject indexFifthNestedObject;
    Map<Integer, Integer> cateweightmap= new TreeMap<Integer, Integer>();
    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						return res != 0 ? -res : 1;
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

    public IndexUpCategoryDimension(IndexArrayObject indexArrayObject, IndexIntAttribute indexIntAttribute, IndexDoubleAttribute indexDoubleAttribute, IndexStringAttribute indexStringAttribute,
                                    IndexNestedObject indexNestedObject,IndexNestedObject indexFirstNestedObject, IndexNestedObject indexSecondNestedObject,
                                    IndexNestedObject indexThirdNestedObject, IndexNestedObject indexFourthNestedObject, IndexNestedObject indexFifthNestedObject){

        this.indexArrayObject = indexArrayObject;
        this.indexIntAttribute = indexIntAttribute;
        this.indexDoubleAttribute = indexDoubleAttribute;
        this.indexStringAttribute = indexStringAttribute;
        this.indexNestedObject = indexNestedObject;
        this.indexFirstNestedObject = indexFirstNestedObject;
        this.indexSecondNestedObject = indexSecondNestedObject;
        this.indexThirdNestedObject = indexThirdNestedObject;
        this.indexFourthNestedObject = indexFourthNestedObject;
        this.indexFifthNestedObject = indexFifthNestedObject;
    }


    public void setIndexArrayObject(IndexArrayObject indexArrayObject){
        this.indexArrayObject = indexArrayObject;
    }

    public void setIndexIntAttribute(IndexIntAttribute indexIntAttribute) {this.indexIntAttribute = indexIntAttribute;}

    public void setIndexStringAttribute(IndexStringAttribute indexStringAttribute){
        this.indexStringAttribute = indexStringAttribute;
    }

    public void setIndexNestedObject(IndexNestedObject indexNestedObject){
        this.indexNestedObject = indexNestedObject;
    }

    public void setIndexNestedObject(IndexNestedObject indexFirstNestedObject,
                                     IndexNestedObject indexSecondNestedObject,
                                     IndexNestedObject indexThirdNestedObject,
                                     IndexNestedObject indexFourthNestedObject,
                                     IndexNestedObject indexFifthNestedObject){

        this.indexFirstNestedObject = indexFirstNestedObject;
        this.indexSecondNestedObject = indexSecondNestedObject;
        this.indexThirdNestedObject = indexThirdNestedObject;
        this.indexFourthNestedObject = indexFourthNestedObject;
        this.indexFifthNestedObject = indexFifthNestedObject;


    }

    public void parseBfdCategory(JSONObject obj, UserProfile2.AttributeInfo attr) {
    	//max_price
    	if(attr.hasMaxPrice()){
    		indexDoubleAttribute.setIndexDoubleAttribute(obj, "max_price", (double) attr.getMaxPrice());
        }
    	
    	//min_price
    	 if(attr.hasMinPrice()){
    		 indexDoubleAttribute.setIndexDoubleAttribute(obj, "min_price", (double) attr.getMinPrice());
         }
    	 

         //average_price
         if(attr.hasMaxPrice() && attr.hasMinPrice()){
        	 indexDoubleAttribute.setIndexDoubleAttribute(obj, "average_price", (double) ((attr.getMinPrice() + attr.getMaxPrice()) / 2));
         }
    	 
    	 //price_sum
    	 if(attr.hasPriceSum()){
    		 indexDoubleAttribute.setIndexDoubleAttribute(obj, "price_sum", (double) attr.getPriceSum());
    	 }
    	 
    	//price_num
    	 if(attr.hasPriceNum()){
    		 indexDoubleAttribute.setIndexDoubleAttribute(obj, "price_num", (double) attr.getPriceNum());
    	 }
    	 
    	 //brands
    	 JSONArray brands = new JSONArray();
    	 for (int i = 0; i < attr.getBrandsCount(); ++i) {
             brands.add(attr.getBrands(i).getValue());
         }
         if (!brands.isEmpty()) {
             indexArrayObject.setJSONObject(obj, brands, "brands");
         }
    	 
    	 //peoples
    	 if(attr.hasPeoples()){
    		 indexIntAttribute.setIndexIntAttribute(obj, "peoples",  attr.getPeoples());
    	 }
    	 
    	 //price_level
    	 JSONArray price_levels = new JSONArray();
    	 for (int i = 0; i < attr.getPriceLevelCount(); ++i) {
    		 price_levels.add( attr.getPriceLevel(i).getValue() );
         }
         if (!price_levels.isEmpty()) {
             indexArrayObject.setJSONObject(obj, price_levels, "price_level");
         }
    	 
        //tags
        JSONArray tags = new JSONArray();
        for (int i = 0; i < attr.getTagsCount(); ++i) {
            tags.add(attr.getTags(i).getValue());
        }
        if (!tags.isEmpty()) {
            indexArrayObject.setJSONObject(obj, tags, "tags");
        }
        
        //colours
        JSONArray colours = new JSONArray();
        for (int i = 0; i < attr.getColoursCount(); ++i) {
        	colours.add(attr.getColours(i).getValue());
        }
        if (!colours.isEmpty()) {
            indexArrayObject.setJSONObject(obj, colours, "colours");
        }
        
        //shape
        JSONArray shapes = new JSONArray();
        for (int i = 0; i < attr.getShapeCount(); ++i) {
        	shapes.add(attr.getShape(i).getValue());
        }
        if (!shapes.isEmpty()) {
            indexArrayObject.setJSONObject(obj, shapes, "shape");
        }
        
        //material
        JSONArray material = new JSONArray();
        for (int i = 0; i < attr.getMaterialCount(); ++i) {
        	material.add(attr.getMaterial(i).getValue());
        }
        if (!material.isEmpty()) {
            indexArrayObject.setJSONObject(obj, material, "material");
        }
        
        //style
        JSONArray style = new JSONArray();
        for (int i = 0; i < attr.getStyleCount(); ++i) {
        	style.add(attr.getStyle(i).getValue());
        }
        if (!style.isEmpty()) {
            indexArrayObject.setJSONObject(obj, style, "style");
        }
        
        //fashion
        JSONArray fashions = new JSONArray();
        for (int i = 0; i < attr.getFashionCount(); ++i) {
        	fashions.add(attr.getFashion(i).getValue());
        }
        if (!fashions.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fashions, "fashion");
        }
        
        //sizes
        JSONArray sizes = new JSONArray();
        for (int i = 0; i < attr.getSizesCount(); ++i) {
        	sizes.add(attr.getSizes(i).getValue());
        }
        if (!sizes.isEmpty()) {
            indexArrayObject.setJSONObject(obj, sizes, "sizes");
        }
        
        //crowd
        JSONArray crowd = new JSONArray();
        for (int i = 0; i < attr.getCrowdCount(); ++i) {
        	crowd.add(attr.getCrowd(i).getValue());
        }
        if (!crowd.isEmpty()) {
            indexArrayObject.setJSONObject(obj, crowd, "crowd");
        }
        
	    //sexes
        JSONArray sexes = new JSONArray();
        for (int i = 0; i < attr.getSexesCount(); ++i) {
        	sexes.add(attr.getSexes(i).getValue());
        }
        if (!sexes.isEmpty()) {
            indexArrayObject.setJSONObject(obj, sexes, "sexes");
        }
        
        //origin
        JSONArray origins = new JSONArray();
        for (int i = 0; i < attr.getOriginCount(); ++i) {
        	origins.add(attr.getOrigin(i).getValue());
        }
        if (!origins.isEmpty()) {
            indexArrayObject.setJSONObject(obj, origins, "origin");
        }
        
        //district
        JSONArray districts = new JSONArray();
        for (int i = 0; i < attr.getDistrictCount(); ++i) {
        	districts.add(attr.getDistrict(i).getValue());
        }
        if (!districts.isEmpty()) {
            indexArrayObject.setJSONObject(obj, districts, "district");
        }
        
        //locations
        JSONArray locations = new JSONArray();
        for (int i = 0; i < attr.getLocationsCount(); ++i) {
        	locations.add(attr.getLocations(i).getValue());
        }
        if (!locations.isEmpty()) {
            indexArrayObject.setJSONObject(obj, locations, "locations");
        }
       
        //size
        JSONArray size = new JSONArray();
        for (int i = 0; i < attr.getSizeCount(); ++i) {
        	size.add(attr.getSize(i).getValue());
        }
        if (!size.isEmpty()) {
            indexArrayObject.setJSONObject(obj, size, "size");
        }
        
        //departures
        JSONArray departures = new JSONArray();
        for (int i = 0; i < attr.getDeparturesCount(); ++i) {
        	departures.add(attr.getDepartures(i).getValue());
        }
        if (!departures.isEmpty()) {
            indexArrayObject.setJSONObject(obj, departures, "departures");
        }
        
        //destination
        JSONArray destination = new JSONArray();
        for (int i = 0; i < attr.getDestinationCount(); ++i) {
        	destination.add(attr.getDestination(i).getValue());
        }
        if (!destination.isEmpty()) {
            indexArrayObject.setJSONObject(obj, destination, "destination");
        }
        
        //sights
        JSONArray sights = new JSONArray();
        for (int i = 0; i < attr.getSightsCount(); ++i) {
        	sights.add(attr.getSights(i).getValue());
        }
        if (!sights.isEmpty()) {
            indexArrayObject.setJSONObject(obj, sights, "sights");
        }
        
        //tour_type
        JSONArray tour_type = new JSONArray();
        for (int i = 0; i < attr.getTourTypeCount(); ++i) {
        	tour_type.add(attr.getTourType(i).getValue());
        }
        if (!tour_type.isEmpty()) {
            indexArrayObject.setJSONObject(obj, tour_type, "tour_type");
        }
        
        //tastes
        JSONArray tastes = new JSONArray();
        for (int i = 0; i < attr.getTastesCount(); ++i) {
        	tastes.add(attr.getTastes(i).getValue());
        }
        if (!tastes.isEmpty()) {
            indexArrayObject.setJSONObject(obj, tastes, "tastes");
        }
        
        //materials
        JSONArray materials = new JSONArray();
        for (int i = 0; i < attr.getMaterialsCount(); ++i) {
        	materials.add(attr.getMaterials(i).getValue());
        }
        if (!materials.isEmpty()) {
            indexArrayObject.setJSONObject(obj, materials, "materials");
        }
       
        //crowds
        JSONArray crowds = new JSONArray();
        for (int i = 0; i < attr.getCrowdsCount(); ++i) {
        	crowds.add(attr.getCrowds(i).getValue());
        }
        if (!crowds.isEmpty()) {
            indexArrayObject.setJSONObject(obj, crowds, "crowds");
        }
        
        //crafts
        JSONArray crafts = new JSONArray();
        for (int i = 0; i < attr.getCraftsCount(); ++i) {
        	crafts.add(attr.getCrafts(i).getValue());
        }
        if (!crafts.isEmpty()) {
            indexArrayObject.setJSONObject(obj, crafts, "crafts");
        }
        
        //diseases
        JSONArray diseases = new JSONArray();
        for (int i = 0; i < attr.getDiseasesCount(); ++i) {
        	diseases.add(attr.getDiseases(i).getValue());
        }
        if (!diseases.isEmpty()) {
            indexArrayObject.setJSONObject(obj, diseases, "diseases");
        }
        
        //visceras
        JSONArray visceras = new JSONArray();
        for (int i = 0; i < attr.getViscerasCount(); ++i) {
        	visceras.add(attr.getVisceras(i).getValue());
        }
        if (!visceras.isEmpty()) {
            indexArrayObject.setJSONObject(obj, visceras, "visceras");
        }
        
        //functions
        JSONArray functions = new JSONArray();
        for (int i = 0; i < attr.getFunctionsCount(); ++i) {
        	functions.add(attr.getFunctions(i).getValue());
        }
        if (!functions.isEmpty()) {
            indexArrayObject.setJSONObject(obj, functions, "functions");
        }
        
        //wares
        JSONArray wares = new JSONArray();
        for (int i = 0; i < attr.getWaresCount(); ++i) {
        	wares.add(attr.getWares(i).getValue());
        }
        if (!wares.isEmpty()) {
            indexArrayObject.setJSONObject(obj, wares, "wares");
        }
        
      //durations
        JSONArray durations = new JSONArray();
        for (int i = 0; i < attr.getDurationsCount(); ++i) {
        	durations.add(attr.getDurations(i).getValue());
        }
        if (!durations.isEmpty()) {
            indexArrayObject.setJSONObject(obj, durations, "durations");
        }
        
        //styles
        JSONArray styles = new JSONArray();
        for (int i = 0; i < attr.getStylesCount(); ++i) {
        	styles.add(attr.getStyles(i).getValue());
        }
        if (!styles.isEmpty()) {
            indexArrayObject.setJSONObject(obj, styles, "styles");
        }
        
        //styles
        JSONArray difficultys = new JSONArray();
        for (int i = 0; i < attr.getDifficultysCount(); ++i) {
        	difficultys.add(attr.getDifficultys(i).getValue());
        }
        if (!difficultys.isEmpty()) {
            indexArrayObject.setJSONObject(obj, difficultys, "difficultys");
        }
        
        //scenes
        JSONArray scenes = new JSONArray();
        for (int i = 0; i < attr.getScenesCount(); ++i) {
        	scenes.add(attr.getScenes(i).getValue());
        }
        if (!scenes.isEmpty()) {
            indexArrayObject.setJSONObject(obj, scenes, "scenes");
        }
        
        //food_wrap
        JSONArray food_wrap = new JSONArray();
        for (int i = 0; i < attr.getFoodWrapCount(); ++i) {
        	food_wrap.add(attr.getFoodWrap(i).getValue());
        }
        if (!food_wrap.isEmpty()) {
            indexArrayObject.setJSONObject(obj, food_wrap, "food_wrap");
        }
        
        //food_has_sugar
        JSONArray food_has_sugar = new JSONArray();
        for (int i = 0; i < attr.getFoodHasSugarCount(); ++i) {
        	food_has_sugar.add(attr.getFoodHasSugar(i).getValue());
        }
        if (!food_has_sugar.isEmpty()) {
            indexArrayObject.setJSONObject(obj, food_has_sugar, "food_has_sugar");
        }
        
        //artist
        JSONArray artist = new JSONArray();
        for (int i = 0; i < attr.getArtistCount(); ++i) {
        	artist.add(attr.getArtist(i).getValue());
        }
        if (!artist.isEmpty()) {
            indexArrayObject.setJSONObject(obj, artist, "artist");
        }
        
      //mb_model
        JSONArray mb_model = new JSONArray();
        for (int i = 0; i < attr.getMbModelCount(); ++i) {
        	mb_model.add(attr.getMbModel(i).getValue());
        }
        if (!mb_model.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_model, "mb_model");
        }
        
        //mb_date
        JSONArray mb_date = new JSONArray();
        for (int i = 0; i < attr.getMbDateCount(); ++i) {
			mb_date.add(attr.getMbDate(i).getValue());
        }
        if (!mb_date.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_date, "mb_date");
        }
        
        //mb_color
        JSONArray mb_color = new JSONArray();
        for (int i = 0; i < attr.getMbColorCount(); ++i) {
        	mb_color.add(attr.getMbColor(i).getValue());
        }
        if (!mb_color.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_color, "mb_color");
        }
        
        //mb_support
        JSONArray mb_support = new JSONArray();
        for (int i = 0; i < attr.getMbSupportCount(); ++i) {
        	mb_support.add(attr.getMbSupport(i).getValue());
        }
        if (!mb_support.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_support, "mb_support");
        }
        
        //mb_os
        JSONArray mb_os = new JSONArray();
        for (int i = 0; i < attr.getMbOsCount(); ++i) {
        	mb_os.add(attr.getMbOs(i).getValue());
        }
        if (!mb_os.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_os, "mb_os");
        }
        
        //mb_resolution
        JSONArray mb_resolution = new JSONArray();
        for (int i = 0; i < attr.getMbResolutionCount(); ++i) {
        	mb_resolution.add(attr.getMbResolution(i).getValue());
        }
        if (!mb_resolution.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_resolution, "mb_resolution");
        }
        
        //mb_ram
        JSONArray mb_ram = new JSONArray();
        for (int i = 0; i < attr.getMbRamCount(); ++i) {
        	mb_ram.add(attr.getMbRam(i).getValue());
        }
        if (!mb_ram.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_ram, "mb_ram");
        }
        
        //mb_rom
        JSONArray mb_rom = new JSONArray();
        for (int i = 0; i < attr.getMbRomCount(); ++i) {
        	mb_rom.add(attr.getMbRom(i).getValue());
        }
        if (!mb_rom.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_rom, "mb_rom");
        }
        
        //mb_camera
        JSONArray mb_camera = new JSONArray();
        for (int i = 0; i < attr.getMbCameraCount(); ++i) {
        	mb_camera.add(attr.getMbCamera(i).getValue());
        }
        if (!mb_camera.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_camera, "mb_camera");
        }
        
        //mb_screen
        JSONArray mb_screen = new JSONArray();
        for (int i = 0; i < attr.getMbScreenCount(); ++i) {
        	mb_screen.add(attr.getMbScreen(i).getValue());
        }
        if (!mb_screen.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_screen, "mb_screen");
        }
        
        //mb_pattern
        JSONArray mb_pattern = new JSONArray();
        for (int i = 0; i < attr.getMbPatternCount(); ++i) {
        	mb_pattern.add(attr.getMbPattern(i).getValue());
        }
        if (!mb_pattern.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mb_pattern, "mb_pattern");
        }
        
        //璇讳功琛屼笟
        //authors
        JSONArray authors = new JSONArray();
        for (int i = 0; i < attr.getAuthorsCount() ; ++i) {
        	authors.add(attr.getAuthors(i).getValue());
        }
        if (!authors.isEmpty()) {
            indexArrayObject.setJSONObject(obj, authors, "authors");
        }
        
        //bk_name
        JSONArray bk_name = new JSONArray();
        for (int i = 0; i < attr.getBkNameCount() ; ++i) {
        	bk_name.add(attr.getBkName(i).getValue());
        }
        if (!bk_name.isEmpty()) {
            indexArrayObject.setJSONObject(obj, bk_name, "bk_name");
        }
        
        //bk_translate
        JSONArray bk_translate = new JSONArray();
        for (int i = 0; i < attr.getBkTranslateCount() ; ++i) {
        	bk_translate.add(attr.getBkTranslate(i).getValue());
        }
        if (!bk_translate.isEmpty()) {
            indexArrayObject.setJSONObject(obj, bk_translate, "bk_translate");
        }
        
        //bk_length
        JSONArray bk_length = new JSONArray();
        for (int i = 0; i < attr.getBkLengthCount() ; ++i) {
        	bk_length.add(attr.getBkLength(i).getValue());
        }
        if (!bk_length.isEmpty()) {
            indexArrayObject.setJSONObject(obj, bk_length, "bk_length");
        }
        
        //bk_process
        JSONArray bk_process = new JSONArray();
        for (int i = 0; i < attr.getBkProcessCount() ; ++i) {
        	bk_process.add(attr.getBkProcess(i).getValue());
        }
        if (!bk_process.isEmpty()) {
            indexArrayObject.setJSONObject(obj, bk_process, "bk_process");
        }
        
        //bk_group
        JSONArray bk_group = new JSONArray();
        for (int i = 0; i < attr.getBkGroupCount() ; ++i) {
        	bk_group.add(attr.getBkGroup(i).getValue());
        }
        if (!bk_group.isEmpty()) {
            indexArrayObject.setJSONObject(obj, bk_group, "bk_group");
        }
        
        //褰辫琛屼笟
        //actors
        JSONArray actors = new JSONArray();
        for (int i = 0; i < attr.getActorsCount() ; ++i) {
        	actors.add(attr.getActors(i).getValue());
        }
        if (!actors.isEmpty()) {
            indexArrayObject.setJSONObject(obj, actors, "actors");
        }
        
        //directors
        JSONArray directors = new JSONArray();
        for (int i = 0; i < attr.getDirectorsCount() ; ++i) {
        	directors.add(attr.getDirectors(i).getValue());
        }
        if (!directors.isEmpty()) {
            indexArrayObject.setJSONObject(obj, directors, "directors");
        }
        
        //mv_name
        JSONArray mv_name = new JSONArray();
        for (int i = 0; i < attr.getMvNameCount() ; ++i) {
        	mv_name.add(attr.getMvName(i).getValue());
        }
        if (!mv_name.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mv_name, "mv_name");
        }
        
        //mv_area
        JSONArray mv_area = new JSONArray();
        for (int i = 0; i < attr.getMvAreaCount() ; ++i) {
        	mv_area.add(attr.getMvArea(i).getValue());
        }
        if (!mv_area.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mv_area, "mv_area");
        }
        
        //mv_year
        JSONArray mv_year = new JSONArray();
        for (int i = 0; i < attr.getMvYearCount() ; ++i) {
        	mv_year.add(attr.getMvYear(i).getValue());
        }
        if (!mv_year.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mv_year, "mv_year");
        }
        
        //鐢佃剳琛屼笟
        //cmp_screen
        JSONArray cmp_screen = new JSONArray();
        for (int i = 0; i < attr.getCmpScreenCount() ; ++i) {
        	cmp_screen.add(attr.getCmpScreen(i).getValue());
        }
        if (!cmp_screen.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_screen, "cmp_screen");
        }
        
        //cmp_color
        JSONArray cmp_color = new JSONArray();
        for (int i = 0; i < attr.getCmpColorCount() ; ++i) {
        	cmp_color.add(attr.getCmpColor(i).getValue());
        }
        if (!cmp_color.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_color, "cmp_color");
        }
        
        //cmp_cpu
        JSONArray cmp_cpu = new JSONArray();
        for (int i = 0; i < attr.getCmpCpuCount() ; ++i) {
        	cmp_cpu.add(attr.getCmpCpu(i).getValue());
        }
        if (!cmp_cpu.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_cpu, "cmp_cpu");
        }
        
        //cmp_video
        JSONArray cmp_video = new JSONArray();
        for (int i = 0; i < attr.getCmpVideoCount() ; ++i) {
        	cmp_video.add(attr.getCmpVideo(i).getValue());
        }
        if (!cmp_video.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_video, "cmp_video");
        }
        
        //cmp_resolution
        JSONArray cmp_resolution = new JSONArray();
        for (int i = 0; i < attr.getCmpResolutionCount() ; ++i) {
        	cmp_resolution.add(attr.getCmpResolution(i).getValue());
        }
        if (!cmp_resolution.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_resolution, "cmp_resolution");
        }
        
        //cmp_model
        JSONArray cmp_model = new JSONArray();
        for (int i = 0; i < attr.getCmpModelCount() ; ++i) {
        	cmp_model.add(attr.getCmpModel(i).getValue());
        }
        if (!cmp_model.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_model, "cmp_model");
        }
        
        //cmp_os
        JSONArray cmp_os = new JSONArray();
        for (int i = 0; i < attr.getCmpOsCount() ; ++i) {
        	cmp_os.add(attr.getCmpOs(i).getValue());
        }
        if (!cmp_os.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_os, "cmp_os");
        }
        
        //cmp_date
        JSONArray cmp_date = new JSONArray();
        for (int i = 0; i < attr.getCmpDateCount() ; ++i) {
			cmp_date.add(attr.getCmpDate(i).getValue());
        }
        if (!cmp_date.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_date, "cmp_date");
        }
        
        //cmp_memory
        JSONArray cmp_memory = new JSONArray();
        for (int i = 0; i < attr.getCmpMemoryCount() ; ++i) {
        	cmp_memory.add(attr.getCmpMemory(i).getValue());
        }
        if (!cmp_memory.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_memory, "cmp_memory");
        }

        //cmp_disk
        JSONArray cmp_disk = new JSONArray();
        for (int i = 0; i < attr.getCmpDiskCount() ; ++i) {
        	cmp_disk.add(attr.getCmpDisk(i).getValue());
        }
        if (!cmp_disk.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cmp_disk, "cmp_disk");
        }

        //姹借溅琛屼笟
        //qc_manufacturers
        JSONArray qc_manufacturers = new JSONArray();
        for (int i = 0; i < attr.getQcManufacturersCount() ; ++i) {
        	qc_manufacturers.add(attr.getQcManufacturers(i).getValue());
        }
        if (!qc_manufacturers.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_manufacturers, "qc_manufacturers");
        }

        //qc_model
        JSONArray qc_model = new JSONArray();
        for (int i = 0; i < attr.getQcModelCount() ; ++i) {
        	qc_model.add(attr.getQcModel(i).getValue());
        }
        if (!qc_model.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_model, "qc_model");
        }

        //qc_level
        JSONArray qc_level = new JSONArray();
        for (int i = 0; i < attr.getQcLevelCount() ; ++i) {
        	qc_level.add(attr.getQcLevel(i).getValue());
        }
        if (!qc_level.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_level, "qc_level");
        }

        //qc_ed_model
        JSONArray qc_ed_model = new JSONArray();
        for (int i = 0; i < attr.getQcEdModelCount() ; ++i) {
        	qc_ed_model.add(attr.getQcEdModel(i).getValue());
        }
        if (!qc_ed_model.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_ed_model, "qc_ed_model");
        }

        //qc_price
        JSONArray qc_price = new JSONArray();
        for (int i = 0; i < attr.getQcPriceCount() ; ++i) {
        	qc_price.add(attr.getQcPrice(i).getValue());
        }
        if (!qc_price.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_price, "qc_price");
        }

        //qc_displacement
        JSONArray qc_displacement = new JSONArray();
        for (int i = 0; i < attr.getQcDisplacementCount() ; ++i) {
        	qc_displacement.add(attr.getQcDisplacement(i).getValue());
        }
        if (!qc_displacement.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_displacement, "qc_displacement");
        }

        //qc_color
        JSONArray qc_color = new JSONArray();
        for (int i = 0; i < attr.getQcColorCount() ; ++i) {
        	qc_color.add(attr.getQcColor(i).getValue());
        }
        if (!qc_color.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_color, "qc_color");
        }

        //qc_drive
        JSONArray qc_drive = new JSONArray();
        for (int i = 0; i < attr.getQcDriveCount() ; ++i) {
        	qc_drive.add(attr.getQcDrive(i).getValue());
        }
        if (!qc_drive.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_drive, "qc_drive");
        }

        //qc_gearbox
        JSONArray qc_gearbox = new JSONArray();
        for (int i = 0; i < attr.getQcGearboxCount() ; ++i) {
        	qc_gearbox.add(attr.getQcGearbox(i).getValue());
        }
        if (!qc_gearbox.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_gearbox, "qc_gearbox");
        }

        //qc_seats_num
        JSONArray qc_seats_num = new JSONArray();
        for (int i = 0; i < attr.getQcSeatsNumCount() ; ++i) {
        	qc_seats_num.add(attr.getQcSeatsNum(i).getValue());
        }
        if (!qc_seats_num.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_seats_num, "qc_seats_num");
        }

        //qc_structure
        JSONArray qc_structure = new JSONArray();
        for (int i = 0; i < attr.getQcStructureCount() ; ++i) {
        	qc_structure.add(attr.getQcStructure(i).getValue());
        }
        if (!qc_structure.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_structure, "qc_structure");
        }

        //qc_fuel
        JSONArray qc_fuel = new JSONArray();
        for (int i = 0; i < attr.getQcFuelCount() ; ++i) {
        	qc_fuel.add(attr.getQcFuel(i).getValue());
        }
        if (!qc_fuel.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_fuel, "qc_fuel");
        }

        //qc_intake_form
        JSONArray qc_intake_form = new JSONArray();
        for (int i = 0; i < attr.getQcIntakeFormCount() ; ++i) {
        	qc_intake_form.add(attr.getQcIntakeForm(i).getValue());
        }
        if (!qc_intake_form.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_intake_form, "qc_intake_form");
        }

        //qc_year
        JSONArray qc_year = new JSONArray();
        for (int i = 0; i < attr.getQcYearCount() ; ++i) {
        	qc_year.add(attr.getQcYear(i).getValue());
        }
        if (!qc_year.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_year, "qc_year");
        }

        //qc_cylinder
        JSONArray qc_cylinder = new JSONArray();
        for (int i = 0; i < attr.getQcCylinderCount() ; ++i) {
        	qc_cylinder.add(attr.getQcCylinder(i).getValue());
        }
        if (!qc_cylinder.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_cylinder, "qc_cylinder");
        }

        //qc_doors_num
        JSONArray qc_doors_num = new JSONArray();
        for (int i = 0; i < attr.getQcDoorsNumCount() ; ++i) {
        	qc_doors_num.add(attr.getQcDoorsNum(i).getValue());
        }
        if (!qc_doors_num.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_doors_num, "qc_doors_num");
        }

        //qc_used_car_price
        JSONArray qc_used_car_price = new JSONArray();
        for (int i = 0; i < attr.getQcUsedCarPriceCount() ; ++i) {
        	qc_used_car_price.add(attr.getQcUsedCarPrice(i).getValue());
        }
        if (!qc_used_car_price.isEmpty()) {
            indexArrayObject.setJSONObject(obj, qc_used_car_price, "qc_used_car_price");
        }

        //閰掑簵
        //ht_star_level
        JSONArray ht_star_level = new JSONArray();
        for (int i = 0; i < attr.getHtStarLevelCount() ; ++i) {
        	ht_star_level.add(attr.getHtStarLevel(i).getValue());
        }
        if (!ht_star_level.isEmpty()) {
            indexArrayObject.setJSONObject(obj, ht_star_level, "ht_star_level");
        }

        //鐏溅绁ㄣ�侀鏈虹エ
        //tk_class
        JSONArray tk_class = new JSONArray();
        for (int i = 0; i < attr.getTkClassCount() ; ++i) {
        	tk_class.add(attr.getTkClass(i).getValue());
        }
        if (!tk_class.isEmpty()) {
            indexArrayObject.setJSONObject(obj, tk_class, "tk_class");
        }

        //鐢靛晢-鏈嶈閰嶉グ
        //clo_sleeve
        JSONArray clo_sleeve = new JSONArray();
        for (int i = 0; i < attr.getCloSleeveCount() ; ++i) {
        	clo_sleeve.add(attr.getCloSleeve(i).getValue());
        }
        if (!clo_sleeve.isEmpty()) {
            indexArrayObject.setJSONObject(obj, clo_sleeve, "clo_sleeve");
        }

        //clo_collar
        JSONArray clo_collar = new JSONArray();
        for (int i = 0; i < attr.getCloCollarCount() ; ++i) {
        	clo_collar.add(attr.getCloCollar(i).getValue());
        }
        if (!clo_collar.isEmpty()) {
            indexArrayObject.setJSONObject(obj, clo_collar, "clo_collar");
        }

        //鐢靛晢-鍖荤枟淇濆仴
        //mdc_country
        JSONArray mdc_country = new JSONArray();
        for (int i = 0; i < attr.getMdcCountryCount() ; ++i) {
        	mdc_country.add(attr.getMdcCountry(i).getValue());
        }
        if (!mdc_country.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mdc_country, "mdc_country");
        }

        //mdc_body
        JSONArray mdc_body = new JSONArray();
        for (int i = 0; i < attr.getMdcBodyCount() ; ++i) {
        	mdc_body.add(attr.getMdcBody(i).getValue());
        }
        if (!mdc_body.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mdc_body, "mdc_body");
        }

        //mdc_symptom
        JSONArray mdc_symptom = new JSONArray();
        for (int i = 0; i < attr.getMdcSymptomCount() ; ++i) {
        	mdc_symptom.add(attr.getMdcSymptom(i).getValue());
        }
        if (!mdc_symptom.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mdc_symptom, "mdc_symptom");
        }

        //mdc_disease
        JSONArray mdc_disease = new JSONArray();
        for (int i = 0; i < attr.getMdcDiseaseCount() ; ++i) {
        	mdc_disease.add(attr.getMdcDisease(i).getValue());
        }
        if (!mdc_disease.isEmpty()) {
            indexArrayObject.setJSONObject(obj, mdc_disease, "mdc_disease");
        }

        //鐢靛晢-涓姢鍖栧
        //cos_situation
        JSONArray cos_situation = new JSONArray();
        for (int i = 0; i < attr.getCosSituationCount() ; ++i) {
        	cos_situation.add(attr.getCosSituation(i).getValue());
        }
        if (!cos_situation.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cos_situation, "cos_situation");
        }

        //cos_smell
        JSONArray cos_smell = new JSONArray();
        for (int i = 0; i < attr.getCosSmellCount() ; ++i) {
        	cos_smell.add(attr.getCosSmell(i).getValue());
        }
        if (!cos_smell.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cos_smell, "cos_smell");
        }

        //cos_skin
        JSONArray cos_skin = new JSONArray();
        for (int i = 0; i < attr.getCosSkinCount() ; ++i) {
        	cos_skin.add(attr.getCosSkin(i).getValue());
        }
        if (!cos_skin.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cos_skin, "cos_skin");
        }

        //cos_sunblock
        JSONArray cos_sunblock = new JSONArray();
        for (int i = 0; i < attr.getCosSunblockCount() ; ++i) {
        	cos_sunblock.add(attr.getCosSunblock(i).getValue());
        }
        if (!cos_sunblock.isEmpty()) {
            indexArrayObject.setJSONObject(obj, cos_sunblock, "cos_sunblock");
        }

        //濯掍綋-璐㈢粡
        //fina_code
        JSONArray fina_code = new JSONArray();
        for (int i = 0; i < attr.getFinaCodeCount() ; ++i) {
        	fina_code.add(attr.getFinaCode(i).getValue());
        }
        if (!fina_code.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_code, "fina_code");
        }

        //fina_name
        JSONArray fina_name = new JSONArray();
        for (int i = 0; i < attr.getFinaNameCount() ; ++i) {
        	fina_name.add(attr.getFinaName(i).getValue());
        }
        if (!fina_name.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_name, "fina_name");
        }

        //fina_style
        JSONArray fina_style = new JSONArray();
        for (int i = 0; i < attr.getFinaStyleCount() ; ++i) {
        	fina_style.add(attr.getFinaStyle(i).getValue());
        }
        if (!fina_style.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_style, "fina_style");
        }

        //fina_method
        JSONArray fina_method = new JSONArray();
        for (int i = 0; i < attr.getFinaMethodCount() ; ++i) {
        	fina_method.add(attr.getFinaMethod(i).getValue());
        }
        if (!fina_method.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_method, "fina_method");
        }

        //fina_range
        JSONArray fina_range = new JSONArray();
        for (int i = 0; i < attr.getFinaRangeCount() ; ++i) {
        	fina_range.add(attr.getFinaRange(i).getValue());
        }
        if (!fina_range.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_range, "fina_range");
        }

        //fina_product
        JSONArray fina_product = new JSONArray();
        for (int i = 0; i < attr.getFinaProductCount() ; ++i) {
        	fina_product.add(attr.getFinaProduct(i).getValue());
        }
        if (!fina_product.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_product, "fina_product");
        }

        //fina_type
        JSONArray fina_type = new JSONArray();
        for (int i = 0; i < attr. getFinaTypeCount(); ++i) {
        	fina_type.add(attr.getFinaType(i).getValue());
        }
        if (!fina_type.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_type, "fina_type");
        }

        //fina_currency
        JSONArray fina_currency = new JSONArray();
        for (int i = 0; i < attr.getFinaCurrencyCount() ; ++i) {
        	fina_currency.add(attr.getFinaCurrency(i).getValue());
        }
        if (!fina_currency.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_currency, "fina_currency");
        }

        //fina_market
        JSONArray fina_market = new JSONArray();
        for (int i = 0; i < attr.getFinaMarketCount() ; ++i) {
        	fina_market.add(attr.getFinaMarket(i).getValue());
        }
        if (!fina_market.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_market, "fina_market");
        }

        //fina_newPrice
        JSONArray fina_newPrice = new JSONArray();
        for (int i = 0; i < attr.getFinaNewPriceCount() ; ++i) {
        	fina_newPrice.add(attr.getFinaNewPrice(i).getValue());
        }
        if (!fina_newPrice.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_newPrice, "fina_newPrice");
        }

        //fina_company
        JSONArray fina_company = new JSONArray();
        for (int i = 0; i < attr.getFinaCompanyCount() ; ++i) {
        	fina_company.add(attr.getFinaCompany(i).getValue());
        }
        if (!fina_company.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_company, "fina_company");
        }

        //fina_region
        JSONArray fina_region = new JSONArray();
        for (int i = 0; i < attr.getFinaRegionCount() ; ++i) {
        	fina_region.add(attr.getFinaRegion(i).getValue());
        }
        if (!fina_region.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_region, "fina_region");
        }

        //fina_area
        JSONArray fina_area = new JSONArray();
        for (int i = 0; i < attr.getFinaAreaCount() ; ++i) {
        	fina_area.add(attr.getFinaArea(i).getValue());
        }
        if (!fina_area.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_area, "fina_area");
        }

        //fina_deadline
        JSONArray fina_deadline = new JSONArray();
        for (int i = 0; i < attr.getFinaDeadlineCount() ; ++i) {
        	fina_deadline.add(attr.getFinaDeadline(i).getValue());
        }
        if (!fina_deadline.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_deadline, "fina_deadline");
        }

        //fina_preEarnings
        JSONArray fina_preEarnings = new JSONArray();
        for (int i = 0; i < attr.getFinaPreEarningsCount() ; ++i) {
        	fina_preEarnings.add(attr.getFinaPreEarnings(i).getValue());
        }
        if (!fina_preEarnings.isEmpty()) {
            indexArrayObject.setJSONObject(obj, fina_preEarnings, "fina_preEarnings");
        }

        
    }


    public void fillUpDimension(Object object, JSONObject obj) {


    }

    public void fillUpDimension(Object object, JSONArray jsons) {

        if(!(object instanceof UserProfile2.CidInfo)){
            return;
        }
        UserProfile2.CidInfo cidInfo = (UserProfile2.CidInfo) object;
        String cid = cidInfo.getCid();

        for(int j = 0; j < cidInfo.getIndusCount(); ++j){
            UserProfile2.IndustryInfo industryInfo = cidInfo.getIndus(j);

            for(int index_pay_info = 0; index_pay_info < industryInfo.getPayCateCount(); ++index_pay_info){
                UserProfile2.PayInfo payInfo = industryInfo.getPayInfo(index_pay_info);
                Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

                Map<String, String> timeStampMap = new HashMap<String, String>();
                timeStampMap.put("update_time",String.valueOf(payInfo.getTimestamp()));

                Map<String, String> stringMap = new HashMap<String, String>();

                Map<String, String> integerMap = new HashMap<String, String>();
                //stringMap.put("method", "Pay");

                //Pay琛屼负鏄犲皠涓�1
                integerMap.put("method", "1");

                stringMap.put("cid", cid);
                if (payInfo.getCateNameCount() > 0) {
                    stringMap.put("first_category", payInfo.getCateName(0));
                }
                if (payInfo.getCateNameCount() > 1) {
                    stringMap.put("second_category", payInfo.getCateName(1));
                }
                if (payInfo.getCateNameCount() > 2) {
                    stringMap.put("third_category", payInfo.getCateName(2));
                }
                if(timeStampMap.size() > 0){
                    map.put("TimeStamp", timeStampMap);
                }
                if(stringMap.size() > 0){
                    map.put("String", stringMap);
                }
                if(integerMap.size() > 0){
                    map.put("Integer", integerMap);
                }

                if(map.size() > 0){
                    JSONObject json = new JSONObject();
                    indexNestedObject.fillNestedObject(json,map);
                    indexArrayObject.addArrayJSONObject(jsons, json);
                }
            }



            for (int index_add_cart = 0; index_add_cart < industryInfo.getAddcartInfoCount(); ++index_add_cart) {
                UserProfile2.PayInfo payInfo = industryInfo.getAddcartInfo(index_add_cart);
                Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

                Map<String, String> timeStampMap = new HashMap<String, String>();
                timeStampMap.put("update_time",String.valueOf(payInfo.getTimestamp()));

                Map<String, String> stringMap = new HashMap<String, String>();
                Map<String, String> integerMap = new HashMap<String, String>();
                //stringMap.put("method", "AddCart");

                //灏咥ddCart琛屼负鏄犲皠涓�2
                integerMap.put("method", "2");
                stringMap.put("cid", cid);
                if (payInfo.getCateNameCount() > 0) {
                    stringMap.put("first_category", payInfo.getCateName(0));
                }
                if (payInfo.getCateNameCount() > 1) {
                    stringMap.put("second_category", payInfo.getCateName(1));
                }
                if (payInfo.getCateNameCount() > 2) {
                    stringMap.put("third_category", payInfo.getCateName(2));
                }

                if(timeStampMap.size() > 0){
                    map.put("TimeStamp", timeStampMap);
                }
                if(stringMap.size() > 0){
                    map.put("String", stringMap);
                }

                if(integerMap.size() > 0){
                    map.put("Integer", integerMap);
                }

                if(map.size() > 0){
                    JSONObject json = new JSONObject();
                    indexNestedObject.fillNestedObject(json,map);
                    indexArrayObject.addArrayJSONObject(jsons, json);
                }
            }


            if(industryInfo.getFirstCateCount() == 0 || industryInfo.getMediaCateCount() ==0){

                Map<String, String> timeStampMap = new HashMap<String, String>();
                if(industryInfo.hasTimestamp()){
                    timeStampMap.put("update_time",String.valueOf(industryInfo.getTimestamp()));
                }

                Map<String, String> stringMap = new HashMap<String, String>();
                stringMap.put("cid", cid);

                Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                if(timeStampMap.size() > 0){
                    map.put("TimeStamp", timeStampMap);
                }
                if(stringMap.size() > 0){
                    map.put("String", stringMap);
                }
                if(map.size() > 0){
                    JSONObject json = new JSONObject();
                    indexNestedObject.fillNestedObject(json,map);
                    indexArrayObject.addArrayJSONObject(jsons, json);
                }
            }

            //闀挎湡璐墿鍋忓ソ(media)
            for (int index_first_category = 0; index_first_category < industryInfo.getMediaCateCount(); ++index_first_category){
                UserProfile2.FirstCategory firstCategory = industryInfo.getMediaCate(index_first_category);
                String firstCateName = firstCategory.getName();
                Map<String, String> stringMap = new HashMap<String, String>();
                stringMap.put("first_category", firstCateName);
                stringMap.put("cid", cid);
                Map<String, String> IntegerMap = new HashMap<String, String>();
                Map<String, String> timeStampMap = new HashMap<String, String>();
                IntegerMap.put("type", "2");

                Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                map.put("String", stringMap);
                map.put("Integer",IntegerMap);
                map.put("TimeStamp", timeStampMap);

                if (firstCategory.hasAttrs() || firstCategory.getSecondCateCount() == 0) {
                    JSONObject first_obj = new JSONObject();
                    timeStampMap.put("update_time",String.valueOf(firstCategory.getTimestamp()));
                    parseBfdCategory(first_obj, firstCategory.getAttrs());
                    indexFirstNestedObject.fillNestedObject(first_obj,map);
                    indexArrayObject.addArrayJSONObject(jsons,first_obj);
                }


                for (int index_second_category = 0; index_second_category < firstCategory.getSecondCateCount(); ++index_second_category) {
                    UserProfile2.SecondCategory secondCategory = firstCategory.getSecondCate(index_second_category);
                    stringMap.put("second_category", secondCategory.getName());
                    if (secondCategory.hasAttrs() || secondCategory.getThirdCateCount() == 0) {
                        JSONObject second_obj = new JSONObject();
                        timeStampMap.put("update_time", String.valueOf(secondCategory.getTimestamp()));
                        parseBfdCategory(second_obj, secondCategory.getAttrs());
                        indexSecondNestedObject.fillNestedObject(second_obj, map);
                        indexArrayObject.addArrayJSONObject(jsons, second_obj);
                    }
                    for (int index_third_category = 0; index_third_category < secondCategory.getThirdCateCount(); ++index_third_category) {

                        UserProfile2.ThirdCategory thirdCategory = secondCategory.getThirdCate(index_third_category);
                        stringMap.put("third_category", thirdCategory.getName());
                        if (thirdCategory.hasAttrs() || thirdCategory.getFourthCateCount() == 0) {
                            JSONObject third_obj = new JSONObject();
                            timeStampMap.put("update_time", String.valueOf(thirdCategory.getTimestamp()));
                            parseBfdCategory(third_obj, thirdCategory.getAttrs());
                            indexThirdNestedObject.fillNestedObject(third_obj, map);
                            indexArrayObject.addArrayJSONObject(jsons, third_obj);
                        }
                        for (int index_fourth_category = 0; index_fourth_category < thirdCategory.getFourthCateCount(); ++index_fourth_category) {
                            UserProfile2.FourthCategory fourthCategory = thirdCategory.getFourthCate(index_fourth_category);
                            stringMap.put("fourth_category", fourthCategory.getName());
                            if (fourthCategory.hasAttrs() || fourthCategory.getFiveCateCount() == 0) {
                                JSONObject fourth_obj = new JSONObject();
                                timeStampMap.put("update_time", String.valueOf(fourthCategory.getTimestamp()));
                                parseBfdCategory(fourth_obj, fourthCategory.getAttrs());
                                indexFourthNestedObject.fillNestedObject(fourth_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, fourth_obj);
                            }
                            for (int index_five_category = 0; index_five_category < fourthCategory.getFiveCateCount(); ++index_five_category) {
                                UserProfile2.FiveCategory fiveCategory = fourthCategory.getFiveCate(index_five_category);
                                stringMap.put("fifth_category", fiveCategory.getName());
                                JSONObject fifth_obj = new JSONObject();
                                timeStampMap.put("update_time", String.valueOf(fiveCategory.getTimestamp()));
                                if (fiveCategory.hasAttrs()) {
                                    parseBfdCategory(fifth_obj, fiveCategory.getAttrs());
                                }
                                indexFifthNestedObject.fillNestedObject(fifth_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, fifth_obj);
                            }
                        }
                    }
                }
            }

            //闀挎湡璐墿鍋忓ソ-鐢靛晢(ec)
            for (int index_first_category = 0; index_first_category < industryInfo.getFirstCateCount(); ++index_first_category){
                UserProfile2.FirstCategory firstCategory = industryInfo.getFirstCate(index_first_category);
                String firstCateName = firstCategory.getName();
                Map<String, String> stringMap = new HashMap<String, String>();
                stringMap.put("first_category", firstCateName);
                stringMap.put("cid", cid);
                Map<String, String> timeStampMap = new HashMap<String, String>();
                Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                Map<String, String> IntegerMap = new HashMap<String, String>();
                IntegerMap.put("type", "1");
                map.put("String", stringMap);
                map.put("TimeStamp", timeStampMap);
                map.put("Integer",IntegerMap);

                if (firstCategory.hasAttrs() || firstCategory.getSecondCateCount() == 0) {
                    JSONObject first_obj = new JSONObject();
                    timeStampMap.put("update_time",String.valueOf(firstCategory.getTimestamp()));
                    parseBfdCategory(first_obj, firstCategory.getAttrs());
                    indexFirstNestedObject.fillNestedObject(first_obj,map);
                    indexArrayObject.addArrayJSONObject(jsons,first_obj);
                }
                for (int index_second_category = 0; index_second_category < firstCategory.getSecondCateCount(); ++index_second_category) {
                    UserProfile2.SecondCategory secondCategory = firstCategory.getSecondCate(index_second_category);
                    stringMap.put("second_category", secondCategory.getName());
                    if (secondCategory.hasAttrs() || secondCategory.getThirdCateCount() == 0) {
                        JSONObject second_obj = new JSONObject();
                        //update_time
                        timeStampMap.put("update_time", String.valueOf(secondCategory.getTimestamp()));
                        parseBfdCategory(second_obj, secondCategory.getAttrs());
                        indexSecondNestedObject.fillNestedObject(second_obj, map);
                        indexArrayObject.addArrayJSONObject(jsons, second_obj);
                    }
                    for (int index_third_category = 0; index_third_category < secondCategory.getThirdCateCount(); ++index_third_category) {

                        UserProfile2.ThirdCategory thirdCategory = secondCategory.getThirdCate(index_third_category);
                        stringMap.put("third_category", thirdCategory.getName());
                        if (thirdCategory.hasAttrs() || thirdCategory.getFourthCateCount() == 0) {
                            JSONObject third_obj = new JSONObject();
                            //濉啓涓夌骇鍝佺被鐨勬洿鏂版椂闂�
                            timeStampMap.put("update_time", String.valueOf(thirdCategory.getTimestamp()));
                            parseBfdCategory(third_obj, thirdCategory.getAttrs());
                            indexThirdNestedObject.fillNestedObject(third_obj, map);
                            indexArrayObject.addArrayJSONObject(jsons, third_obj);
                        }
                        for (int index_fourth_category = 0; index_fourth_category < thirdCategory.getFourthCateCount(); ++index_fourth_category) {
                            UserProfile2.FourthCategory fourthCategory = thirdCategory.getFourthCate(index_fourth_category);
                            stringMap.put("fourth_category", fourthCategory.getName());
                            if (fourthCategory.hasAttrs() || fourthCategory.getFiveCateCount() == 0) {
                                JSONObject fourth_obj = new JSONObject();
                                //濉啓鍥涚骇鍝佺被鐨勬洿鏂版椂闂�
                                timeStampMap.put("update_time", String.valueOf(fourthCategory.getTimestamp()));
                                parseBfdCategory(fourth_obj, fourthCategory.getAttrs());
                                indexFourthNestedObject.fillNestedObject(fourth_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, fourth_obj);
                            }
                            for (int index_five_category = 0; index_five_category < fourthCategory.getFiveCateCount(); ++index_five_category) {
                                UserProfile2.FiveCategory fiveCategory = fourthCategory.getFiveCate(index_five_category);
                                stringMap.put("fourth_category", fiveCategory.getName());
                                JSONObject fifth_obj = new JSONObject();
                                //濉啓浜旂骇鍝佺被鐨勬洿鏂版椂闂�
                                timeStampMap.put("update_time", String.valueOf(fiveCategory.getTimestamp()));
                                if (fiveCategory.hasAttrs()) {
                                    parseBfdCategory(fifth_obj, fiveCategory.getAttrs());
                                }
                                indexFourthNestedObject.fillNestedObject(fifth_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, fifth_obj);
                            }
                        }
                    }
                }
            }
            
            //褰撲笅闇�姹�
            if(industryInfo.getFirstCateCount()>0){
//            	List<FirstCategory> firstCategories_1 = industryInfo.getFirstCateList();
//            	List<FirstCategory> firstCategories = new ArrayList<FirstCategory>();
//            	for (int ii=0; ii<firstCategories_1.size(); ii++) {
//            		if (firstCategories_1.get(ii).hasWeight()) {
//            			cateweightmap.put(ii, firstCategories_1.get(ii).getWeight());
//            		}
//            	}
//            	System.out.println("first before sort");
//            	for (Map.Entry<Integer, Integer> iter : entriesSortedByValues(cateweightmap)) {
//            		firstCategories.add(firstCategories_1.get(iter.getKey()));
//        		}
//            	System.out.println("first after sort");
//            	CategoryCompare categoryCompare = new CategoryCompare();
//                System.out.println("before first sort size:"+firstCategories.size());
//                firstCategories.sort(categoryCompare);
//                System.out.println("after first sort size:"+firstCategories.size());
//            	if (firstCategories.size()>0) {
//            		sort(firstCategories);
//            	}
            	List<FirstCategory> firstCategories_1 = industryInfo.getFirstCateList();
            	FirstCategory[] firstCategories = new FirstCategory[firstCategories_1.size()];
            	for (int i=0; i<firstCategories_1.size(); i++) {
            		firstCategories[i]=firstCategories_1.get(i);
            	}
            	System.out.println("firstCategories.length "+firstCategories.length);
            	System.out.println("first before sort");
            	arraySort(firstCategories);
            	System.out.println("first before sort");
                if (firstCategories.length>0) {
                	//褰撲笅闇�姹傜壒寰�,閫夊彇瑙勫垯锛氶暱鏈熻喘鐗╁亸濂界殑top1
        			for (int index_first_category = 0; index_first_category < 1; ++index_first_category){
                        UserProfile2.FirstCategory firstCategory = firstCategories[index_first_category];
                        String firstCateName = firstCategory.getName();
                        Map<String, String> stringMap = new HashMap<String, String>();
                        stringMap.put("first_category", firstCateName);
                        stringMap.put("cid", cid);
                        Map<String, String> timeStampMap = new HashMap<String, String>();
                        Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                        Map<String, String> IntegerMap = new HashMap<String, String>();
                        IntegerMap.put("type", "4");
                        map.put("String", stringMap);
                        map.put("TimeStamp", timeStampMap);
                        map.put("Integer",IntegerMap);

                        if (firstCategory.hasAttrs() || firstCategory.getSecondCateCount() == 0) {
                            JSONObject first_obj = new JSONObject();
                            timeStampMap.put("update_time",String.valueOf(firstCategory.getTimestamp()));
                            parseBfdCategory(first_obj, firstCategory.getAttrs());
                            indexFirstNestedObject.fillNestedObject(first_obj,map);
                            indexArrayObject.addArrayJSONObject(jsons,first_obj);
                        }
                        for (int index_second_category = 0; index_second_category < firstCategory.getSecondCateCount(); ++index_second_category) {
                            UserProfile2.SecondCategory secondCategory = firstCategory.getSecondCate(index_second_category);
                            stringMap.put("second_category", secondCategory.getName());
                            if (secondCategory.hasAttrs() || secondCategory.getThirdCateCount() == 0) {
                                JSONObject second_obj = new JSONObject();
                                //update_time
                                timeStampMap.put("update_time", String.valueOf(secondCategory.getTimestamp()));
                                parseBfdCategory(second_obj, secondCategory.getAttrs());
                                indexSecondNestedObject.fillNestedObject(second_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, second_obj);
                            }
                            for (int index_third_category = 0; index_third_category < secondCategory.getThirdCateCount(); ++index_third_category) {

                                UserProfile2.ThirdCategory thirdCategory = secondCategory.getThirdCate(index_third_category);
                                stringMap.put("third_category", thirdCategory.getName());
                                if (thirdCategory.hasAttrs() || thirdCategory.getFourthCateCount() == 0) {
                                    JSONObject third_obj = new JSONObject();
                                    //濉啓涓夌骇鍝佺被鐨勬洿鏂版椂闂�
                                    timeStampMap.put("update_time", String.valueOf(thirdCategory.getTimestamp()));
                                    parseBfdCategory(third_obj, thirdCategory.getAttrs());
                                    indexThirdNestedObject.fillNestedObject(third_obj, map);
                                    indexArrayObject.addArrayJSONObject(jsons, third_obj);
                                }
                                for (int index_fourth_category = 0; index_fourth_category < thirdCategory.getFourthCateCount(); ++index_fourth_category) {
                                    UserProfile2.FourthCategory fourthCategory = thirdCategory.getFourthCate(index_fourth_category);
                                    stringMap.put("fourth_category", fourthCategory.getName());
                                    if (fourthCategory.hasAttrs() || fourthCategory.getFiveCateCount() == 0) {
                                        JSONObject fourth_obj = new JSONObject();
                                        //濉啓鍥涚骇鍝佺被鐨勬洿鏂版椂闂�
                                        timeStampMap.put("update_time", String.valueOf(fourthCategory.getTimestamp()));
                                        parseBfdCategory(fourth_obj, fourthCategory.getAttrs());
                                        indexFourthNestedObject.fillNestedObject(fourth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fourth_obj);
                                    }
                                    for (int index_five_category = 0; index_five_category < fourthCategory.getFiveCateCount(); ++index_five_category) {
                                        UserProfile2.FiveCategory fiveCategory = fourthCategory.getFiveCate(index_five_category);
                                        stringMap.put("fourth_category", fiveCategory.getName());
                                        JSONObject fifth_obj = new JSONObject();
                                        //濉啓浜旂骇鍝佺被鐨勬洿鏂版椂闂�
                                        timeStampMap.put("update_time", String.valueOf(fiveCategory.getTimestamp()));
                                        if (fiveCategory.hasAttrs()) {
                                            parseBfdCategory(fifth_obj, fiveCategory.getAttrs());
                                        }
                                        indexFourthNestedObject.fillNestedObject(fifth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fifth_obj);
                                    }
                                }
                            }
                        }
                    }
        			//鐭湡璐墿鍋忓ソ,閫夊彇瑙勫垯锛氶暱鏈熻喘鐗╁亸濂界殑top2
        			int num = 0;
        			if (firstCategories.length==1) {
        				num = 1;
        			} else {
        				num = 2;
        			}
        			for (int index_first_category = 0; index_first_category < num; ++index_first_category){
                        UserProfile2.FirstCategory firstCategory = firstCategories[index_first_category];
                        String firstCateName = firstCategory.getName();
                        Map<String, String> stringMap = new HashMap<String, String>();
                        stringMap.put("first_category", firstCateName);
                        stringMap.put("cid", cid);
                        Map<String, String> timeStampMap = new HashMap<String, String>();
                        Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                        Map<String, String> IntegerMap = new HashMap<String, String>();
                        IntegerMap.put("type", "6");
                        map.put("String", stringMap);
                        map.put("TimeStamp", timeStampMap);
                        map.put("Integer",IntegerMap);

                        if (firstCategory.hasAttrs() || firstCategory.getSecondCateCount() == 0) {
                            JSONObject first_obj = new JSONObject();
                            timeStampMap.put("update_time",String.valueOf(firstCategory.getTimestamp()));
                            parseBfdCategory(first_obj, firstCategory.getAttrs());
                            indexFirstNestedObject.fillNestedObject(first_obj,map);
                            indexArrayObject.addArrayJSONObject(jsons,first_obj);
                        }
                        for (int index_second_category = 0; index_second_category < firstCategory.getSecondCateCount(); ++index_second_category) {
                            UserProfile2.SecondCategory secondCategory = firstCategory.getSecondCate(index_second_category);
                            stringMap.put("second_category", secondCategory.getName());
                            if (secondCategory.hasAttrs() || secondCategory.getThirdCateCount() == 0) {
                                JSONObject second_obj = new JSONObject();
                                //update_time
                                timeStampMap.put("update_time", String.valueOf(secondCategory.getTimestamp()));
                                parseBfdCategory(second_obj, secondCategory.getAttrs());
                                indexSecondNestedObject.fillNestedObject(second_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, second_obj);
                            }
                            for (int index_third_category = 0; index_third_category < secondCategory.getThirdCateCount(); ++index_third_category) {

                                UserProfile2.ThirdCategory thirdCategory = secondCategory.getThirdCate(index_third_category);
                                stringMap.put("third_category", thirdCategory.getName());
                                if (thirdCategory.hasAttrs() || thirdCategory.getFourthCateCount() == 0) {
                                    JSONObject third_obj = new JSONObject();
                                    //濉啓涓夌骇鍝佺被鐨勬洿鏂版椂闂�
                                    timeStampMap.put("update_time", String.valueOf(thirdCategory.getTimestamp()));
                                    parseBfdCategory(third_obj, thirdCategory.getAttrs());
                                    indexThirdNestedObject.fillNestedObject(third_obj, map);
                                    indexArrayObject.addArrayJSONObject(jsons, third_obj);
                                }
                                for (int index_fourth_category = 0; index_fourth_category < thirdCategory.getFourthCateCount(); ++index_fourth_category) {
                                    UserProfile2.FourthCategory fourthCategory = thirdCategory.getFourthCate(index_fourth_category);
                                    stringMap.put("fourth_category", fourthCategory.getName());
                                    if (fourthCategory.hasAttrs() || fourthCategory.getFiveCateCount() == 0) {
                                        JSONObject fourth_obj = new JSONObject();
                                        //濉啓鍥涚骇鍝佺被鐨勬洿鏂版椂闂�
                                        timeStampMap.put("update_time", String.valueOf(fourthCategory.getTimestamp()));
                                        parseBfdCategory(fourth_obj, fourthCategory.getAttrs());
                                        indexFourthNestedObject.fillNestedObject(fourth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fourth_obj);
                                    }
                                    for (int index_five_category = 0; index_five_category < fourthCategory.getFiveCateCount(); ++index_five_category) {
                                        UserProfile2.FiveCategory fiveCategory = fourthCategory.getFiveCate(index_five_category);
                                        stringMap.put("fourth_category", fiveCategory.getName());
                                        JSONObject fifth_obj = new JSONObject();
                                        //濉啓浜旂骇鍝佺被鐨勬洿鏂版椂闂�
                                        timeStampMap.put("update_time", String.valueOf(fiveCategory.getTimestamp()));
                                        if (fiveCategory.hasAttrs()) {
                                            parseBfdCategory(fifth_obj, fiveCategory.getAttrs());
                                        }
                                        indexFourthNestedObject.fillNestedObject(fifth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fifth_obj);
                                    }
                                }
                            }
                        }
                    }
        			//娼滃湪闇�姹傜壒寰�,閫夊彇瑙勫垯锛氶暱鏈熻喘鐗╁亸濂界殑top5
        			if (firstCategories.length<5) {
        				num = firstCategories.length;
        			} else {
        				num = 5;
        			}
        			for (int index_first_category = 0; index_first_category < num; ++index_first_category){
                        UserProfile2.FirstCategory firstCategory = firstCategories[index_first_category];
                        String firstCateName = firstCategory.getName();
                        Map<String, String> stringMap = new HashMap<String, String>();
                        stringMap.put("first_category", firstCateName);
                        stringMap.put("cid", cid);
                        Map<String, String> timeStampMap = new HashMap<String, String>();
                        Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                        Map<String, String> IntegerMap = new HashMap<String, String>();
                        IntegerMap.put("type", "3");
                        map.put("String", stringMap);
                        map.put("TimeStamp", timeStampMap);
                        map.put("Integer",IntegerMap);

                        if (firstCategory.hasAttrs() || firstCategory.getSecondCateCount() == 0) {
                            JSONObject first_obj = new JSONObject();
                            timeStampMap.put("update_time",String.valueOf(firstCategory.getTimestamp()));
                            parseBfdCategory(first_obj, firstCategory.getAttrs());
                            indexFirstNestedObject.fillNestedObject(first_obj,map);
                            indexArrayObject.addArrayJSONObject(jsons,first_obj);
                        }
                        for (int index_second_category = 0; index_second_category < firstCategory.getSecondCateCount(); ++index_second_category) {
                            UserProfile2.SecondCategory secondCategory = firstCategory.getSecondCate(index_second_category);
                            stringMap.put("second_category", secondCategory.getName());
                            if (secondCategory.hasAttrs() || secondCategory.getThirdCateCount() == 0) {
                                JSONObject second_obj = new JSONObject();
                                //update_time
                                timeStampMap.put("update_time", String.valueOf(secondCategory.getTimestamp()));
                                parseBfdCategory(second_obj, secondCategory.getAttrs());
                                indexSecondNestedObject.fillNestedObject(second_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, second_obj);
                            }
                            for (int index_third_category = 0; index_third_category < secondCategory.getThirdCateCount(); ++index_third_category) {

                                UserProfile2.ThirdCategory thirdCategory = secondCategory.getThirdCate(index_third_category);
                                stringMap.put("third_category", thirdCategory.getName());
                                if (thirdCategory.hasAttrs() || thirdCategory.getFourthCateCount() == 0) {
                                    JSONObject third_obj = new JSONObject();
                                    //濉啓涓夌骇鍝佺被鐨勬洿鏂版椂闂�
                                    timeStampMap.put("update_time", String.valueOf(thirdCategory.getTimestamp()));
                                    parseBfdCategory(third_obj, thirdCategory.getAttrs());
                                    indexThirdNestedObject.fillNestedObject(third_obj, map);
                                    indexArrayObject.addArrayJSONObject(jsons, third_obj);
                                }
                                for (int index_fourth_category = 0; index_fourth_category < thirdCategory.getFourthCateCount(); ++index_fourth_category) {
                                    UserProfile2.FourthCategory fourthCategory = thirdCategory.getFourthCate(index_fourth_category);
                                    stringMap.put("fourth_category", fourthCategory.getName());
                                    if (fourthCategory.hasAttrs() || fourthCategory.getFiveCateCount() == 0) {
                                        JSONObject fourth_obj = new JSONObject();
                                        //濉啓鍥涚骇鍝佺被鐨勬洿鏂版椂闂�
                                        timeStampMap.put("update_time", String.valueOf(fourthCategory.getTimestamp()));
                                        parseBfdCategory(fourth_obj, fourthCategory.getAttrs());
                                        indexFourthNestedObject.fillNestedObject(fourth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fourth_obj);
                                    }
                                    for (int index_five_category = 0; index_five_category < fourthCategory.getFiveCateCount(); ++index_five_category) {
                                        UserProfile2.FiveCategory fiveCategory = fourthCategory.getFiveCate(index_five_category);
                                        stringMap.put("fourth_category", fiveCategory.getName());
                                        JSONObject fifth_obj = new JSONObject();
                                        //濉啓浜旂骇鍝佺被鐨勬洿鏂版椂闂�
                                        timeStampMap.put("update_time", String.valueOf(fiveCategory.getTimestamp()));
                                        if (fiveCategory.hasAttrs()) {
                                            parseBfdCategory(fifth_obj, fiveCategory.getAttrs());
                                        }
                                        indexFourthNestedObject.fillNestedObject(fifth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fifth_obj);
                                    }
                                }
                            }
                        }
                    }
                }
              
            }
            
            //鐭湡鍏磋叮鍋忓ソ,閫夊彇瑙勫垯锛氶暱鏈熷叴瓒ｅ亸濂界殑top2
            if (industryInfo.getMediaCateCount()>0) {
            	
    			List<FirstCategory> mediaCategories_1 = industryInfo.getMediaCateList();
    			FirstCategory[] mediaCategories = new FirstCategory[mediaCategories_1.size()];
    			for (int i=0; i<mediaCategories_1.size(); i++) {
    				mediaCategories[i] = mediaCategories_1.get(i);
    			}
    			System.out.println("mediaCategories.length:"+mediaCategories.length);
//    			CategoryCompare categoryCompare = new CategoryCompare();
//    			System.out.println("media before sort size:"+mediaCategories.size());
//    			mediaCategories.sort(categoryCompare);
//    			System.out.println("media after sort size:"+mediaCategories.size());
//    			if (mediaCategories.size()>0) {
//    				sort(mediaCategories);
////    			}
//    			List<FirstCategory> mediaCategories = new ArrayList<FirstCategory>();
//            	for (int ii=0; ii<mediaCategories_1.size(); ii++) {
//            		if (mediaCategories_1.get(ii).hasWeight()) {
//            			cateweightmap.put(ii, mediaCategories_1.get(ii).getWeight());
//            		}
//            	}
//            	System.out.println("media before sort");
//            	for (Map.Entry<Integer, Integer> iter : entriesSortedByValues(cateweightmap)) {
//            		mediaCategories.add(mediaCategories_1.get(iter.getKey()));
//        		}
//            	System.out.println("media after sort");
    			System.out.println("media before sort");
    			arraySort(mediaCategories);
    			System.out.println("media after sort");
    			if (mediaCategories.length>0) {
    				int num = 0;
        			if (mediaCategories.length==1) {
        				num = 1;
        			} else {
        				num = 2;
        			}
        			for (int index_first_category = 0; index_first_category < num; ++index_first_category){
                        UserProfile2.FirstCategory firstCategory = mediaCategories[index_first_category];
                        String firstCateName = firstCategory.getName();
                        Map<String, String> stringMap = new HashMap<String, String>();
                        stringMap.put("first_category", firstCateName);
                        stringMap.put("cid", cid);
                        Map<String, String> IntegerMap = new HashMap<String, String>();
                        Map<String, String> timeStampMap = new HashMap<String, String>();
                        IntegerMap.put("type", "5");
                        Map<String, Map<String, String>> map = new HashMap<String,Map<String, String>>();
                        map.put("String", stringMap);
                        map.put("Integer",IntegerMap);
                        map.put("TimeStamp", timeStampMap);

                        if (firstCategory.hasAttrs() || firstCategory.getSecondCateCount() == 0) {
                            JSONObject first_obj = new JSONObject();
                            timeStampMap.put("update_time",String.valueOf(firstCategory.getTimestamp()));
                            parseBfdCategory(first_obj, firstCategory.getAttrs());
                            indexFirstNestedObject.fillNestedObject(first_obj,map);
                            indexArrayObject.addArrayJSONObject(jsons,first_obj);
                        }


                        for (int index_second_category = 0; index_second_category < firstCategory.getSecondCateCount(); ++index_second_category) {
                            UserProfile2.SecondCategory secondCategory = firstCategory.getSecondCate(index_second_category);
                            stringMap.put("second_category", secondCategory.getName());
                            if (secondCategory.hasAttrs() || secondCategory.getThirdCateCount() == 0) {
                                JSONObject second_obj = new JSONObject();
                                timeStampMap.put("update_time", String.valueOf(secondCategory.getTimestamp()));
                                parseBfdCategory(second_obj, secondCategory.getAttrs());
                                indexSecondNestedObject.fillNestedObject(second_obj, map);
                                indexArrayObject.addArrayJSONObject(jsons, second_obj);
                            }
                            for (int index_third_category = 0; index_third_category < secondCategory.getThirdCateCount(); ++index_third_category) {

                                UserProfile2.ThirdCategory thirdCategory = secondCategory.getThirdCate(index_third_category);
                                stringMap.put("third_category", thirdCategory.getName());
                                if (thirdCategory.hasAttrs() || thirdCategory.getFourthCateCount() == 0) {
                                    JSONObject third_obj = new JSONObject();
                                    timeStampMap.put("update_time", String.valueOf(thirdCategory.getTimestamp()));
                                    parseBfdCategory(third_obj, thirdCategory.getAttrs());
                                    indexThirdNestedObject.fillNestedObject(third_obj, map);
                                    indexArrayObject.addArrayJSONObject(jsons, third_obj);
                                }
                                for (int index_fourth_category = 0; index_fourth_category < thirdCategory.getFourthCateCount(); ++index_fourth_category) {
                                    UserProfile2.FourthCategory fourthCategory = thirdCategory.getFourthCate(index_fourth_category);
                                    stringMap.put("fourth_category", fourthCategory.getName());
                                    if (fourthCategory.hasAttrs() || fourthCategory.getFiveCateCount() == 0) {
                                        JSONObject fourth_obj = new JSONObject();
                                        timeStampMap.put("update_time", String.valueOf(fourthCategory.getTimestamp()));
                                        parseBfdCategory(fourth_obj, fourthCategory.getAttrs());
                                        indexFourthNestedObject.fillNestedObject(fourth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fourth_obj);
                                    }
                                    for (int index_five_category = 0; index_five_category < fourthCategory.getFiveCateCount(); ++index_five_category) {
                                        UserProfile2.FiveCategory fiveCategory = fourthCategory.getFiveCate(index_five_category);
                                        stringMap.put("fifth_category", fiveCategory.getName());
                                        JSONObject fifth_obj = new JSONObject();
                                        timeStampMap.put("update_time", String.valueOf(fiveCategory.getTimestamp()));
                                        if (fiveCategory.hasAttrs()) {
                                            parseBfdCategory(fifth_obj, fiveCategory.getAttrs());
                                        }
                                        indexFifthNestedObject.fillNestedObject(fifth_obj, map);
                                        indexArrayObject.addArrayJSONObject(jsons, fifth_obj);
                                    }
                                }
                            }
                        }
                    }
    			}
            }
            
            
            
            
            
            
            
        }
    }

    public void setUpDimension(JSONObject up, JSONObject value, String key) {
        up.put(key, value);
    }

    public void setUpDimension(JSONObject up, JSONArray value, String key) {
        up.put(key,value);
    }
    
    public static void sort(List<FirstCategory> list){
    	List<FirstCategory> ls = new ArrayList<FirstCategory>();
    	for (int i=0; i<list.size(); i++) {
    		if (!list.get(i).hasWeight()) {
    			System.out.println("no weight");
    			list.remove(i);
    		}
    	}
    	for (int i=0; i<list.size(); i++) {
    		for (int j=0; j<list.size()-i-1; j++) {
    			FirstCategory tmp = null;
    			if (list.get(j).getWeight()<list.get(j+1).getWeight()) {
    				tmp = list.get(j);
    				list.set(j, list.get(j+1)) ;
    				list.set(j+1, tmp);
    			}
    		}
    	}
    }
    
    public static void arraySort(FirstCategory[] list){
    	for (int i=0; i<list.length; i++) {
    		if (!list[i].hasWeight()) {
    			System.out.println("no weight");
    		}
    	}
    	for (int i=0; i<list.length; i++) {
    		for (int j=0; j<list.length-i-1; j++) {
    			FirstCategory tmp = null;
    			if (list[j].getWeight()<list[j+1].getWeight()) {
    				tmp = list[j];
    				list[j]=list[j+1];
    				list[j+1]=tmp;
    			}
    		}
    	}
    }
}
