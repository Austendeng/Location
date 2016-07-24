package com.sdust.location.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdust.location.configuration.OrientConfiguration;
import com.sdust.location.dao.bean.Coordsbean;
import com.sdust.location.dao.bean.LocationWifiFingerprint;
import com.sdust.location.dao.bean.Locationbean;
import com.sdust.location.dao.bean.Wifibean;
import com.sdust.location.dao.interfaces.LocationWifiFingerprintMapper;


@Component
public class LocationModel {
	@Autowired
	LocationWifiFingerprintMapper lwfm = null;
	
	public void buzhidaojiaosha(String scopeCode,String functionCode,long geoNum){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("sensor_scope_code", scopeCode);
		map.put("geo_num", geoNum);
		map.put("function_code", functionCode);
		lwfm.insertSensorScope(map);
	}

	public ArrayList<LocationWifiFingerprint> getInvolvedFingerPrints(Long groupid) {

		return lwfm.selectInvolvedFingerPrints(groupid);
	}

	/***
	 * function:加权K邻近
	 */
	public Coordsbean getLocation(Double min_dis, Map<Long, Double> simMap, Map<Long, Coordsbean> coordsMap) {
		// TODO Auto-generated method stub
		if (Math.abs(0.0 - min_dis) < 0.001) {
			long targetgroup = 0;
			for (Long groupid : simMap.keySet()) {
				if (Math.abs(0.0 - simMap.get(groupid)) < 0.001) {
					targetgroup = groupid;
					return coordsMap.get(targetgroup);
				}
			}
		}

		Coordsbean cb = new Coordsbean();
		Map<Long, Double> group = new HashMap<Long, Double>();
		Double disper_sum = 0.0;

		Double threshold = OrientConfiguration.RSSI_RATN * min_dis;
		for (Long simMapkey : simMap.keySet()) {
			if (simMap.get(simMapkey) <= threshold) {
				group.put(simMapkey, simMap.get(simMapkey));
				disper_sum += (Double) (1.0 / simMap.get(simMapkey));
			}
		}
		
		System.out.println(group.keySet()+"阈值内");

		Double sum_x = 0.0;
		Double sum_y = 0.0;
		for (Long coordsMapkey : coordsMap.keySet()) {
			for (Long groupid : group.keySet()) {
				if (groupid == coordsMapkey) {
					sum_x += (Double) 1.0 / group.get(groupid) / disper_sum * coordsMap.get(coordsMapkey).getRef_x();
					sum_y += (Double) 1.0 / group.get(groupid) / disper_sum * coordsMap.get(coordsMapkey).getRef_y();
				}
			}
		}

		cb.setRef_x(sum_x);
		cb.setRef_y(sum_y);
		return cb;
	}

	public Double getEuclidean_distance(List<LocationWifiFingerprint> InvolvedFingerPrints,
			ArrayList<Wifibean> wifilist) {
		// TODO Auto-generated method stub
		Double dis = 0.0;
		
		double Rss_max = -Double.MAX_VALUE;
		for (LocationWifiFingerprint ifp : InvolvedFingerPrints) {
			for (int i = 0; i < wifilist.size(); i++) {
				if (wifilist.get(i).getMacaddress().equals(ifp.getWifiMac())) {// 该Wifi为此group中的一个
					if(wifilist.get(i).getRssValue()>Rss_max)
						Rss_max = wifilist.get(i).getRssValue();
				}
			}

		}
		
		for (LocationWifiFingerprint ifp : InvolvedFingerPrints) {			
			for (int i=0;i<wifilist.size();i++) {
				if (ifp.getWifiMac().equals(wifilist.get(i).getMacaddress())) {
					// System.out.println("ifp.getRssValue():<><><>"+ifp.getRssValue()+"
					// wl.getRssValue():<><><>"+wl.getRssValue());
					//if (ifp.getRssValue() != null && wl.getRssValue() != null)
					if(ifp.getRssValue() == null)
						ifp.setRssValue(Rss_max);
					if(wifilist.get(i).getRssValue() == null)
						wifilist.get(i).setRssValue(Rss_max);
					dis += Math.pow((ifp.getRssValue() - (wifilist.get(i).getRssValue()-Rss_max)), 2);
				}
			}
		}
		dis = (Double) Math.sqrt(dis);
		return dis;
	}

	public void getBestLocation(Coordsbean loc_last, Coordsbean loc_forest, Double cov_x, Double cov_y, Double speed_x,
			Double speed_y, Double Kg) {
		// TODO Auto-generated method stub

	}

	// 离线阶段获取组号相对应的所有Wifi的Macaddress
	public List<String> getWifiList(Long groupNo) {
		// TODO Auto-generated method stub

		List<String> wifilist = new ArrayList<String>();
		wifilist = lwfm.selectWifiList(groupNo);
		
		//	System.out.println(wifilist);
		return lwfm.selectWifiList(groupNo);
	}

	public int updateRssI(LocationWifiFingerprint figureprint) {
		// TODO Auto-generated method stub
		return lwfm.updateRssI(figureprint);
	}
	
	public Locationbean coord2location(Coordsbean coord){
		Locationbean lb = new Locationbean();
		lb.setLng(coord.getRef_x());
		lb.setLat(coord.getRef_y());
		return lb;
	}

}
