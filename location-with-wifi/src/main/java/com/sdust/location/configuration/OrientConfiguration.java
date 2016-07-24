package com.sdust.location.configuration;

public class OrientConfiguration {
	public static double		RSSI_RATN		=  1.2;  // 设置确定比例法RSSI偏差值阈值
	public static int 		RSSI_CCT		= 3;     // 设置算RSSI卡尔曼滤波时采集RSSI的次数（卡尔曼滤波）
	public final static double 	KALMAN_RSSQ		=  1e-2;  // 设置处理RSSI信号的卡尔曼滤波系数Q
	public final static double 	KALMAN_RSSR		=  4;  // 设置处理RSSI信号的卡尔曼滤波系数R
	public final static double 	KALMAN_COORDQ		=  1e-2;  // 设置处理坐标的卡尔曼滤波系数Q
	public final static double 	KALMAN_COORDR		=  4;  // 设置处理坐标的卡尔曼滤波系数R
	public static double KalMan_Pk = 10;
	public static int 		Coords_CCT		= 3;
}
