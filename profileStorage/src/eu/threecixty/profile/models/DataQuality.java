package eu.threecixty.profile.models;

/**
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.DataQuality
 * @author Mobidot
 *
 */
public enum DataQuality {
	Unknown,     
	Good,     
	Approximate,     
	Corrupt,     
	Missing,     
	CorruptTime,     
	Overlap,     
	Approved,     
	Bad;
}
