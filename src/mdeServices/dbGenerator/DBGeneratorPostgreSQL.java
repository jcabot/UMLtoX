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
import mdeServices.metamodel.stereotypes.E_ForeignKeyEventKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;



/**
 *  Class for generating Postgresql 8.2 database scripts
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */
public class DBGeneratorPostgreSQL extends DBGenerator{

	
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
			out.println(nestedString() + "DROP TABLE IF EXISTS " + itDrop.next().getTableName() + " CASCADE;");
		
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
		
		//We may need to add the following explanation
		//out.println(nestedString() + "-- Creation of triggers assume that plpgsql language is already created in the database");
		//out.println(nestedString() + "-- Otherwise uncomment the following sentence" );
		//out.println(nestedString() + "--CREATE LANGUAGE plpgsql"); 
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
			  out.println(nestedString() + "DROP SEQUENCE IF EXISTS " +  seqName+ ";");	 
			  out.println(nestedString() + "CREATE SEQUENCE " +  seqName +  " START WITH 1 INCREMENT BY 1 NO MAXVALUE;" );	 
			  
			  // As an alternative, instead of creating a trigger we could also create a 
			  //cretion of the before insert trigger
			  String triggerName=o.getProperty("db.table.triggers.prefix")+ o.getProperty("db.table.triggers.sequence.prefix")+cl.getTableName() +
				o.getProperty("db.table.triggers.sequence.suffix")+	o.getProperty("db.table.triggers.suffix") ;
				
			  //Creating first the trigger procedure
			  out.println(nestedString()+ "DROP FUNCTION IF EXISTS " + triggerName +"() CASCADE;");
				
			  out.println(nestedString()+ "CREATE FUNCTION " + triggerName + "() RETURNS trigger AS $" + triggerName + "$");
			  out.println(nestedString()+ "BEGIN");
			  incNested();
			  out.println(nestedString() + "IF (NEW."+ cl.getPrimaryKeyAttribute().getName() +  " IS NULL) THEN");
			  incNested();
			  out.println(nestedString() + "NEW."+ cl.getPrimaryKeyAttribute().getName() + ":= NEXTVAL('" + seqName + "');");
			  decNested();
			  out.println(nestedString() + "END IF;"); 
			  out.println(nestedString() + "RETURN NEW;");
			  decNested();
			  out.println(nestedString() + "END;");
			  out.println(nestedString() + "$" + triggerName + "$ LANGUAGE plpgsql;");
			  
			  //The trigger is removed when removing the function
			  out.println(nestedString() + "CREATE TRIGGER " + o.getProperty("db.table.triggers.prefix")+ o.getProperty("db.table.triggers.sequence.prefix")+cl.getTableName() +
					o.getProperty("db.table.triggers.sequence.suffix")+	o.getProperty("db.table.triggers.suffix"));
			  out.println(nestedString() + "BEFORE INSERT ON " + cl.getTableName() );
			  out.println(nestedString() + "FOR EACH ROW");
			  out.println(nestedString() + "EXECUTE PROCEDURE " + triggerName +"();");
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
    {  //No update clause in PostGresQL
       out.println(nestedString() + ";") ;
    }
    
	protected String onDelete(S_ForeignKey s)
	{
		if (s.getOnDelete()== E_ForeignKeyEventKind.E_RESTRICT) 
			return "NO ACTION";
		else if (s.getOnDelete()== E_ForeignKeyEventKind.E_DEFAULT) 
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
		// TODO Auto-generated method stub
		return "BOOLEAN";
	}

	@Override
	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
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
		// Postgresql has support for enums starting from version 8.3. For now, we stick to the older constraint check version
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
		return "INTEGER";
	}

	@Override
	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "BIGINT";
	}

	@Override
	protected String getTypeInfoDT_LongReal(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "DOUBLE PRECISION";
	}

	@Override
	protected String getTypeInfoDT_Real(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "REAL";
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
