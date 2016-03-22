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
 *  Class for generating the firebird database 
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */
public class DBGeneratorFirebird extends DBGenerator{

	
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
	{	//In firebird we need to remove all objects linked to the table before deleting the table itself
	    out.println(nestedString() + "DROP TRIGGER " + o.getProperty("db.table.triggers.prefix")+ o.getProperty("db.table.triggers.sequence.prefix")+c.getTableName() +
				o.getProperty("db.table.triggers.sequence.suffix")+	o.getProperty("db.table.triggers.suffix")+ ";");
		
	    Iterator<S_ForeignKey> itFK= c.getForeignKeys().iterator();
	  while (itFK.hasNext()) {
		S_ForeignKey s = (S_ForeignKey) itFK.next();
		  out.println(nestedString() + "DROP INDEX " + o.getProperty("db.table.index.fk.prefix")+ c.getName()+ "_" + s.getOwnAtt().getName()+
					o.getProperty("db.table.index.fk.suffix")+";");
	  }
	    
	  	    out.println(nestedString() + "DROP TABLE " + c.getTableName() + ";");
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
			  out.println(nestedString() + "CREATE SEQUENCE " +  seqName +";" );	 
			  
			  //cretion of the before insert trigger
			  out.println(nestedString() + "SET TERM ^ ;");
			  
			  out.println(nestedString() + "CREATE TRIGGER " + o.getProperty("db.table.triggers.prefix")+ o.getProperty("db.table.triggers.sequence.prefix")+cl.getTableName() +
					o.getProperty("db.table.triggers.sequence.suffix")+	o.getProperty("db.table.triggers.suffix"));
				
				out.println(nestedString() + "BEFORE INSERT ON " + cl.getTableName() );
			//	out.println(nestedString() + "FOR EACH ROW");
				out.println(nestedString() + "AS BEGIN");
				incNested();
				out.println(nestedString() + "IF (new."+ cl.getPrimaryKeyAttribute().getName() +  " IS NULL) THEN");
				incNested();
				out.println(nestedString() + "new." +  cl.getPrimaryKeyAttribute().getName() + " = NEXT VALUE FOR " + seqName +";"); 
				decNested();
			//	out.println(nestedString() + "END IF;"); 
				decNested();
				out.println(nestedString() + "END^");
				out.println(nestedString() + "SET TERM ; ^");
				out.println();
		  }
		}
	};
	
	protected  String reduceLength(String str)
	{
	   if (str.length()>30) return str.substring(0, 29);	
	    else return str;
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

	@Override
	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
	  String type="";
		type="NUMERIC(1) CONSTRAINT " + o.getProperty("db.constraint.ck.prefix") + a.getName() +  o.getProperty("db.constraint.ck.boolean.name") +  
	      o.getProperty("db.constraint.ck.suffix")+ " CHECK(" + a.getName() + "=0 OR " +a.getName() + "=1)" ;
		return type;
	}

	@Override
	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "DATE";
	}

	@Override
	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return null;
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
		return "FLOAT";
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
