package eu.threecixty.querymanager;

/**
 * Utility class.
 * @author Cong-Kinh NGUYEN
 *
 */
public class GpsCoordinateUtils {

	private static final int EARTH_RADIUS = 6371;

	/**
	 * Calculates GPS coordinates information. The information is in radian.
	 * <br><br>
	 * The formula to calculate is taken from http://www.movable-type.co.uk/scripts/latlong.html
	 * @param point
	 * @param distance
	 * @param bearing
	 * @return
	 */
	public static GpsCoordinate calc(GpsCoordinate point, double distance, double bearing) {
		double ratio = distance / EARTH_RADIUS;
		double lat2 = Math.asin(Math.sin(point.lat) * Math.cos(ratio) + Math.cos(point.lat) * Math.sin(ratio) * Math.cos(bearing));
		double lon2 = point.getLongitude() + Math.atan2(Math.sin(bearing) * Math.sin(ratio) * Math.cos(point.lat),
				Math.cos(ratio) - Math.sin(point.lat) * Math.sin(lat2));
		return new GpsCoordinate(lat2, lon2);
	}

	/**
	 * Converts GPS coordinates information to object.
	 * @param latitudeInDegree
	 * @param longitudeInDegree
	 * @return
	 */
	public static GpsCoordinate convert(double latitudeInDegree, double longitudeInDegree) {
		return new GpsCoordinate(Math.toRadians(latitudeInDegree), Math.toRadians(longitudeInDegree));
	}

	/**
	 * Gets longitude in degree from GPS coordinates information. 
	 * @param point
	 * @return
	 */
	public static double getLogitudeInDegree(GpsCoordinate point) {
		return Math.toDegrees(point.lon);
	}

	/**
	 * Gets latitude in degree from GPS coordinates information.
	 * @param point
	 * @return
	 */
	public static double getLatitudeInDegree(GpsCoordinate point) {
		return Math.toDegrees(point.lat);
	}

	private GpsCoordinateUtils() {
	}

	public static class GpsCoordinate {
		private double lat;
		private double lon;

		public GpsCoordinate(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		public double getLatitude() {
			return lat;
		}

		public double getLongitude() {
			return lon;
		}
	}
}


