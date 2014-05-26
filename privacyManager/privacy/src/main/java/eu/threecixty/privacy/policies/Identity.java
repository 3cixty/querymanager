package eu.threecixty.privacy.policies;

import java.io.Serializable;

import eu.threecixty.privacy.model.DefaultModelFactory;
import eu.threecixty.privacy.semantic.Model;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.storage.Credential;

/*package*/ class Identity implements Credential, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -383558049957708708L;
	private Serializable identityToken;

	/**
	 * @param identity
	 *            Something that will uniquely identity the owner of the data
	 *            created with this policy. Must not be null.
	 */
	public Identity(Serializable identity) {
		if (null == identity)
			throw new NullPointerException("The identity token must not be null");
		
		this.identityToken = identity;
	}

	public Scope getSubject() {
		String ontology = "http://id@" + identityToken;
		Model model = new DefaultModelFactory().newModel(ontology);
		return model.newScope("global");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identityToken == null) ? 0 : identityToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Identity other = (Identity) obj;
		if (identityToken == null) {
			if (other.identityToken != null)
				return false;
		} else if (!identityToken.equals(other.identityToken))
			return false;
		return true;
	}

}