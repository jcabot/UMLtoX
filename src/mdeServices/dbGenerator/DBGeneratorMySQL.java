/**
 * 
 */
package mdeServices.dbGenerator;


import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import mdeServices.DBGenerator;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Classifier;

import mdeServices.metamodel.DT_Integer;
import mdeServices.metamodel.DT_LongInteger;
import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_String;

import mdeServices.metamodel.Enumeration;

import mdeServices.metamodel.Class;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.stereotypes.E_ForeignKeyEventKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;



/**
 *  Class for generating the database MySQL 5.1
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */
public class DBGeneratorMySQL extends DBGenerator{

	protected void createDropScript(Vector<Class> allClasses, PrintWriter out)
	{
		//Creation of the initial drop sentence
		Iterator<Class> itDrop= allClasses.iterator();
		String names=""; 
		while (itDrop.hasNext())
		{
			names= names + itDrop.next().getTableName();
			if (itDrop.hasNext()) names=names+",";
		}
		//The set foreign_key_checks allows us to drop the tables in any order, avoiding errors due to dangling foreing keys
		out.println(nestedString() + "SET FOREIGN_KEY_CHECKS=0;");
		out.println(nestedString() + "DROP TABLE IF EXISTS " + names + ";");
		out.println(nestedString() + "SET FOREIGN_KEY_CHECKS=1;");
		out.println();
	}
	
	
    //Generation of the multiplicity triggers for each class 
	//not implemented in MySQL (no possibility of stopping the trigger execution)
	protected void generateTriggers(Class c, PrintWriter out)
	{
		
	}
	
	
	protected String autoIncrement(Attribute pk, Attribute at, boolean isAuto)
	{
	  String auto="";
	  if (isAuto) 
	  {
		if (pk==at) auto= "AUTO_INCREMENT";
	  }
		 
	  return auto;
	}
	
	
	
	protected String commentSymbol() { return "--";}
	
	protected String commentSymbolEnd() { return "";}
		
	public boolean isAutoIncrementPossible() {return true;}
	
	public String getPHPDBConnection(String host, String dbName)
	{
      return "mysql:host=" + host + ";dbname="+dbName;
	}
	
	//No additional operations required in MySQL
	protected  void otherOperations(StaticModel m, Vector<Class> allClasses, PrintWriter out){};
	
	//No length restrictions for MySQL
	protected  String reduceLength(String str)
	{
	  return str;	
	}
	
	// The default option is not supported, we return "RESTRICT" instead
	protected String onUpdate(S_ForeignKey s)
	{
		if (s.getOnUpdate()== E_ForeignKeyEventKind.E_DEFAULT) 
			return "RESTRICT";
		else return s.getOnUpdate().toString();
	}
	
	protected String onDelete(S_ForeignKey s)
	{
		if (s.getOnDelete()== E_ForeignKeyEventKind.E_DEFAULT) 
			return "RESTRICT";
		else return s.getOnDelete().toString();
	}


	@Override
	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "TINYINT(1)";
	}


	
	@Override
	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "DATE";
	}


	@Override
	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "DATETIME";
	}


	@Override
	protected String getTypeInfoDT_Enumeration(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		String type="";
		Enumeration e = (Enumeration) d;
		type="ENUM(";
		Iterator<String> itS=e.getValues().iterator();
		while (itS.hasNext()) {
			String string = (String) itS.next();
			type=type+ "'" + string + "'";
			if (itS.hasNext()) type=type+",";
		}
		type=type+ ")";
		return type;
	}


	@Override
	protected String getTypeInfoDT_Integer(Attribute a, Classifier d) {
		String type="";
		DT_Integer dI = (DT_Integer) d;
		type="INTEGER(" + dI.getLength() + ")";
		return type;
	}


	@Override
	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		String type="";
		DT_LongInteger dI = (DT_LongInteger) d;
		type="BIGINT(" + dI.getLength() + ")";
		return type;
	}


	@Override
	protected String getTypeInfoDT_LongReal(Attribute a, Classifier d) {
		String type="";
		DT_LongReal dR = (DT_LongReal ) d;
		type="DOUBLE(" + dR.getLength() + "," + dR.getPrecision() + ")";
		// TODO Auto-generated method stub
		return type;
	}


	@Override
	protected String getTypeInfoDT_Real(Attribute a, Classifier d) {
		String type="";
		DT_Real dR = (DT_Real ) d;
		type="FLOAT(" + dR.getLength() + "," + dR.getPrecision() + ")";
		return type;
	}
	


	@Override
	protected String getTypeInfoDT_String(Attribute a, Classifier d) {
		String type="";
		DT_String dS = (DT_String) d;
		boolean fixLength=dS.getFixLength().booleanValue();
		if (fixLength) type="CHAR(" + dS.getLength() + ")";
		else type="VARCHAR(" + dS.getLength() + ")";
		return type;
	}

	//So far we do not deal with unsigned
	@Override
	protected String getTypeInfoDT_UnsignedInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_Integer(a,d);
	}

	@Override
	protected String getTypeInfoDT_UnsignedLongInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_LongInteger(a,d);
	}

	@Override
	protected String getTypeInfoDT_UnsignedLongReal(Attribute a, Classifier d) {
		return getTypeInfoDT_LongReal(a,d);
	}

	@Override
	protected String getTypeInfoDT_UnsignedReal(Attribute a, Classifier d) {
		return getTypeInfoDT_Real(a,d);
	}
	
	@Override
	protected String getTypeInfoDT_ShortInteger(Attribute a, Classifier d) {
		return getTypeInfoDT_Integer(a,d);
	}

	@Override
	protected String getTypeInfoDT_UnsignedShortInteger(Attribute a,
			Classifier d) {
		return getTypeInfoDT_ShortInteger(a,d);
	}
		
	@Override
	protected String getTypeInfoDT_Currency(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_Real(a,d);
	}

	@Override
	protected String getTypeInfoDT_Char(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_String(a,d);
	}

}
