package mdeServices.system.commandLine;

import java.util.HashMap;

/**
 *  Basic class for managing a command line argument 
 * 
 * @version 0.1 25 Aug 2008
 * @author jcabot
 *
 */

public abstract class ArgParser {
	
	public static class InvalidArgument extends Exception {
		private static final long serialVersionUID = -7401927051888654896L;
		public String argument;
		public InvalidArgument(String argument) {
			this.argument = argument;
		}
	}
	
	public static class InvalidParametersNum extends Exception {
		private static final long serialVersionUID = 5800183912617187385L;
		public String argument;
		public InvalidParametersNum(String argument) {
			this.argument = argument;
		}
	}
	
	public static class ArgDesc {
		public String name;
		public Object code;
		public int numParams;
		public ArgDesc(String name, Object code, int numParams) {
			this.name = name;
			this.code = code;
			this.numParams = numParams;
		}
	}

	private HashMap<String, ArgDesc> argDescMap =
		new HashMap<String, ArgDesc>();

	public ArgParser(ArgDesc[] descs) {
		for (ArgDesc desc : descs)
			argDescMap.put(desc.name, desc);
	}
	
	public void parse(String cmdLine) 
		throws InvalidArgument, InvalidParametersNum {
		
		parse(cmdLine.split(" "));
	}
	
	public void parse(String[] args) 
		throws InvalidArgument, InvalidParametersNum {
		
		int idx = 0;
		while (idx < args.length) {
			
			String tag = args[idx++];
			ArgDesc arg = argDescMap.get(tag);
			
			 if (arg == null)
				throw new InvalidArgument(tag);
			
			if (args.length - idx < arg.numParams)
				throw new InvalidParametersNum(tag);
			
			String[] params = new String[arg.numParams];
			for (int i = 0; i < arg.numParams; i++)
				params[i] = args[idx++];
			
			processArgument(arg.code, params);
		}
	}

	protected abstract void processArgument(Object code, String[] params);
}
