package com.sdust.location.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdust.location.dao.bean.Coordsbean;
import com.sdust.location.dao.bean.LocationWifiFingerprint;
import com.sdust.location.dao.bean.Locationbean;
import com.sdust.location.dao.bean.Wifibean;
import com.sdust.location.dao.interfaces.LocationWifiFingerprintMapper;
import com.sdust.location.model.LocationModel;
import com.sdust.location.model.WifiModel;

@Service
public class LocationService {
	@Autowired
	LocationModel locationModel = null;

	@Autowired
	WifiModel wifiModel = null;

	@Autowired
	LocationWifiFingerprintMapper lwfm = null;

	/*
	 * @Autowired LocationWifiFingerprintMapper lwfm = null;
	 */

	public Coordsbean locatePositionWithWifi(ArrayList<Wifibean> wifilist, Float compass, Coordsbean lastcoord) {
	//	System.out.println("savedcompass:" + compass + "  lastcoord:" + lastcoord.getRef_y());
		Double min_dis = Double.MAX_VALUE;
		// 获得被Wifi覆盖的整个区域的Wifi组的组号
		HashSet<Long> group = new HashSet<Long>();
		group = wifiModel.getWifigroup(wifilist);
		// System.out.println(group.toString());
		if (group == null || group.size() == 0) {
			return null;
		}

		Map<Long, Double> simMap = new HashMap<Long, Double>();
		Map<Long, Coordsbean> coordsMap = new HashMap<Long, Coordsbean>();
		for (Long groupid : group) {
			ArrayList<LocationWifiFingerprint> InvolvedFingerPrints = null;
			Coordsbean temp_coord = new Coordsbean();
			// 获取相关Wifi站点数据
			InvolvedFingerPrints = locationModel.getInvolvedFingerPrints(groupid);
			temp_coord.setRef_x(InvolvedFingerPrints.get(0).getReferenceX());
			temp_coord.setRef_y(InvolvedFingerPrints.get(0).getReferenceY());

			// 检查坐标是否在方向范围内，若不在进行下一趟循环
			if (lastcoord.getRef_x() != null) {
				if (wifiModel.isLegal(temp_coord, compass, lastcoord) == false) {
					continue;
				}
			}
			coordsMap.put(groupid, temp_coord);
			Double temp_dis;
			// KNN第一步计算欧式距离
			temp_dis = locationModel.getEuclidean_distance(InvolvedFingerPrints, wifilist);
			if (temp_dis < min_dis)
				min_dis = temp_dis;
			simMap.put(groupid, temp_dis);
		//	System.out.println("group:" + groupid + " dis:" + temp_dis);
		}

		// KNN第二步归类，得出位置
		Coordsbean coord = locationModel.getLocation(min_dis, simMap, coordsMap);
        if(Math.abs(0.0 - coord.getRef_x()) < 0.001)
        	coord=lastcoord;
		/*
		 * if (lastcoord.getRef_x() != null) { // 依据方向判断新的位置是否可信 if (compass >=
		 * 45 && compass <= 135) {// 手机指向为东 if (coord.getRef_x() <
		 * lastcoord.getRef_x()) { return lastcoord; } } else if (compass >= 135
		 * && compass <= 225) {// 手机指向为南 if (coord.getRef_y() >
		 * lastcoord.getRef_y()) { return lastcoord; } } else if (compass >= 225
		 * && compass <= 315) {// 手机指向为西 if (coord.getRef_x() >
		 * lastcoord.getRef_x()) { return lastcoord; } } else {// 手机指向为北 if
		 * (coord.getRef_y() < lastcoord.getRef_y()) { return lastcoord; } } }
		 */
		/*Long geo = GeoSOT.getGeoNum(coord.getRef_y(), coord.getRef_x(), (byte) 26);
		System.out.println("格点《》《》《》《》：" + geo);
		locationModel.buzhidaojiaosha("DZN-DZP","1",geo);
	    locationModel.buzhidaojiaosha("LBSBD", "3", geo);*/

		Locationbean location = locationModel.coord2location(coord);
        
		return coord;
	}

	public void setFigurePrint(ArrayList<Wifibean> wifilist, Double ref_x, Double ref_y, Long groupNo) {
		// TODO Auto-generated method stub
		// 获取该组Wifi Macaddress
		List<String> wifilistInvolved = new ArrayList<String>();
		wifilistInvolved = locationModel.getWifiList(groupNo);

		double Rss_max = -Double.MAX_VALUE;
		List<LocationWifiFingerprint> figurelist = new ArrayList<LocationWifiFingerprint>();
		for (String wl : wifilistInvolved) {
			for (int i = 0; i < wifilist.size(); i++) {
				if (wifilist.get(i).getMacaddress().equals(wl)) {// 该Wifi为此group中的一个
					LocationWifiFingerprint figureprint = new LocationWifiFingerprint();
					figureprint.setGroupNum(groupNo);
					figureprint.setReferenceX(ref_x);
					figureprint.setReferenceY(ref_y);
					figureprint.setRssValue(wifilist.get(i).getRssValue());
					figureprint.setWifiMac(wifilist.get(i).getMacaddress());
					figureprint.setWifiName(wifilist.get(i).getName());
					figurelist.add(figureprint);
					if (wifilist.get(i).getRssValue() > Rss_max)
						Rss_max = wifilist.get(i).getRssValue();
				}
			}

		}

		for (LocationWifiFingerprint lwfp : figurelist) {
			lwfp.setRssValue(lwfp.getRssValue() - Rss_max);
			int a = locationModel.updateRssI(lwfp);
			System.out.println("采集完成:"+a);
		}
	}

}