package mdeServices.transformations;


/**
 *  Exception raised when the requested transformation cannot be applied to a mode
 *  (this is only raised when there is an error, not when the transformation does not
 *  find any model subset where to be applied)
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class TransformationNotApplicable extends Exception {

		/**
		 * @param arg0
		 */
		public TransformationNotApplicable(String arg0) {
			super(arg0);
			
		}
		
		
		
	}

