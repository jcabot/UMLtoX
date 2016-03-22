package mdeServices.metamodel;

import java.util.UUID;
import java.util.Vector;

public abstract class Model {
	
		/**Internal identifier of the model */
		protected UUID id; 
		/**Name of the model */
		protected String name; 
		/**Model description */
		protected String description; 
		/**Project where the model belongs */
		protected Project project;
		/**Models refining this model */
		protected Vector<Model> refinements;
		
		
		
		/**
		 * @param id
		 * @param name
		 * @param description
		 */
		public Model(String name) {
			super();
			this.id = java.util.UUID.randomUUID();
			this.name = name;
			this.description = "";
		}

		/**
		 * @return the id
		 */
		public UUID getId() {
			return id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * @return the project
		 */
		public Project getProject() {
			return project;
		}

		/**
		 * @param project the project where the model belongs
		 */
		public void setProject(Project project) {
			this.project = project;
		}

		/**
		 * @return the models
		 */
		public Vector<Model> getModels() {
			return refinements;
		}

		/**
		 * @param models the models to set
		 */
		public void setModels(Vector<Model> refinements) {
			this.refinements = refinements;
		}
		
		/**
		 * @param models the models to set
		 */
		public void addModel(Model model) {
			this.refinements.add(model);
		}
		
		
		public boolean hasName()
		{
			boolean hasName=false;
			if (this.name!=null && this.name!="") hasName=true;
			return hasName;
		}

		
		
	
}
