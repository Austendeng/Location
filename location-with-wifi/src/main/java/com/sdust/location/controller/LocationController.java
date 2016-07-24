package com.sdust.location.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.sdust.location.configuration.OrientConfiguration;
import com.sdust.location.dao.bean.Coordsbean;
import com.sdust.location.dao.bean.Wifibean;
import com.sdust.location.model.Average_Filter;
import com.sdust.location.model.Kalman_Filter;
import com.sdust.location.model.WifiModel;
import com.sdust.location.service.LocationService;


@SessionAttributes
@RestController
@RequestMapping(produces = "application/json;charset=UTF-8", method = { RequestMethod.GET, RequestMethod.POST })
public class LocationController {

	@Autowired
	LocationService locationService = null;

	@Autowired
	WifiModel wifiModel = null;
	/**
	 * 功能：wifi定位 参数：rsses
	 * http://127.0.0.1:8080/location/locateWithWifi?rsses=[{"rssValue":"-42.0","name":"iwere","macaddress":"3c:46:d8:a2:3e:19"},{"rssValue":"-58.0","name":"iwere","macaddress":"3c:46:d8:a2:3a:03"},{"rssValue":"-38.0","name":"iwere","macaddress":"3c:46:d8:a2:41:c7"},{"rssValue":"-48.0","name":"iwere","macaddress":"3c:46:d8:a2:4a:95"},{"rssValue":"-18.0","name":"iwere","macaddress":"74:1f:4a:cd:f1:30"}]&compass=90
	 * http://192.168.31.170:8080/wifi-location/locateWithWifi?rsses=[{"rssValue":"-42.0","name":"iwere","macaddress":"3c:46:d8:a2:3e:19"},{"rssValue":"-58.0","name":"iwere","macaddress":"3c:46:d8:a2:3a:03"},{"rssValue":"-38.0","name":"iwere","macaddress":"3c:46:d8:a2:41:c7"},{"rssValue":"-48.0","name":"iwere","macaddress":"3c:46:d8:a2:4a:95"},{"rssValue":"-18.0","name":"iwere","macaddress":"74:1f:4a:cd:f1:30"}]&compass=90
	 * 192.168.31.170
	 **/
	public boolean DEBUG = false;
	public boolean isCollect = true;
	
	public static List<ArrayList<Wifibean>> collectlist = new ArrayList<ArrayList<Wifibean>>();
    public static Long lastgroup = (long) 0;
	
	@RequestMapping(value = "/locateWithWifi")
	public String locatePositionWithWifi(@RequestParam(required = true, value = "rsses") String rsses,
			@RequestParam(required = false, value = "compass") Float compass,
			 HttpServletRequest request) {
		// 2 操作
		// 获取session
		/*System.out.println(rsses);
		System.out.println(compass);*/
		HttpSession session = request.getSession();
		Map<String, String> result = new HashMap<String, String>();		
		Kalman_Filter KF = new Kalman_Filter();
		ArrayList<Wifibean> wifilist_after = new ArrayList<Wifibean>();
		ArrayList<Wifibean> wifilist = new ArrayList<Wifibean>();
		wifilist = wifiModel.getWifiInfo(rsses);
		
		Coordsbean coord = new Coordsbean();
		List<ArrayList<Wifibean>> savedlist = (ArrayList<ArrayList<Wifibean>>) session.getAttribute("savedlist");
        List<Coordsbean> savedcoords = (List<Coordsbean>) session.getAttribute("savedcoords");
		List<Wifibean> lastlist = (ArrayList<Wifibean>) session.getAttribute("lastlist");
		Float savedcompass = (Float) session.getAttribute("savedcompass");
		Coordsbean lastcoord = (Coordsbean) session.getAttribute("lastcoord");
		if(savedcompass == null){
			savedcompass = new Float(0);
		}
		savedcompass += compass;
		session.setAttribute("savedcompass", savedcompass);
		
        if (savedlist == null) {
			savedlist = new ArrayList<ArrayList<Wifibean>>();			
		}
		savedlist.add(wifilist);
		session.setAttribute("savedlist", savedlist);
		
		if(lastcoord == null){
			lastcoord = new Coordsbean();
		}

		/*********** 测试用 ****************************/
		if (DEBUG) {
			List<ArrayList<Wifibean>> test = new ArrayList<ArrayList<Wifibean>>();
			test = savedlist;
			for (int i = 0; i < test.size(); i++) {
				for (int j = 0; j < test.get(i).size(); j++)
					System.out.println("address:  " + test.get(i).get(j).getMacaddress() + "  value:"
							+ test.get(i).get(j).getRssValue()+"  name:"+test.get(i).get(j).getName());
			}

		//	System.out.println("<><><><");
		}
		if(lastlist == null){
			lastlist = new ArrayList<Wifibean>();
		}
		
		if (savedlist.size() >= OrientConfiguration.RSSI_CCT) {
			if (OrientConfiguration.RSSI_CCT == 1) {
				wifilist_after = savedlist.get(0);
			} else {
				wifilist_after = KF.kalman(savedlist,lastlist);
			}	
			lastlist.clear();
			lastlist.addAll(wifilist_after);			
			
			session.setAttribute("lastlist", lastlist);
			savedcompass = savedcompass/OrientConfiguration.RSSI_CCT;

			/*********** 测试用 ****************************/
			if (DEBUG) {
				for (int i = 0; i < wifilist_after.size(); i++)
					System.out.println("address" + wifilist_after.get(i).getMacaddress() + " value"
							+ wifilist_after.get(i).getRssValue());
			}
			savedlist.clear();			
			coord = locationService.locatePositionWithWifi(wifilist_after,savedcompass,lastcoord);
			session.setAttribute("savedcompass", null);
			if(coord == null){
				result.put("status", "200");
				result.put("ErrorFlag", "1");// ErrorFlag为1代表定位失败
				return result.toString();
			}else {				
				if(savedcoords == null){
					savedcoords = new ArrayList<Coordsbean>();
					if(lastcoord.getRef_x()!=null){
						savedcoords.add(lastcoord);
					}
				}
				savedcoords.add(coord);
				session.setAttribute("savedcoords", savedcoords);
			//	System.out.println("savedcoordssize:"+savedcoords.size());
				if(savedcoords.size() == OrientConfiguration.Coords_CCT){
					/*for(int i=0;i<savedcoords.size();i++){
						System.out.println("savedcoords:"+savedcoords.get(i).getRef_x());
					}*/
					coord = KF.coordsKalMan(savedcoords);
					lastcoord = coord;
					session.setAttribute("lastcoord", lastcoord);
					result.put("lng", coord.getRef_x().toString());
					result.put("lat", coord.getRef_y().toString());					
					result.put("locationFlag", "true");
					result.put("status", "200");
					savedcoords.clear();
					
					return result.toString();
				}else {
					result.put("locationFlag", "false");
					result.put("status", "200");
					return result.toString();
				}
			}	
				
			}else {
				result.put("locationFlag", "false");
				result.put("status", "200");
				return result.toString();
			}
} 


	

	// http://localhost:8080/location/collectFigurePrint?rsses=[{"rssValue":"-42.0","name":"iwere","macaddress":"3c:46:d8:a2:3e:19"},{"rssValue":"-58.0","name":"iwere","macaddress":"3c:46:d8:a2:3a:03"},{"rssValue":"-38.0","name":"iwere","macaddress":"3c:46:d8:a2:41:c7"},{"rssValue":"-48.0","name":"iwere","macaddress":"3c:46:d8:a2:4a:95"},{"rssValue":"-18.0","name":"iwere","macaddress":"74:1f:4a:cd:f1:30"},{"rssValue":"-10.0","name":"iwere","macaddress":"74:1f:4a:cd:f1:32"}]&ref_x=1&ref_y=1&groupNo=57&actionFlag=1
	@RequestMapping(value = "/collectFigurePrint")
	public String collectFigurePrint(@RequestParam(required = true, value = "rsses") String rsses,
			@RequestParam(required = true, value = "ref_x") Double ref_x,
			@RequestParam(required = true, value = "ref_y") Double ref_y,
			@RequestParam(required = true, value = "groupNo") Long groupNo,
			@RequestParam(required = false, value = "actionFlag") Integer actionFlag // 0开始，1结束
	) {
		if(!lastgroup.equals(groupNo) ){
			lastgroup = groupNo;
			collectlist.clear();
		}
		System.out.println("last:"+lastgroup+"  group:"+groupNo);
		Map<String, String> result = new HashMap<String, String>();
		if (isCollect) {
			Kalman_Filter KF = new Kalman_Filter();
			Average_Filter AF = new Average_Filter();
			ArrayList<Wifibean> wifilist_after = new ArrayList<Wifibean>();
			ArrayList<Wifibean> wifilist = new ArrayList<Wifibean>();
			wifilist = wifiModel.getWifiInfo(rsses);
			for(int i=0;i<wifilist.size();i++){
				System.out.println("name:"+wifilist.get(i).getName()+"        macaddress:"+wifilist.get(i).getMacaddress()+"         rssi:"+wifilist.get(i).getRssValue());
			}
			collectlist.add(wifilist);
			System.out.println("size:"+collectlist.size());
			//if (actionFlag != null && actionFlag == 1) {
			if(collectlist.size()>=120){
			    if (collectlist.size() == 1)
					wifilist_after = collectlist.get(0);
				else {
				    // 使用卡尔曼滤波处理数据
				    //	wifilist_after = KF.kalman(collectlist);

				   // 使用均值滤波处理数据
			       wifilist_after = AF.averege(collectlist);
			   }
				for (int i = 0; i < wifilist_after.size(); i++) {
					System.out.println("采集数据。。。。。。。。" + "Macaddress:  " + wifilist_after.get(i).getMacaddress()
							+ "  Rss:" + wifilist_after.get(i).getRssValue() + "  name:"
							+ wifilist_after.get(i).getName());
				}
				collectlist.clear();
				locationService.setFigurePrint(wifilist_after, ref_x, ref_y, groupNo);
			}
		}
		result.put("status", "200");
		return result.toString();
	}
}