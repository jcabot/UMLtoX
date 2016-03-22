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

import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_String;

import mdeServices.metamodel.Enumeration;

import mdeServices.metamodel.Class;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.stereotypes.E_ForeignKeyEventKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;



/**
 *  Class for generating the database Microsoft SQL Server 2006
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */
public class DBGeneratorSQLServer extends DBGenerator{

	
    //Generation of the multiplicity triggers for each class 
	//not yet implemented
	protected void generateTriggers(Class c, PrintWriter out)
	{

		
	}

	protected void createDropScript(Vector<Class> allClasses, PrintWriter out) throws DBGenerationException
	{
		Vector<Classifier> cl= orderClasses(allClasses);
		//Creation of the initial drop sentence
		Iterator<Classifier> itDrop= cl.iterator();
		String names=""; 
		while (itDrop.hasNext())
		{
		   createDropTableScript( (Class) itDrop.next(), out);	
			
		}
		out.println();
	}
	
	protected void createDropTableScript(Class c, PrintWriter out)
	{		    
	  	    out.println(nestedString() + "DROP TABLE " + c.getTableName() + ";");
	}
	
   //AutoIncrement columns in Oracle are implemented by means of sequence constructs. See the otherOperations function
	protected String autoIncrement(Attribute pk, Attribute at, boolean isAuto)
	{
		  String auto="";
		  if (isAuto) 
		  {
			if (pk==at) auto= "IDENTITY(1,1)";
		  }
			 
		  return auto;
	}
	
	
	
	protected String commentSymbol() { return "--";}
	
	protected String commentSymbolEnd() { return "";}
	
	public boolean isAutoIncrementPossible() {return true;}
	
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
	  return str;
	}
	
	protected String onUpdate(S_ForeignKey s)
	{
		if (s.getOnUpdate()== E_ForeignKeyEventKind.E_RESTRICT) 
			return "NO ACTION";
		else return s.getOnUpdate().toString();
	}
	
	protected String onDelete(S_ForeignKey s)
	{
		if (s.getOnDelete()== E_ForeignKeyEventKind.E_RESTRICT) 
			return "NO ACTION";
		else return s.getOnDelete().toString();
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

	@Override
	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
		//Boolean attributes are mapped to bit integers (0 is assumed to be false and 1 to be true);
		return "BIT" ;
	}

	@Override
	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		//The date time has been introduced in the SQL Server 2008 version
		return "DATETIME";
	}

	@Override
	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		//The date time has been introduced in the SQL Server 2008 version
		return "DATETIME";
	}

	@Override
	protected String getTypeInfoDT_Enumeration(Attribute a, Classifier d) {
		String type="";
		Enumeration e = (Enumeration) d;
		type="VARCHAR(" + e.getMaxLength() + ")";
		type= type + " CONSTRAINT " + o.getProperty("db.constraint.ck.prefix") + a.getName() +  o.getProperty("db.constraint.ck.enum.name") +  
	      o.getProperty("db.constraint.ck.suffix")+ " CHECK(" + a.getName()+ " IN (";
		Iterator<String> itS=e.getValues().iterator();
		while (itS.hasNext()) {
			String string = (String) itS.next();
			type=type+ "'" + string + "'";
			if (itS.hasNext()) type=type+",";
		}
		type=type+ "))";
		return type;
	}

	@Override
	protected String getTypeInfoDT_Integer(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "INT";
	}

	@Override
	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "BIGINT";
	}

	@Override
	protected String getTypeInfoDT_LongReal(Attribute a, Classifier d) {
		String type="";
		DT_LongReal dR = (DT_LongReal ) d;
		type="DECIMAL(" + dR.getLength() + "," + dR.getPrecision() + ")";
		return type;
	}

	@Override
	protected String getTypeInfoDT_Real(Attribute a, Classifier d) {
		String type="";
		DT_Real dR = (DT_Real ) d;
		type="DECIMAL(" + dR.getLength() + "," + dR.getPrecision() + ")";
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

	
}
