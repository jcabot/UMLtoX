package mdeServices.xmi.importXMI;

import java.util.Iterator;

import mdeServices.metamodel.DataType;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.ModelFactory;

public class ImportXMIArgoUML_v028 extends ImportXMIArgoUML{

	
	public void importExternalDataTypes(){
	//hard coded list of external data types used in the uml and java profiles of argouml
		
		//uml14 profile
		DataType aux=ModelFactory.createDataType("Integer");
		hash.put("-84-17--56-5-43645a83:11466542d86:-8000:000000000000087C", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("UnlimitedInteger");
		hash.put("-84-17--56-5-43645a83:11466542d86:-8000:000000000000087D", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("String");
		hash.put("-84-17--56-5-43645a83:11466542d86:-8000:000000000000087E", aux);
		m.addDataType(aux);
		
		//Java profile
		aux=ModelFactory.createDataType("Object");
		hash.put(".:0000000000000850", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Char");
		hash.put(".:0000000000000851" , aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Byte");
		hash.put(".:0000000000000852", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Boolean");
		hash.put(".:0000000000000853", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Short");
		hash.put(".:0000000000000854", aux);
		m.addDataType(aux);
				
		aux=ModelFactory.createDataType("Integer");
		hash.put(".:0000000000000855", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Long");
		hash.put(".:0000000000000856", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Float");
		hash.put(".:0000000000000857", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Double");
		hash.put(".:0000000000000858", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("String");
		hash.put(".:0000000000000859", aux);
		m.addDataType(aux);
	
		aux=ModelFactory.createDataType("Date");
		hash.put(".:000000000000085F", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("Time");
		hash.put(".:0000000000000860", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("BigDecimal");
		hash.put(".:0000000000000864", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("BigInteger");
		hash.put(".:0000000000000865", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("void");
		hash.put(".:000000000000086B", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("int");
		hash.put(".:000000000000086C", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("short");
		hash.put(".:000000000000086D", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("long");
		hash.put(".:000000000000086E", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("double");
		hash.put(".:000000000000086F", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("float");
		hash.put(".:0000000000000870", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("char");
		hash.put(".:0000000000000871", aux);
		m.addDataType(aux);
	
		aux=ModelFactory.createDataType("byte");
		hash.put(".:0000000000000872", aux);
		m.addDataType(aux);
		
		aux=ModelFactory.createDataType("boolean");
		hash.put(".:0000000000000873", aux);
		m.addDataType(aux);
	
		  
	};
	
	public void importExternalEnumerations(){
		Enumeration aux=new Enumeration("Boolean");
		aux.addValue("TRUE");
		aux.addValue("FALSE");
		hash.put("-84-17--56-5-43645a83:11466542d86:-8000:0000000000000880", aux);
		m.addDataType(aux);
	};
	
}
