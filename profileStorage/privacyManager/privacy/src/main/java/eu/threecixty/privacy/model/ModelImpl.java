package eu.threecixty.privacy.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import eu.threecixty.privacy.semantic.Entity;
import eu.threecixty.privacy.semantic.Model;
import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.semantic.Statement;

class ModelImpl implements Model, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7249568745200451562L;

	private final String ontologyURL;
	
    private final transient HashSet<Entity> entities = new HashSet<Entity>();

    public ModelImpl(String ontologyURL) {
        this.ontologyURL = ontologyURL;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public Entity newEntity(String entity) {
        Entity e = new EntityImpl(entity);
        entities.add(e);
        return e;
    }

    public Scope newScope(String scope) {
        Scope s = new ScopeImpl(scope);
        entities.add(s);
        return s;
    }

    public <V> Resource<V> newResource(String resource, Class<V> type) {
        Resource<V> r = new ResourceImpl<V>(resource);
        entities.add(r);
        return r;
    }

    public Statement newStatement(String statement) {
        Statement s = new StatementImpl(statement);
        entities.add(s);
        return s;
    }

    class EntityImpl implements Entity, Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1704882889787961927L;
		
		private final String type;
        private final String id;

        public EntityImpl(String entity) {
            int i = entity.indexOf('|');
            if (i >= 0) {
                id = entity.substring(i + 1);
                type = entity.substring(0, i);
            } else {
                id = null;
                type = entity;
            }
        }

        public String getOntologyURL() {
            return ontologyURL;
        }

        public String getEntityAsString() {
            return ontologyURL + "#" + type + ((id != null) ? ("|" + id) : "");
        }

        public String getEntityIDAsString() {
            return id;
        }

        public String getEntityTypeAsString() {
            return ontologyURL + "#" + type;
        }

        public String getEntityTypeAsShortString() {
            return type;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Entity)
                return ((Entity) obj).getEntityAsString().equals(
                        this.getEntityAsString());
            return false;
        }

        public int hashCode() {
            return this.getEntityAsString().hashCode();
        }

        public String toString() {
        	return this.getEntityAsString();
        }
    }

    class ScopeImpl extends EntityImpl implements Scope, Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = -3772766551623907378L;

		public ScopeImpl(String entity) {
            super(entity);
        }

        public boolean isParentScope(Scope possibleParentScope) {
            if (possibleParentScope == ALL)
                return true;
            return false;
        }

        public boolean isChildScope(Scope possibleChildScope) {
            return false;
        }

    }

    class StatementImpl extends EntityImpl implements Statement, Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = 3054269562539193313L;

		public StatementImpl(String entity) {
            super(entity);
        }
    }

    class ResourceImpl<V> extends EntityImpl implements Resource<V>, Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = 6828794806591365756L;

		public ResourceImpl(String entity) {
            super(entity);
        }
    }

}