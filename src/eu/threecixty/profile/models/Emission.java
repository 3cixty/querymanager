package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Specific amount of the specified type of substance that has omitted in a given trip, timeframe,...
 * @author Mobidot, edited by Rachit.Agarwal@inria.fr
 *
 */
public class Emission {
	@Description(hasText = "Unique emission ID")
	private int ID;
	@Description(hasText = "Type of emitted substance")
	private Substance hasSubstance;
	@Description(hasText = "Amount emitted, in a unit that is appropriate for the specified emission type, eg."
			+ "gram for CO2")
	private Number amount;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public Substance getHasSubstance() {
		return hasSubstance;
	}
	public void setHasSubstance(Substance hasSubstance) {
		this.hasSubstance = hasSubstance;
	}
	public Number getAmount() {
		return amount;
	}
	public void setAmount(Number amount) {
		this.amount = amount;
	}
	
}
