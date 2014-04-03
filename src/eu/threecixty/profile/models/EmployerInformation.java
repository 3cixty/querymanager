package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Employer History of the user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class EmployerInformation {
	@Description(hasText="Employer information")
	private Employer hasEmployer;
	@Description(hasText="Start and end information about the event")
    private Information hasInformation;
	@Description(hasText="Nature of Work done by the user")
    private NatureOfWork isTheNatureOfWork;
	public Employer getHasEmployer() {
		return hasEmployer;
	}
	public void setHasEmployer(Employer hasEmployer) {
		this.hasEmployer = hasEmployer;
	}
	public Information getHasInformation() {
		return hasInformation;
	}
	public void setHasInformation(Information hasInformation) {
		this.hasInformation = hasInformation;
	}
	public NatureOfWork getIsTheNatureOfWork() {
		return isTheNatureOfWork;
	}
	public void setIsTheNatureOfWork(NatureOfWork isTheNatureOfWork) {
		this.isTheNatureOfWork = isTheNatureOfWork;
	}
	
	
}
