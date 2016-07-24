package com.sdust.location.model;

import java.util.ArrayList;
import java.util.List;

import com.sdust.location.dao.bean.Wifibean;


public class Average_Filter {
	public ArrayList<Wifibean> averege(List<ArrayList<Wifibean>> wifilist) {
		ArrayList<Wifibean> averagelist = new ArrayList<Wifibean>();
		averagelist = wifilist.get(10);
		for (int i = 0; i < averagelist.size(); i++) {
			for (int j = 11; j < wifilist.size(); j++) {
				for (int k = 0; k < wifilist.get(j).size(); k++)
					if (averagelist.get(i).getMacaddress().equals(wifilist.get(j).get(k).getMacaddress())) {
						averagelist.get(i)
								.setRssValue(averagelist.get(i).getRssValue() + wifilist.get(j).get(k).getRssValue());
						break;
					}
			}
		}

		for (int i = 0; i < averagelist.size(); i++) {
			averagelist.get(i).setRssValue(averagelist.get(i).getRssValue() / (wifilist.size()-10));
		}
	
		return averagelist;
	}

}
