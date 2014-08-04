package eu.threecixty.stats;

/**
 * This interface aims at easily changing class implementation to test.
 * @author Cong-Kinh Nguyen
 *
 */
public interface StatsStorage {

	/**
	 * Saves statistic information.
	 * @param stats
	 * @return <code>true</code> if the method successfully saves statistics, and <code>false</code> otherwise.
	 */
	boolean save(Stats stats);
}
