package eu.threecixty.privacy.store;

public interface User {

	public abstract String getId();

	public abstract byte[] getAuthenticator();

}