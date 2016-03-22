package mdeServices.metamodel.database;

import mdeServices.metamodel.ModelElement;

/**
 * Class for representing the triggers that may be attached to persistent classes 
 * 
 * @author jcabot
 *
 */

public class Trigger extends ModelElement{
	protected TriggerEventKind event;
	protected TriggerMomentKind moment;
	
	/**
	 * @param name
	 */
	public Trigger(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
	 * @return the event
	 */
	public TriggerEventKind getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(TriggerEventKind event) {
		this.event = event;
	}

	/**
	 * @return the moment
	 */
	public TriggerMomentKind getMoment() {
		return moment;
	}

	/**
	 * @param moment the moment to set
	 */
	public void setMoment(TriggerMomentKind moment) {
		this.moment = moment;
	}

	
	

}
