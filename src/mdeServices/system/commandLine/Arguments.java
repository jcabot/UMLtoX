package mdeServices.system.commandLine;

import mdeServices.system.commandLine.ArgParser.ArgDesc;

/**
 *  Basic class for processing the command-line arguments
 * 
 * @version 0.1 25 Aug 2008
 * @author jcabot
 *
 */

public class Arguments {

	public enum ArgCode {USERNAME, MODEL}
	
	//Expected Structure for the arguments 
	public static ArgDesc[] description = 
		new ArgDesc[] {
			new ArgDesc("-user", ArgCode.USERNAME, 1) , new ArgDesc("-model", ArgCode.MODEL, 1)	};
	
	public static String modelFile = "";
	public static String userName ="";
	
	public static void processArgument(Object code, String[] params) {
		switch ((ArgCode)code) {
		case MODEL:
			modelFile = params[0]; //assigning the path of the modelFile
			break;
		case USERNAME:
			userName = params[0]; //assigning the path of the modelFile
		break;}
		}
	
	public static String getArgName(ArgCode code) {
		for (ArgDesc arg : description)
			if (arg.code.equals(code))
				return arg.name;
		return "";
	}
	
	public static String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append("modelFile = ").append(modelFile).append('\n');
		return sb.toString();
	}
}
