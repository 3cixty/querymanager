package eu.threecixty.privacy.store;

public interface User {

	public abstract Long getUserId();

	public abstract String getName();

	public abstract byte[] getAuthenticator();

}