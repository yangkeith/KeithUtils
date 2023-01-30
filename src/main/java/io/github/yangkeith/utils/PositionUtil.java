package io.github.yangkeith.utils;

import io.github.yangkeith.dto.Gps;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

/**
 * 各地图API坐标系统比较与转换;
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系,
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外）;
 * GCJ02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。
 * 谷歌中国地图和搜搜中国地图采用的是GCJ02地理坐标系;
 * BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系;
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。
 */
public class PositionUtil {
    
    public static final String BAIDU_LBS_TYPE = "bd09ll";
    
    /**
     * bdπ
     */
    public static double bd_pi = 3.14159265358979324 * 3000.0 / 180.0;
    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;
    /**
     * ct工厂
     *
     * @see CoordinateTransformFactory
     */
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    /**
     * crs工厂
     *
     * @see CRSFactory
     */
    private static final CRSFactory crsFactory = new CRSFactory();
    /**
     * wgs84参数
     *
     * @see String
     */
    static final String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +datum=WGS84 +units=degrees";
    /**
     * wgs84
     *
     * @see CoordinateReferenceSystem
     */
    CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84",
            WGS84_PARAM);
    
    /**
     * 创建crs
     *
     * @param crsSpec crs规范
     * @return {@link CoordinateReferenceSystem }
     * @author Keith
     * @date 2023-01-30
     */
    private static CoordinateReferenceSystem createCRS(String crsSpec) {
        CoordinateReferenceSystem crs = null;
        // test if name is a PROJ4 spec
        if (crsSpec.indexOf("+") >= 0 || crsSpec.indexOf("=") >= 0) {
            crs = crsFactory.createFromParameters("Anon", crsSpec);
        } else {
            crs = crsFactory.createFromName(crsSpec);
        }
        // crs = crsFactory.createFromParameters("Anon", crsSpec);
        return crs;
    }
    
    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     *
     * @param lat
     * @param lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps gps84_To_Gcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Gps(mgLat, mgLon);
    }
    
    /**
     * 火星坐标系 (GCJ-02) to 84
     *
     * @param lat
     * @param lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps gcj_To_Gps84(double lat, double lon) {
        Gps gps = transform(lat, lon);
        double lontitude = lon * 2 - gps.getWgLon();
        double latitude = lat * 2 - gps.getWgLat();
        return new Gps(latitude, lontitude);
    }
    
    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     *
     * @param gg_lat
     * @param gg_lon
     * @return {@link Gps }
     * @author Keith
     * * @date 2023-01-30
     */
    public static Gps gcj02_To_Bd09(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * bd_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * bd_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Gps(bd_lat, bd_lon);
    }
    
    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标
     *
     * @param bd_lat
     * @param bd_lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps bd09_To_Gcj02(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * bd_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * bd_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Gps(gg_lat, gg_lon);
    }
    
    /**
     * (BD-09)-->84
     *
     * @param bd_lat
     * @param bd_lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps bd09_To_Gps84(double bd_lat, double bd_lon) {
        Gps gcj02 = PositionUtil.bd09_To_Gcj02(bd_lat, bd_lon);
        Gps map84 = PositionUtil.gcj_To_Gps84(gcj02.getWgLat(),
                gcj02.getWgLon());
        return map84;
        
    }
    
    /**
     * 84->bd09
     *
     * @param gps_lat
     * @param gps_lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps Gps84_to_bd09(double gps_lat, double gps_lon) {
        Gps gcj02 = PositionUtil.gps84_To_Gcj02(gps_lat, gps_lon);
        Gps bd09 = PositionUtil.gcj02_To_Bd09(gcj02.getWgLat(),
                gcj02.getWgLon());
        return bd09;
    }
    
    /**
     * bd09ll->bd09mc
     *
     * @param bd09ll_lat
     * @param bd09ll_lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps bd09ll_to_bd09mc(double bd09ll_lat, double bd09ll_lon) {
        String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +datum=WGS84 +units=degrees";
        String tgtCRS = "+title=BaiDuMercator +proj=merc +a=6378206 +b=6356584.314245179 +lat_ts=0.0 +lon_0=0.0 +x_0=0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs";
        CoordinateTransform trans = ctFactory.createTransform(
                createCRS(WGS84_PARAM), createCRS(tgtCRS));
        ProjCoordinate pout = new ProjCoordinate();
        ProjCoordinate p = new ProjCoordinate(bd09ll_lon, bd09ll_lat);
        trans.transform(p, pout);
        return new Gps(pout.y, pout.x);
    }
    
    /**
     * wgs84->db09mc
     *
     * @param gps_lat
     * @param gps_lon
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps wgs84_to_bd09mc(double gps_lat, double gps_lon) {
        Gps bd09ll = Gps84_to_bd09(gps_lat, gps_lon);
        Gps bd09mc = bd09ll_to_bd09mc(bd09ll.getWgLat(), bd09ll.getWgLon());
        return bd09mc;
    }
    
    /**
     * 判断坐标是否在国内
     *
     * @param lat
     * @param lon
     * @return
     * @Method TODO
     * @author Keith
     * @Date 2018年12月11日
     * @version 1.0.0
     */
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347) {
            return true;
        }
        if (lat < 0.8293 || lat > 55.8271) {
            return true;
        }
        return false;
    }
    
    /**
     * 变换
     *
     * @param lat 纬度
     * @param lon 朗
     * @return {@link Gps }
     * @author Keith
     * @date 2023-01-30
     */
    public static Gps transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new Gps(lat, lon);
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Gps(mgLat, mgLon);
    }
    
    /**
     * 变换纬度
     *
     * @param x x
     * @param y y
     * @return double
     * @author Keith
     * @date 2023-01-30
     */
    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    
    /**
     * 变换朗
     *
     * @param x x
     * @param y y
     * @return double
     * @author Keith
     * @date 2023-01-30
     */
    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }
    
    /**
     * 地球半径
     */
    private static double earthRadius = 6378.137;
    
    /**
     * 经纬度转化成弧度
     *
     * @param d 经度/纬度
     * @return double
     * @author Keith
     * @date 2023-01-30
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    
    /**
     * 计算两个坐标点之间的距离
     *
     * @param firstLatitude   第一个坐标的纬度
     * @param firstLongitude  第一个坐标的经度
     * @param secondLatitude  第二个坐标的纬度
     * @param secondLongitude 第二个坐标的经度
     * @return double
     * @author Keith
     * @date 2023-01-30
     */
    public static double getDistance(double firstLatitude, double firstLongitude,
                                     double secondLatitude, double secondLongitude) {
        double firstRadLat = rad(firstLatitude);
        double firstRadLng = rad(firstLongitude);
        double secondRadLat = rad(secondLatitude);
        double secondRadLng = rad(secondLongitude);
        
        double a = firstRadLat - secondRadLat;
        double b = firstRadLng - secondRadLng;
        double cal = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(firstRadLat)
                * Math.cos(secondRadLat) * Math.pow(Math.sin(b / 2), 2))) * earthRadius;
        double result = Math.round(cal * 10000d) / 10000d;
        return result;
    }
    
    
    /**
     * 获取距离
     *
     * @param firstPoint  第一点
     * @param secondPoint 第二点
     * @return double
     * @author Keith
     * @date 2023-01-30
     */
    public static double getDistance(Gps firstPoint, Gps secondPoint) {
        double firstLatitude = firstPoint.getWgLat();
        double firstLongitude = firstPoint.getWgLon();
        double secondLatitude = secondPoint.getWgLat();
        double secondLongitude = secondPoint.getWgLon();
        return getDistance(firstLatitude, firstLongitude, secondLatitude, secondLongitude);
    }
    
    /**
     * 计算两个坐标点之间的距离
     *
     * @param firstPoint  第一个坐标点的（纬度,经度） 例如："31.2553210000,121.4620020000"
     * @param secondPoint 第二个坐标点的（纬度,经度） 例如："31.2005470000,121.3269970000"
     * @return double
     * @author Keith
     * @date 2023-01-30
     */
    public static double getDistance(String firstPoint, String secondPoint) {
        String[] firstArray = firstPoint.split(",");
        String[] secondArray = secondPoint.split(",");
        double firstLatitude = Double.valueOf(firstArray[0].trim());
        double firstLongitude = Double.valueOf(firstArray[1].trim());
        double secondLatitude = Double.valueOf(secondArray[0].trim());
        double secondLongitude = Double.valueOf(secondArray[1].trim());
        return getDistance(firstLatitude, firstLongitude, secondLatitude, secondLongitude);
    }
    
    public static void main(String[] args) {
        
        // 北斗芯片获取的经纬度为WGS84地理坐标 31.426896,119.496145
        Gps gps = new Gps(39.754672309028, 116.456306152344);
		/*Gps star = gcj_To_Gps84(gps.getWgLat(), gps.getWgLon());
		System.out.println("star:" + star);

		gps = new Gps(22.818603515625, 113.741112);
		star = gps84_To_Gcj02(gps.getWgLat(), gps.getWgLon());
		// System.out.println("star:" + star);

		gps = new Gps(22.6891708374023, 113.968391);
		star = gps84_To_Gcj02(gps.getWgLat(), gps.getWgLon());*/
        // System.out.println("star:" + star);
        
        gps = new Gps(37.113370564999627, 116.97719315613926);
        Gps star = Gps84_to_bd09(gps.getWgLat(), gps.getWgLon());
        System.out.println("star:" + star);
    }
}
