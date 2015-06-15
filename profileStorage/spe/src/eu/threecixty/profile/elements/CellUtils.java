package eu.threecixty.profile.elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CellUtils {
	

	private static final double DX = 234.431;
	private static final double DY = 234.431;
	private static final double HAF_OF_DX = DX / 2;
	private static final double HALF_OF_DY = DY / 2;
	private static final double CONV_FACTOR = (2.0 * Math.PI)/360.0;
	private static final int R = 6371;
	
	// The following constants are kept in lower case due to original version
	private static final double m1 = 111132.92;	// latitude calculation term 1
	private static final double m2 = -559.82;	// latitude calculation term 2
	private static final double m3 = 1.175;	// latitude calculation term 3
	private static final double m4 = -0.0023;	// latitude calculation term 4
	private static final double p1 = 111412.84;	// longitude calculation term 1
	private static final double p2 = -93.5;	// longitude calculation term 2
	private static final double p3 = 0.118;	// longitude calculation term 3


	
	
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
			double checkedLat = cellIdLats.get(cellId);
			double checkedLon = cellIdLons.get(cellId);
			if (isIntersects(orgLat, orgLon, distance, checkedLat, checkedLon)) {
				effectiveCellIds.add(cellId);
			}
		}
		return effectiveCellIds;
	}
	
	private static boolean isIntersects(double orgLat, double orgLon,
			double distance, double checkedLat, double checkedLon) {
		/*
		double x_closest = 0;

		if(Math.abs(orgLat - checkedLat) < latMeterToDeg(orgLat, HAF_OF_DX)) {
		x_closest = orgLat;
		} else {
		x_closest = checkedLat + latMeterToDeg(orgLat, HAF_OF_DX) * sign(orgLat - checkedLat);
		}


		double y_closest = 0;

		if(Math.abs(orgLon - checkedLon) < lonMeterToDeg(lat, dY/2)) {
		y_closest = lon;
		} else {
		y_closest = cell[0] + lonMeterToDeg(lat, dY/2) * sign(lon - cell[0]);
		}
		
		return false;
		*/
		return false;
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


/*
		var intersects = function (stringID, xOrg, yOrg, xCell, yCell, lat, lon, d) {
		var log  = '';

		var cell;


		if(cells[stringID] !=undefined) {
		cell = cells[stringID].split(" ");
		} else {
		//console.log("X: " + x_string + " & Y:" + y_string + " are not available");
		return;
		}
		if (cell.length < 2) return true;

		log += "Cell:" + cell;
		cell[0] = parseFloat(cell[0]);
		cell[1] = parseFloat(cell[1]);
		lat = parseFloat(lat);
		lon = parseFloat(lon);
		d = parseFloat(d);

		var x_closest = 0;

		//var xDist = getDistanceFromLatLonInMeter(lat, cell[0], cell[1], cell[0]);

		if(Math.abs(lat - cell[1]) < latMeterToDeg(lat, dX/2)) {
		x_closest = lat;
		} else {
		x_closest = cell[1] + latMeterToDeg(lat, dX/2) * sign(lat - cell[1]);
		}
		//console.log(Math.abs(lat - cell[1]) < latMeterToDeg(cell[1], d/2));


		var y_closest = 0;

		//var yDist = getDistanceFromLatLonInMeter(cell[1], lon, cell[1], cell[0]);


		if(Math.abs(lon - cell[0]) < lonMeterToDeg(lat, dY/2)) {
		y_closest = lon;
		} else {
		y_closest = cell[0] + lonMeterToDeg(lat, dY/2) * sign(lon - cell[0]);
		}
		//console.log(Math.abs(lon - cell[0]) < lonMeterToDeg(cell[1], d/2));



		log += "\nyLon: " + y_closest + " xLat: " + x_closest;
		    var calcutedDistance = getDistanceFromLatLonInMeter(x_closest, y_closest, lat, lon);
		var r = calcutedDistance < d; //(Math.pow(x_closest - lat, 2) + Math.pow(y_closest - lon, 2) < Math.pow(d,2));
		log += " " + r;
		log += "\nHaversineDist:" + getDistanceFromLatLonInMeter(x_closest, y_closest, lat, lon) + " d:" + d;

		//console.log(log);

		return r;
		};

		var add_intersecting_cell = function (xOrg, yOrg, lat, lon, d, relevant_cells) {
		xOrg = parseInt(xOrg);
		yOrg = parseInt(yOrg);



		var stepsX = Math.ceil(d / dX - 1);
		var stepsY = Math.ceil(d / dY - 1);

		var Xmin = (xOrg - stepsX - 1); Xmin = Math.max(Xmin,0);
		var Xmax = (xOrg + stepsX + 1); Xmax = Math.min(Xmax,100);
		var Ymin = (yOrg - stepsY - 1); Ymin = Math.max(Ymin,0);
		var Ymax = (yOrg + stepsY + 1); Ymax = Math.min(Ymax,100);

		var x_string = 0;
		var y_string = 0;


		var stringID = "0";

		for (var x = Xmin; x <= Xmax; x++) {
		x_string = "" + x;
		while (x_string.length < indent_length) { x_string = "0" + x_string; }

		for (var y = Ymin; y <= Ymax; y++) {
		y_string = "" + y;
		while (y_string.length < indent_length) { y_string = "0" + y_string; }
		stringID = "" + parseInt(x_string + y_string);

		if(!relevant_cells[stringID]) {
		if(x != xOrg || y != yOrg) { //(x == Xmin || x == Xmax || y == Ymin || y == Ymax) {
		if(intersects(stringID, xOrg, yOrg, x, y, lat, lon, d)) relevant_cells[stringID] = stringID;
		} else relevant_cells[stringID] = stringID;
		};
		};
		}

		return relevant_cells;
		};
		
		*/

	private static void initCellIDLatLons() {
		// TODO Auto-generated method stub
		cellIdLats = new HashMap <Integer, Double>();
		cellIdLons = new HashMap <Integer, Double>();
	}
	
	private CellUtils() {
	}
}
