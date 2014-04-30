package eu.threecixty.privacy.semantic;

/**
 * An entity whose semantic meaning can be narrowed down or expanded.
 */
public interface Scope extends Entity {

    /**
     * The parent scope of all scopes
      */
    public static final Scope ALL = new Scope() {

        public String getOntologyURL() {
            return "http://org.theresis.cimst/semantics";
        }

        public String getEntityAsString() {
            return "http://org.theresis.cimst/semantics#Scope|ALL";
        }

        public String getEntityIDAsString() {
            return "ALL";
        }

        public String getEntityTypeAsString() {
            return "http://org.theresis.cimst/semantics#Scope";
        }

        public String getEntityTypeAsShortString() {
            return "Scope";
        }

        public boolean isParentScope(Scope possibleParentScope) {
            return false;
        }

        public boolean isChildScope(Scope possibleChildScope) {
            return true;
        }};
    

    /**
     * Checks if the specified scope can be considered as a parent scope 
     * of this scope. For example, the scope "http://someURL#SocialNetwork" 
     * is a parent scope of "http://someURL#Facebook".
     *
     * @param possibleParentScope the scope which is checked as parent of this scope.
      */
    public boolean isParentScope(Scope possibleParentScope);

    /**
     * Checks if this specified scope can be considered as a child scope 
     * of this scope. For example, the scope "http://someURL#DominoPizza" 
     * is a child scope of "http://someURL#Pizzaria".
     *
     * @param possibleChildScope the scope which is checked as a child of this scope.
     */
    public boolean isChildScope(Scope possibleChildScope);

}
