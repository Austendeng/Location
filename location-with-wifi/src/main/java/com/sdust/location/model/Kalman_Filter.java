package com.sdust.location.model;

import java.util.ArrayList;
import java.util.List;

import com.sdust.location.configuration.OrientConfiguration;
import com.sdust.location.dao.bean.Coordsbean;
import com.sdust.location.dao.bean.Wifibean;

public class Kalman_Filter {
	public boolean firstflag = true;
	public double KalMan_Pk;

	public ArrayList<Wifibean> kalman_single(ArrayList<Wifibean> wb_last, ArrayList<Wifibean> wb_new) {
		if (firstflag == true) {
			KalMan_Pk = OrientConfiguration.KalMan_Pk;
			firstflag = false;
		}

		double Pk_1 = KalMan_Pk + OrientConfiguration.KALMAN_RSSQ;
		double Kg = Pk_1 / (Pk_1 + OrientConfiguration.KALMAN_RSSR);
		for (int i = 0; i < wb_last.size(); ++i) {
			for (int j = 0; j < wb_new.size(); ++j) {
				if (wb_last.get(i).getMacaddress().equals(wb_new.get(j).getMacaddress())) {
					Double xk_1 = wb_last.get(i).getRssValue();
					wb_new.get(j).setRssValue((Double) (xk_1 + Kg * (wb_new.get(j).getRssValue() - xk_1)));
					break;
				}
			}
		}

		firstflag = true;
		KalMan_Pk = (Double) ((double) (1 - Kg) * Pk_1);
		return wb_new;
	}

	public ArrayList<Wifibean> kalman(List<ArrayList<Wifibean>> kl,List<Wifibean> lastlist) {
		Kalman_Filter Kf = new Kalman_Filter();
		boolean flag = true;
		boolean missingflag = true;
		ArrayList<Wifibean> a = new ArrayList<Wifibean>();
		ArrayList<Wifibean> wblist_last = new ArrayList<Wifibean>();
		ArrayList<Wifibean> wblist_new = new ArrayList<Wifibean>();
		for (int j = 1; j < kl.size(); j++) {

			if (flag == true) {
				wblist_last = kl.get(0);
				flag = false;
			}
			wblist_new = kl.get(j);
			wblist_new = Kf.kalman_single(wblist_last, wblist_new);
			wblist_last = wblist_new;

		}
		for(Wifibean lw:lastlist){
			missingflag = true;
			for(Wifibean wn:wblist_new){
			  if(lw.getMacaddress().equals(wn.getMacaddress())){
				  missingflag = false;
				  break;
			  }				
			}
			if(missingflag){
				a.add(lw);
			}
		}
		wblist_new.addAll(a);

		return wblist_new;

	}
	
	public Coordsbean coordsKalMan(List<Coordsbean> coords){
		double coordKalMan_Pk = OrientConfiguration.KalMan_Pk;
		double Kg;
		Coordsbean cd = new Coordsbean();
		cd = coords.get(0);		
		for (int i = 1; i < coords.size(); ++i) {
			coordKalMan_Pk = coordKalMan_Pk + OrientConfiguration.KALMAN_COORDQ;
			Kg = coordKalMan_Pk / (coordKalMan_Pk + OrientConfiguration.KALMAN_COORDR);
			cd.setRef_x(cd.getRef_x()+Kg *(coords.get(i).getRef_x()-cd.getRef_x()));
			cd.setRef_y(cd.getRef_y()+Kg *(coords.get(i).getRef_y()-cd.getRef_y()));
		}
		return cd;
	}	

}
