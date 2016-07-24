package com.sdust.location.dao.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sdust.location.dao.bean.LocationWifiFingerprint;

public interface LocationWifiFingerprintMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table location_wifi_fingerprint
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table location_wifi_fingerprint
     *
     * @mbggenerated
     */
    int insert(LocationWifiFingerprint record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table location_wifi_fingerprint
     *
     * @mbggenerated
     */
    int insertSelective(LocationWifiFingerprint record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table location_wifi_fingerprint
     *
     * @mbggenerated
     */
    LocationWifiFingerprint selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table location_wifi_fingerprint
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(LocationWifiFingerprint record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table location_wifi_fingerprint
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(LocationWifiFingerprint record);

	List<Long> selectgroupid(String macaddress);

	ArrayList<LocationWifiFingerprint> selectInvolvedFingerPrints(Long groupid);

	List<String> selectWifiList(Long groupid);

	int updateRssI(LocationWifiFingerprint figureprint);

	void insertSensorScope(Map<String, Object> map);
}