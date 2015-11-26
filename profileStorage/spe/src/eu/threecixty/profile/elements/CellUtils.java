package eu.threecixty.profile.elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class CellUtils {
	

	public static final double DX = 234.431;
	private static final double DY = 234.431;
	private static final double HAF_OF_DX = DX / 2;
	private static final double HALF_OF_DY = DY / 2;
	private static final double CONV_FACTOR = (2.0 * Math.PI)/360.0;
	private static final int R = 6371;
	
	private static final int TOTAL_NUMBER_OF_CELLS = 10000;
	
	// The following constants are kept in lower case due to original version
	private static final double m1 = 111132.92;	// latitude calculation term 1
	private static final double m2 = -559.82;	// latitude calculation term 2
	private static final double m3 = 1.175;	// latitude calculation term 3
	private static final double m4 = -0.0023;	// latitude calculation term 4
	private static final double p1 = 111412.84;	// longitude calculation term 1
	private static final double p2 = -93.5;	// longitude calculation term 2
	private static final double p3 = 0.118;	// longitude calculation term 3


	 private static final Logger LOGGER = Logger.getLogger(
			 CellUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	
	private static Map <Integer, Double> cellIdLats;
	private static Map <Integer, Double> cellIdLons;
	
	/**
	 * From a given list of cell IDs, throw away cell IDs which don't have any intersects with
	 * the circle from a given original latitude, longitude and distance. Then, the method just
	 * returns an effective list of cell IDs.
	 * @param cellIds
	 * @param distance
	 * @param orgLat
	 * @param orgLon
	 * @return
	 */
	public static List <Integer> calcEffectiveCellIds(List <Integer> cellIds,
			double distance, double orgLat, double orgLon) {
		if (cellIdLats == null) {
			synchronized (CellUtils.class) {
				if (cellIdLats == null) initCellIDLatLons();
			}
		}
		List <Integer> effectiveCellIds = new LinkedList <Integer>();
		for (Integer cellId: cellIds) {
			Double checkedLat = cellIdLats.get(cellId);
			if (checkedLat == null) continue;
			Double checkedLon = cellIdLons.get(cellId);
			if (checkedLon == null) continue;
			if (isIntersects(orgLat, orgLon, distance, checkedLat, checkedLon)) {
				effectiveCellIds.add(cellId);
			}
		}
		return effectiveCellIds;
	}
	
	private static boolean isIntersects(double orgLat, double orgLon,
			double distance, double checkedLat, double checkedLon) {
		double x_closest = 0;

		if(Math.abs(orgLat - checkedLat) < latMeterToDeg(orgLat, HAF_OF_DX)) {
			x_closest = orgLat;
		} else {
			x_closest = checkedLat + latMeterToDeg(orgLat, HAF_OF_DX) * sign(orgLat - checkedLat);
		}


		double y_closest = 0;

		if(Math.abs(orgLon - checkedLon) < lonMeterToDeg(orgLat, HALF_OF_DY)) {
			y_closest = orgLon;
		} else {
			y_closest = checkedLon + lonMeterToDeg(orgLat, HALF_OF_DY) * sign(orgLon - checkedLon);

		}
		double calcutedDistance = getDistanceFromLatLonInMeter(x_closest, y_closest, orgLat, orgLon);
		//if (DEBUG_MOD) LOGGER.info("calculated distance: " + calcutedDistance + ", distance = " + distance);
		return calcutedDistance < distance; //(Math.pow(x_closest - lat, 2) + Math.pow(y_closest - lon, 2) < Math.pow(d,2));
	}
	
	private static int sign(double d) {
		if (d >= 0) return 1;
		return -1;
	}
	
	private static double getDistanceFromLatLonInMeter (double lat1, double lon1, double lat2, double lon2) {
		double dLat = deg2rad(lat2-lat1);  // deg2rad below
		double dLon = deg2rad(lon2-lon1); 
		double a = 
				Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
				Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c * 1000; // Distance in m
		return d;
	}

	private static double lonMeterToDeg(double lon, double distance) {
		double radian = deg2rad(lon);
		return distance / ((p1 * Math.cos(radian)) + (p2 * Math.cos(3 * radian)) + (p3 * Math.cos(5 * radian)));
	}
	
	private static double latMeterToDeg(double lat, double distance) {
		double radian = deg2rad(lat);
		return distance / (m1 + (m2 * Math.cos(2 * radian)) + (m3 * Math.cos(4 * radian)) + (m4 * Math.cos(6 * radian)));
	}

	private static double deg2rad(double deg){
		return (deg * CONV_FACTOR);
	}

	private static void initCellIDLatLons() {
		cellIdLats = new HashMap <Integer, Double>();
		cellIdLons = new HashMap <Integer, Double>();
		
		InputStream input = CellUtils.class.getResourceAsStream("/cells.json");
		
		try {
			String content = getContent(input);
			if (content == null) return;
			input.close();
			JSONObject json = new JSONObject(content);
			
			for (int i = 0; i < TOTAL_NUMBER_OF_CELLS; i++) {
				String value = json.getString(i + "");
				if (value == null) continue;
				String [] arr = value.split(" ");
				if (arr.length != 2) continue;
				try {
					double lon = Double.parseDouble(arr[0]);
					double lat = Double.parseDouble(arr[1]);
					cellIdLats.put(i, lat);
					cellIdLons.put(i, lon);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (DEBUG_MOD) LOGGER.info("Size of total number of cells: " + cellIdLats.size());
	}
	
	private static String getContent(InputStream input) throws IOException {
		byte [] b = new byte[1024];
		int readBytes = 0;
		StringBuilder sb = new StringBuilder();
		while ((readBytes = input.read(b)) >= 0) {
			sb.append(new String(b, 0, readBytes, "UTF-8"));
		}
		return sb.toString();
	}
	
	
	
	private CellUtils() {
	}
}
