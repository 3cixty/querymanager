package eu.threecixty.privacy.store;

public interface Value {

	public abstract Long getId();

	public abstract String getOntology();

	public abstract long getUserId();

	public abstract String getResource();

	public abstract String getProvider();

}