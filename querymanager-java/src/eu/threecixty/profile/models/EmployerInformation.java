package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Employer History of the user
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class EmployerInformation extends EventDetail{
	@Description(hasText="Employer information")
	private Employer hasEmployer;
	@Description(hasText="Nature of Work done by the user")
    private NatureOfWork hasNatureOfWork;
	public Employer getHasEmployer() {
		return hasEmployer;
	}
	public void setHasEmployer(Employer hasEmployer) {
		this.hasEmployer = hasEmployer;
	}
	public NatureOfWork getHasNatureOfWork() {
		return hasNatureOfWork;
	}
	public void setHasNatureOfWork(NatureOfWork hasNatureOfWork) {
		this.hasNatureOfWork = hasNatureOfWork;
	}
	
	
}
