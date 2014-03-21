package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Infrastructure Route
 * @author Mobidot
 *
 */
public class InfraRoute {
	@Description(hasText="ID in terms of OSM, O if unknown")
	private int ID;
	@Description(hasText="route name")
	private String name;
	@Description(hasText="route type")
	private String type;
	@Description(hasText="route reference")
	private String routeReference;
	@Description(hasText="Intended modality for this route")
	private ModalityType routeModality;
	@Description(hasText="descrives het symbol that is used to mark the way along the route e.g. Red Cross")
	private String routeSymbol;
	@Description(hasText="the route is operated by this company")
	private String routeOperator;
	@Description(hasText="A wider network of routes of which this is an example")
	private String routeNetwork;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRouteReference() {
		return routeReference;
	}
	public void setRouteReference(String routeReference) {
		this.routeReference = routeReference;
	}
	public ModalityType getRouteModality() {
		return routeModality;
	}
	public void setRouteModality(ModalityType routeModality) {
		this.routeModality = routeModality;
	}
	public String getRouteSymbol() {
		return routeSymbol;
	}
	public void setRouteSymbol(String routeSymbol) {
		this.routeSymbol = routeSymbol;
	}
	public String getRouteOperator() {
		return routeOperator;
	}
	public void setRouteOperator(String routeOperator) {
		this.routeOperator = routeOperator;
	}
	public String getRouteNetwork() {
		return routeNetwork;
	}
	public void setRouteNetwork(String routeNetwork) {
		this.routeNetwork = routeNetwork;
	}
	
}
