package com.sdust.location.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdust.location.dao.bean.Coordsbean;
import com.sdust.location.dao.bean.Wifibean;
import com.sdust.location.dao.interfaces.LocationWifiFingerprintMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Component
public class WifiModel {
	@Autowired
	LocationWifiFingerprintMapper lwfm = null;
	/**
	 * function:获取所有可能的AP组组号
	 **/
	public HashSet<Long> getWifigroup(ArrayList<Wifibean> wifilist) {
		HashSet<Long> group = new HashSet<Long>();
		for (Wifibean wifi : wifilist) {
			List<Long> wifitemp = new ArrayList<Long>();
			wifitemp = lwfm.selectgroupid(wifi.getMacaddress());
			for (Long wifigrouptemp : wifitemp)
				group.add(wifigrouptemp);
		}
		return group;
	}

	/**
	 * function:分隔各个WiFi macaddress及rss
	 **/
	public ArrayList<Wifibean> getWifiInfo(String rsses) {
		ArrayList<Wifibean> wifilist = new ArrayList<Wifibean>();
		List<Map<String, Object>> maplist = wi(rsses);		
		for(int i=0;i<maplist.size();i++){
			Wifibean wb = new Wifibean();
			wb.setMacaddress(maplist.get(i).get("macaddress")+"");
			wb.setRssValue(Double.parseDouble(maplist.get(i).get("rssValue")+""));
			wb.setName(maplist.get(i).get("name")+"");
			wifilist.add(wb);
		}
		return wifilist;
	}

	//JSON2List
	public static List<Map<String, Object>> wi(String rsses) {
		JSONArray jsonArr = JSONArray.fromObject(rsses);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Iterator<JSONObject> it = jsonArr.iterator();
		while (it.hasNext()) {
			JSONObject json2 = it.next();
			list.add(parseJSON2Map(json2.toString()));
		}
		return list;

	}

	public static Map<String, Object> parseJSON2Map(String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 最外层解析
		JSONObject json = JSONObject.fromObject(jsonStr);
		for (Object k : json.keySet()) {
			Object v = json.get(k);
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<JSONObject> it = ((JSONArray) v).iterator();
				while (it.hasNext()) {
					JSONObject json2 = it.next();
					list.add(parseJSON2Map(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}
		return map;
	}

	public ArrayList<Wifibean> value2Deviation(ArrayList<Wifibean> wifilist) {
		// TODO Auto-generated method stub
		
		return null;
	}	
	
	public boolean isLegal(Coordsbean temp_coord, Float compass, Coordsbean lastcoord){
		//System.out.println("compass:"+compass);
		//处理学校磁场受干扰
		/*if(compass>=0 && compass<=40){
			compass = 1.0f;
		}else if(compass>=60 && compass<=120){
			compass = 95.0f;
		}else if(compass>=130 && compass<=190){
			compass = 185.0f;
		}else if(compass>=200 && compass<=340){
			compass = 275.0f;
		}else{
			return true;
		}*/
	//	double k = (temp_coord.getRef_y() - lastcoord.getRef_y()) / (temp_coord.getRef_x() - lastcoord.getRef_x());
		if(Math.abs(compass - 45.0) < 0.001 || Math.abs(compass - 135.0) < 0.001){
			compass += 1; 
		}
		double k1 = Math.tan((75-compass)/360*2*Math.PI);
		double k2 = Math.tan((105-compass)/360*2*Math.PI);
		double y1 = k1 * (temp_coord.getRef_x() - lastcoord.getRef_x());
		double y2 = k2 * (temp_coord.getRef_x() - lastcoord.getRef_x());
		
		if((compass>=0 && compass<45) || (compass>=315 && compass<360)){
			if((temp_coord.getRef_y() - lastcoord.getRef_y()) >= y1 && (temp_coord.getRef_y() - lastcoord.getRef_y())>= y2){
				
				return true;
			}
		}else if(compass>=45 && compass<135){
			if((temp_coord.getRef_y() - lastcoord.getRef_y()) >= y1 && (temp_coord.getRef_y() - lastcoord.getRef_y()) <=y2){
				
				return true;
			}			
		}else if(compass>=135 && compass<225){
			if((temp_coord.getRef_y() - lastcoord.getRef_y()) <= y1 && (temp_coord.getRef_y() - lastcoord.getRef_y()) <= y2){
				
				return true;
			}
		}else {
			if((temp_coord.getRef_y() - lastcoord.getRef_y()) <= y1 && (temp_coord.getRef_y() - lastcoord.getRef_y()) >=y2){
				
				return true;
			}
		}
		return false;
	}
}
