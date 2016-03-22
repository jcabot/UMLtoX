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

import mdeServices.metamodel.DT_String;

import mdeServices.metamodel.Enumeration;

import mdeServices.metamodel.Class;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.stereotypes.S_ForeignKey;



/**
 *  Generic Class for generating the VoltDB v1
 * 
 * @version 0.1 July 2010
 * @author jcabot
 *
 */
public class DBGeneratorVoltDB extends DBGenerator{

	
  
	protected void generateTriggers(Class c, PrintWriter out)
	{
		
	}

	//Drop sentences not yet supported in VoltDB
	protected void createDropScript(Vector<Class> allClasses, PrintWriter out)
	{
	}
	
	
   //AutoIncrement columns not supported
	protected String autoIncrement(Attribute pk, Attribute at, boolean isAuto)
	{
		return "";
	}
	
		
	protected String commentSymbol() { return "--";}
		
	protected String commentSymbolEnd() { return "";}
	
	public boolean isAutoIncrementPossible() {return false;}
	
	//A corregir
	public String getPHPDBConnection(String host, String dbName)
	{
      return "oci:dbname="+dbName +";host=" + host + ";";
	}
	
	//We must create a sequence and the corresponding insert trigger for each table 
	protected  void otherOperations(StaticModel m, Vector<Class> allClasses, PrintWriter out)
	{  
		
	};
	
	protected  String reduceLength(String str)
	{
	   if (str.length()>30)
		return str.substring(0, 29);	
	   else return str;
	}
	
    protected void onUpdateClause(PrintWriter out, S_ForeignKey s)
    {  //No update clause in VoltDB (FKs not supported)
       out.println(nestedString()) ;
    }
    
    protected void onDeleteClause(PrintWriter out, S_ForeignKey s)
    {
    	//No delete clause in VoltDB (FKs not supported)
        out.println(nestedString() + ";") ;
    }

    //FKs not yet supported
	protected void generateForeignKeys(Class c, PrintWriter out)
	{
	
	}
    
    
   //Boolean types not supported. CHECK constraints not supported. This means
    //we cannot really check that the column is of type boolean
	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
		return "TINYINT" ;
	}

	
	//No specific date type
	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "TIMESTAMP";
	}

	@Override
	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "TIMESTAMP";
	}

   //Check contraints not supported so we cannot restrict the enum column
	protected String getTypeInfoDT_Enumeration(Attribute a, Classifier d) {
		String type="";
		Enumeration e = (Enumeration) d;
		type="VARCHAR(" + e.getMaxLength() + ")";
		return type;
	}

	@Override
	protected String getTypeInfoDT_Integer(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "INTEGER";
	}

	@Override
	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		return "BIGINT";
	}

	@Override
	protected String getTypeInfoDT_LongReal(Attribute a, Classifier d) {
		return "DECIMAL";
	}

	@Override
	protected String getTypeInfoDT_Real(Attribute a, Classifier d) {
		return "FLOAT";
	}

	@Override
	protected String getTypeInfoDT_ShortInteger(Attribute a, Classifier d) {
		return "SHORTINT";
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
