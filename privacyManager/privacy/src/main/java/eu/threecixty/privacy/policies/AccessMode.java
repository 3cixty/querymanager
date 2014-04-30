package eu.threecixty.privacy.policies;

public enum AccessMode {
	NONE,
	READ_ONLY,
	WRITE_ONLY,
	READ_WRITE,
	READ_ALWAYS_WRITE_ONCE;

	public boolean canRead() {
		return (this != NONE) && (this != WRITE_ONLY); // others enum have read access
	}

	public boolean canWrite() {
		return (this != NONE) && (this != READ_ONLY); // others enum have write access
	}
}
