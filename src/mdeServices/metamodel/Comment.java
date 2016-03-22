/**
 * 
 */
package mdeServices.metamodel;


/**
 *  Class representing comments on model elements
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class Comment extends ModelElement{
	protected String text;

	/**
	 * @param name
	 * @param text
	 */
	public Comment(String name) {
		super(name);
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/*public Comment clone()
	{
		Comment c= new Comment(this.name);
		copyTo(c);
		return c;
	}*/

	public void replaceWith(Comment c)
	{
		super.replaceWith(c);
		c.text=this.text;
	
	}
	
}
