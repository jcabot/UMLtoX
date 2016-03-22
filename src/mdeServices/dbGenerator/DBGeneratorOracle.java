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
 *  Generic Class for generating the Oracle database 10g
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */
public class DBGeneratorOracle extends DBGenerator{

	
    //Generation of the multiplicity triggers for each class 
	//not implemented in Oracle (mutating tables problem)
	protected void generateTriggers(Class c, PrintWriter out)
	{
		
	}

	protected void createDropScript(Vector<Class> allClasses, PrintWriter out)
	{
		//Creation of the initial drop sentence
		Iterator<Class> itDrop= allClasses.iterator();
		String names=""; 
		while (itDrop.hasNext())
		{
			out.println(nestedString() + "DROP TABLE " + itDrop.next().getTableName() + " CASCADE CONSTRAINTS;");
		
		}
			out.println();
	}
	
	
   //AutoIncrement columns in Oracle are implemented by means of sequence constructs. See the otherOperations function
	protected String autoIncrement(Attribute pk, Attribute at, boolean isAuto)
	{
		return "";
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
		out.println(nestedString() + commentSymbol() + l.getString("heading.ddl.sequence")+commentSymbolEnd());	 
		boolean auto;
		Iterator<Class> itCl= allClasses.iterator();
		while (itCl.hasNext())
		{
		  Class cl=itCl.next();
		  auto= cl.isPKAutoIncrement();
		  if (auto)
		  {
			  //creation of the sequence 
			  String seqName=o.getProperty("db.sequence.prefix") + cl.getTableName() +  o.getProperty("db.sequence.suffix");
			  out.println(nestedString() + "DROP SEQUENCE " +  seqName+ ";");	 
			  out.println(nestedString() + "CREATE SEQUENCE " +  seqName +  " START WITH 1 INCREMENT BY 1 NOMAXVALUE;" );	 
			  
			  //cretion of the before insert trigger
			  out.println(nestedString() + "CREATE TRIGGER " + o.getProperty("db.table.triggers.prefix")+ o.getProperty("db.table.triggers.sequence.prefix")+cl.getTableName() +
					o.getProperty("db.table.triggers.sequence.suffix")+	o.getProperty("db.table.triggers.suffix"));
				
				out.println(nestedString() + "BEFORE INSERT ON " + cl.getTableName() );
				out.println(nestedString() + "FOR EACH ROW");
				out.println(nestedString() + "BEGIN");
				incNested();
				out.println(nestedString() + "IF :new."+ cl.getPrimaryKeyAttribute().getName() +  " IS NULL THEN");
				incNested();
				out.println(nestedString() + "SELECT " + seqName + ".nextval INTO :new." +  cl.getPrimaryKeyAttribute().getName() +   " FROM dual;");
				decNested();
				out.println(nestedString() + "END IF;"); 
				decNested();
				out.println(nestedString() + "END;");
				out.println(nestedString() + "/");
				out.println();
		  }
		}
	};
	
	protected  String reduceLength(String str)
	{
	   if (str.length()>30)
		return str.substring(0, 29);	
	   else return str;
	}
	
    protected void onUpdateClause(PrintWriter out, S_ForeignKey s)
    {  //No update clause in Oracle
    }
    
    protected void onDeleteClause(PrintWriter out, S_ForeignKey s)
    {  //No delete clause in Oracle
       out.println(nestedString() + ";") ;
    }


	@Override
	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "NUMBER(1) CONSTRAINT " + o.getProperty("db.constraint.ck.prefix") + a.getName() +  o.getProperty("db.constraint.ck.boolean.name") +  
	      o.getProperty("db.constraint.ck.suffix")+ " CHECK(" + a.getName() + "=0 OR " +a.getName() + "=1)" ;

	}

	
	@Override
	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "DATE";
	}

	@Override
	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "TIMESTAMP";
	}

	@Override
	protected String getTypeInfoDT_Enumeration(Attribute a, Classifier d) {
		String type="";
		Enumeration e = (Enumeration) d;
		type="VARCHAR2(" + e.getMaxLength() + ")";
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
		String type="";
		DT_Integer dI = (DT_Integer) d;
		type="NUMBER(" + dI.getLength() + ")";
		return type;
	}

	@Override
	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		String type="";
		DT_LongInteger dI = (DT_LongInteger) d;
		type="NUMBER(" + dI.getLength() + ")";
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	protected String getTypeInfoDT_LongReal(Attribute a, Classifier d) {
		String type="";
		DT_LongReal dR = (DT_LongReal ) d;
		type="NUMBER(" + dR.getLength() + "," + dR.getPrecision() + ")";
		return type;
	}

	@Override
	protected String getTypeInfoDT_Real(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		String type="";
		DT_Real dR = (DT_Real ) d;
		type="NUMBER(" + dR.getLength() + "," + dR.getPrecision() + ")";
		return type;
	}

	
	@Override
	protected String getTypeInfoDT_String(Attribute a, Classifier d) {
		String type="";
		DT_String dS = (DT_String) d;
		boolean fixLength=dS.getFixLength().booleanValue();
		if (fixLength) type="CHAR(" + dS.getLength() + ")";
		else type="VARCHAR2(" + dS.getLength() + ")";
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
