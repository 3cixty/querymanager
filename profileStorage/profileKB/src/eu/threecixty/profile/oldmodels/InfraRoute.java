package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Infrastructure Route
 * @author Mobidot
 *
 */
public class InfraRoute {
	@Description(hasText="ID in terms of OSM, O if unknown")
	private int hasInfraRouteID;
	@Description(hasText="route name")
	private String hasInfraRouteName;
	@Description(hasText="route type")
	private String hasInfraRouteType;
	@Description(hasText="route reference")
	private String hasInfraRouteReference;
	@Description(hasText="Intended modality for this route")
	private ModalityType hasModalityType;
	@Description(hasText="describes the symbol that is used to mark the way along the route e.g. Red Cross")
	private String hasInfraRouteSymbol;
	@Description(hasText="the route is operated by this company")
	private String hasInfraRouteOperator;
	@Description(hasText="A wider network of routes of which this is an example")
	private String hasInfraRouteNetwork;
	public int getHasInfraRouteID() {
		return hasInfraRouteID;
	}
	public void setHasInfraRouteID(int hasInfraRouteID) {
		this.hasInfraRouteID = hasInfraRouteID;
	}
	public String getHasInfraRouteName() {
		return hasInfraRouteName;
	}
	public void setHasInfraRouteName(String hasInfraRouteName) {
		this.hasInfraRouteName = hasInfraRouteName;
	}
	public String getHasInfraRouteType() {
		return hasInfraRouteType;
	}
	public void setHasInfraRouteType(String hasInfraRouteType) {
		this.hasInfraRouteType = hasInfraRouteType;
	}
	public String getHasInfraRouteReference() {
		return hasInfraRouteReference;
	}
	public void setHasInfraRouteReference(String hasInfraRouteReference) {
		this.hasInfraRouteReference = hasInfraRouteReference;
	}
	public ModalityType getHasModalityType() {
		return hasModalityType;
	}
	public void setHasModalityType(ModalityType hasModalityType) {
		this.hasModalityType = hasModalityType;
	}
	public String getHasInfraRouteSymbol() {
		return hasInfraRouteSymbol;
	}
	public void setHasInfraRouteSymbol(String hasInfraRouteSymbol) {
		this.hasInfraRouteSymbol = hasInfraRouteSymbol;
	}
	public String getHasInfraRouteOperator() {
		return hasInfraRouteOperator;
	}
	public void setHasInfraRouteOperator(String hasInfraRouteOperator) {
		this.hasInfraRouteOperator = hasInfraRouteOperator;
	}
	public String getHasInfraRouteNetwork() {
		return hasInfraRouteNetwork;
	}
	public void setHasInfraRouteNetwork(String hasInfraRouteNetwork) {
		this.hasInfraRouteNetwork = hasInfraRouteNetwork;
	}
	
	
}
