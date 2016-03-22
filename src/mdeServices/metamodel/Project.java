package mdeServices.metamodel;

import java.util.Date;
import java.util.UUID;
import java.util.Vector;

import mdeServices.metamodel.gui.DynamicModel;

/**
 * Class representing the full project created by the user
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class Project {
	protected UUID id;
	protected String user;
	protected String name;
	protected String description;
	protected String language;
	protected StaticModel stModel;
	protected DynamicModel dyModel;
	/** created is automatically initialized when creating the project instance*/
	protected Date created;
	
	
	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param language
	 * @param model
	 * @param created
	 */
	public Project(String name, String user, String description, String language) {
		super();
		this.id = java.util.UUID.randomUUID();
		this.name = name;
		this.description = description;
		this.language = language;
		this.created = new Date(); //this initializes the attribute with the current date
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
	 * @return the models
	 */
	public StaticModel getStaticModel() {
		return stModel;
	}

	/**
	 * @param models the models to set
	 */
	public void setStaticModel(StaticModel model) {
		this.stModel = model;
		this.stModel.setProject(this);
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the dyModel
	 */
	public DynamicModel getDynamicModel() {
		return dyModel;
	}

	/**
	 * @param dyModel the dyModel to set
	 */
	public void setDynamicModel(DynamicModel dyModel) {
		this.dyModel = dyModel;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

}
