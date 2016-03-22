/**
 * 
 */
package mdeServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.JDOMException;

import mdeServices.dbGenerator.DBGenerationException;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.DT_Boolean;
import mdeServices.metamodel.DT_Char;
import mdeServices.metamodel.DT_Currency;
import mdeServices.metamodel.DT_Date;
import mdeServices.metamodel.DT_DateTime;
import mdeServices.metamodel.DT_Integer;
import mdeServices.metamodel.DT_LongInteger;
import mdeServices.metamodel.DT_ShortInteger;
import mdeServices.metamodel.DT_UnsignedInteger;
import mdeServices.metamodel.DT_UnsignedLongInteger;
import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.DT_UnsignedLongReal;
import mdeServices.metamodel.DT_UnsignedReal;
import mdeServices.metamodel.DT_UnsignedShortInteger;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.database.MultiplicityTrigger;
import mdeServices.metamodel.database.Trigger;
import mdeServices.metamodel.database.TriggerEventKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_Unique;
import mdeServices.options.Options;
import mdeServices.transformations.T_AddDefaultNameAssoc;
import mdeServices.transformations.T_AddDefaultNameAssocEnds;
import mdeServices.transformations.T_AddDefaultTypeAttributes;
import mdeServices.transformations.T_AssociationsMN_ToAssocClass;
import mdeServices.transformations.T_AssociationsNary_ToAssocClass;
import mdeServices.transformations.T_AssocClassToClass;
import mdeServices.transformations.T_PreciseDataTypeInformation;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.transformations.database.T_AddDefaultNameFKConstraints;
import mdeServices.transformations.database.T_AddDefaultNamePKConstraints;
import mdeServices.transformations.database.T_AddDefaultNameUniqueConstraints;
import mdeServices.transformations.database.T_AddPKeys;
import mdeServices.transformations.database.T_AddPKeysReifiedClasses;
import mdeServices.transformations.database.T_AssociationsToForeignKeys;
import mdeServices.transformations.database.T_MarkAsPersistent;
import mdeServices.transformations.database.T_NoGeneralizations;
import mdeServices.transformations.database.T_RemoveUserDefinedDataTypes;
import mdeServices.utils.Digraph;
import mdeServices.utils.DigraphNode;
import mdeServices.xmi.exportXMI.ExportXMIException;
import mdeServices.xmi.importXMI.ImportXMI;
import mdeServices.xmi.importXMI.ImportXMIException;
import mdeServices.xmi.importXMI.ImportXMIFactory;

/**
 *  Generic Class for generating the database 
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */
public abstract class DBGenerator extends MDEServices{

	protected PrintWriter out;
	protected int nestedLevel=0; //For printing issues	

	
	
	public void generate(File input, String importFromTool, File output, String user) throws ImportXMIException, JDOMException,IOException,DBGenerationException, TransformationNotApplicable, FileNotFoundException
	{
		//If not yet done, initializing the language resource and properties
		if (o==null || l==null)
		{
			System.out.println("Configuration files not initialized");
			initialize(user);
		}
		//Importing the model
		ImportXMI impXMI = ImportXMIFactory.createImportXMI(importFromTool);
		p= impXMI.importFile(input);
		p.setUser(user);
		generate(p,output,user);
		
	}
		
	public void generate(Project p, File output, String user) throws ImportXMIException, JDOMException,IOException,DBGenerationException, TransformationNotApplicable, FileNotFoundException
	{
		//Generating the sql script
		this.p=p;
		out= new PrintWriter(output); 
	    Transformation t;
	    t= new T_AddDefaultTypeAttributes(p,o);
		t.exec();
		t= new T_RemoveUserDefinedDataTypes(p,o);
		t.exec();
	    t= new T_AddDefaultNameAssocEnds(p,o);
		t.exec();
	    t= new T_AddDefaultNameAssoc(p,o);
		t.exec();
		//when generating database we always need to reify M:N associations
		//there´s no framework doing this work for us 
		o.setProperty("app.assoc.reify","true"); 
		t= new T_AssociationsMN_ToAssocClass(p,o);
		t.exec();
		t= new T_AssociationsNary_ToAssocClass(p,o);
		t.exec();
		t= new T_MarkAsPersistent(p,o);
		t.exec();
		
		t=new T_PreciseDataTypeInformation(p,o);
		t.exec();
		
		t= new T_AddPKeys(p,o); //Generation of PKeys 
		t.exec();
		
		t= new T_NoGeneralizations(p,o);
		t.exec();
		
		t= new T_AssocClassToClass(p,o);
		t.exec();
		
		t= new T_MarkAsPersistent(p,o);
		t.exec();
		
		t= new T_AddPKeysReifiedClasses(p,o); //Generation of PKeys for the reified association classes
		t.exec();
		
		t= new T_AssociationsToForeignKeys(p,o);
		t.exec();
		
		t= new T_AddDefaultNamePKConstraints(p,o);
		t.exec();
		

		t= new T_AddDefaultNameFKConstraints(p,o);
		t.exec();
		

		t= new T_AddDefaultNameUniqueConstraints(p,o);
		t.exec();
		
		generateScript(p.getStaticModel(),out);
		out.flush();
		out.close();
	
	}
	
	public void generateScript(StaticModel m, PrintWriter out) throws DBGenerationException
	{
		out.println(commentSymbol()+ l.getString("heading.ddl.script") + p.getName()+commentSymbolEnd());
		out.println();
		Vector<Class> allClasses= m.getAllPersistentClasses();
		
		if (o.getProperty("db.table.drop.script").equals("true"))
		{
			out.println(nestedString() + commentSymbol() + l.getString("heading.ddl.drop.table")+ commentSymbolEnd());
			createDropScript(allClasses, out);
		}
		
		//Generation of the tables for each class	
		Iterator<Class> itCl= allClasses.iterator();
		while (itCl.hasNext())
		{
		  Class cl=itCl.next();
		  generateClass(cl,out);
		}
		
		//Second iteration to generate the foreign keys as "alter table" sentences
		//this way we do not need to generate the tables in any particular order
		Iterator<Class> itCl2=allClasses.iterator();
		while (itCl2.hasNext())
		{
		  Class cl=itCl2.next();
		  generateForeignKeys(cl,out);
		}
		
		//Iteration to generate the multiplicity triggers
		//This part is not implemented due to limitations of DBMSs to check all multiplicities
		/*Iterator<Class> itCl3=allClasses.iterator();
		while (itCl3.hasNext())
		{
		  Class cl=itCl3.next();
		  generateTriggers(cl,out);
		}
	*/	
		if (o.getProperty("db.table.generate.index.fk").equals("true"))
		{
			Iterator<Class> itCl4=allClasses.iterator();
			while (itCl4.hasNext())
			{
			  Class cl=itCl4.next();
			  generateIndexs(cl,out);
			}
		}
		
		otherOperations(m,allClasses,out);
	}
	
	
	protected void generateClass(Class c, PrintWriter out)
	{
		out.println(nestedString()+ commentSymbol() + l.getString("heading.ddl.table") + " " + nameRefinedElements(c.getRefines()) + commentSymbolEnd());
		out.println(nestedString() + "CREATE TABLE " + c.getTableName() + "(");
		incNested();
		boolean auto= c.isPKAutoIncrement();
		Iterator<Attribute> itAt= c.getAtt().iterator();
		Attribute atPK=	c.getPrimaryKeyAttribute();
		while (itAt.hasNext())
		{
			Attribute at=itAt.next();
			if (at.getRefines().size()>0) //Adding comment to identify the association represented by the attribute
				out.println(nestedString()+ commentSymbol() + l.getString("heading.ddl.attribute") + nameRefinedElements(at.getRefines())+commentSymbolEnd());
		    generateAttribute(at, atPK, auto);
		}
		//Adding primary and unique keys at the end;
	
		String pkAttName="";
		pkAttName=atPK.getName();
		out.print(nestedString() + "CONSTRAINT " + c.getPrimaryKey().getConstraintName() +  " PRIMARY KEY (" + pkAttName + ")");
			
		//Adding unique keys (possibly more than one)
		Iterator<S_Unique> itUS = c.getUniqueKeys().iterator();
		if (!(itUS.hasNext())) //If no unique key constraints we just close the tabl 
		   out.println();
		
		while (itUS.hasNext())
		{
			S_Unique su=itUS.next();
			Iterator<Attribute> itUAtt= su.getRefAtt().iterator();
			String uAttName=""; int i=1;
			while (itUAtt.hasNext())
			{
				Attribute a=itUAtt.next();
				if (itUAtt.hasNext()) uAttName=uAttName+a.getName()+",";
				else uAttName=uAttName+a.getName();
			}
			out.println(",");
			out.println(nestedString() + "CONSTRAINT " + su.getConstraintName() + " UNIQUE (" + uAttName + ")");
		    i=i++;
		}

		decNested();
		out.println(nestedString() + ");"); 
		out.println();
	}
	
	protected void generateAttribute(Attribute at, Attribute atPK, boolean auto)
	{
		out.println(nestedString() + at.getName() + " " + getTypeInformation(at,at.getType()) + " " + notnull(at) + " " + autoIncrement(atPK,at,auto)+ ",");
	}
	
	protected void generateForeignKeys(Class c, PrintWriter out)
	{
	//Adding foreign keys (possibly more than one)
	Iterator<S_ForeignKey> itFK = c.getForeignKeys().iterator();
	while (itFK.hasNext())
	{
		S_ForeignKey s= ((S_ForeignKey) itFK.next());
	//	Iterator<Attribute> itFKAtt= s.getOwnAtt().iterator();
		out.println(nestedString() + "ALTER TABLE " + c.getTableName());
		incNested();
		Attribute a= s.getOwnAtt();	
		out.println(nestedString() + "ADD CONSTRAINT " + reduceLength(s.getConstraintName()) + " FOREIGN KEY(" + a.getName() + ")");
		incNested();
		out.println(nestedString() + "REFERENCES " + s.getReferencedClass().getTableName() + "(" + s.getRefAtt().getName() + ")");
		onDeleteClause(out,s);
		onUpdateClause(out,s);
		out.println();
		decNested(); 
		decNested();
	  }
	}
	
    protected void onDeleteClause(PrintWriter out, S_ForeignKey s)
    {
    	out.println(nestedString() + "ON DELETE " + onDelete(s)) ;
    }
	
    protected void onUpdateClause(PrintWriter out, S_ForeignKey s)
    {
        out.println(nestedString() + "ON UPDATE " + onUpdate(s) +";") ;
    }

    //Generation of an index for each foreign key
	protected void generateIndexs(Class c, PrintWriter out)
	{
		//Adding foreign keys (possibly more than one)
		Iterator<S_ForeignKey> itFK = c.getForeignKeys().iterator();
		while (itFK.hasNext())
		{
			S_ForeignKey s= ((S_ForeignKey) itFK.next());
			out.println(nestedString() + "CREATE INDEX " + o.getProperty("db.table.index.fk.prefix")+ c.getName()+ "_" + s.getOwnAtt().getName()+
				o.getProperty("db.table.index.fk.suffix") + " ON " + c.getTableName() + "(" + s.getOwnAtt().getName()+ ");");
		}
	}
	

	/*
    //Generation of the multiplicity triggers for each class 
	protected void generateTriggers(Class c, PrintWriter out)
	{
		Iterator<Trigger> itTr= c.getTriggers().iterator();
		while (itTr.hasNext())
		{
			Trigger t= itTr.next();
			if (t instanceof MultiplicityTrigger)
			{
				MultiplicityTrigger tMult= (MultiplicityTrigger)t;
				String event=tMult.getEvent().toString().toUpperCase();
				
				//In MYSQL it is not possible to indicate the specific column
				//	if (triggerEventKind==TriggerEventKind.E_UPDATE)
				//	{
				//		event= " OF " + tMult.getAttribute() +" ";
				//	}
					
				//Not needed in MySQL. Triggers are dropped if dropping the table
				//if (o.getProperty("db.table.drop.script").equals("true")) 
				//  out.println(nestedString() + "DROP TRIGGER IF EXISTS " + tMult.getName() + ";");

				out.println(nestedString() + "CREATE TRIGGER " + o.getProperty("db.table.triggers.prefix")+  tMult.getName() + o.getProperty("db.table.triggers.suffix"));
				out.println(nestedString() + tMult.getMoment().toString().toUpperCase() + " " + event );
				out.println(nestedString() + "ON " + c.getTableName());
				out.println(nestedString() + "FOR EACH ROW ");
				out.println(nestedString() + "DECLARE v_mult NUMBER;");
				out.println(nestedString() + "BEGIN");
				incNested();
				if (tMult.getEvent()==TriggerEventKind.E_UPDATE)
				{
					out.println(nestedString() + "IF :NEW." + tMult.getAttribute().getName() + " != :OLD." + tMult.getAttribute().getName() + " THEN");
					incNested();
				}
				out.println(nestedString() + "SELECT count(*) INTO v_mult");
				out.println(nestedString() + "FROM " + c.getTableName());
				if (tMult.isMin())
				{
					out.println(nestedString() + "WHERE " + tMult.getAttribute().getName() +  " = :OLD." +  tMult.getAttribute().getName()+ ";");
			        out.println(nestedString() + "IF v_mult=" + tMult.getMultiplicity() + " THEN ");
			        incNested();
			        out.println(nestedString() + "RAISE_APPLICATION_ERROR(num => -20000, msg => '" + c.getName() + "." + tMult.getAttribute().getName() + ":"+ l.getString("error.trigger.multiplicity.min")+ "');");
					decNested();
					out.println(nestedString() + "END IF;");
				}
				if (tMult.isMax())
				{
					out.println(nestedString() + "WHERE " + tMult.getAttribute().getName() +  " = :NEW." +  tMult.getAttribute().getName()+ ";");
			        out.println(nestedString() + "IF v_mult=" + tMult.getMultiplicity() + " THEN ");
			        incNested();
			        out.println(nestedString() + "RAISE_APPLICATION_ERROR(num => -20000, msg => '" + c.getName() + "." + tMult.getAttribute().getName() + ":"+ l.getString("error.trigger.multiplicity.max")+ "');");
					decNested();
					out.println(nestedString() + "END IF;");
				}
	
				if (tMult.getEvent()==TriggerEventKind.E_UPDATE)
				{
					decNested();
					out.println(nestedString() + "END IF;");
				}

				decNested();
				out.println(nestedString() + "END;");
				out.println(nestedString() + "/");
				out.println();
				
			}
		}
	}
	*/
	protected void incNested(){nestedLevel++;}
	
	protected void decNested(){nestedLevel--;}
	
	protected String nestedString(){
		String tab= "";
		for(int i=0;i<=nestedLevel;i++){tab=tab.concat("  ");}
		return tab;
	}
	
	protected String onDelete(S_ForeignKey s)
	{
		return s.getOnDelete().toString();
	}
	
	protected String onUpdate(S_ForeignKey s)
	{
		return s.getOnUpdate().toString();
	}
	
	protected String notnull(Attribute at)
	{
	  if (at.getMin()==0) return "";
	  else return "NOT NULL";
	}
	
	protected String nameRefinedElements(Vector<ModelElement> refined)
	{
		String aux="";
		Iterator<ModelElement> me= refined.iterator();
		while (me.hasNext()) {
			ModelElement modelElement = (ModelElement) me.next();
			if (!(aux.contains(modelElement.getName())))
			{	aux=aux+ modelElement.getName();
				if (me.hasNext()) aux=aux+ ",";
			}
		}
		if (aux.substring(aux.length()-1).equals(","))
			aux=aux.substring(0,aux.length()-1);
		return aux;
	}
	
	//Function that orders the classes depending on the foreign key dependencies
	//required in some DBMS to perform the drop tables in order
	//Returns an exception if the classes are cyclic
	protected Vector<Classifier> orderClasses(Vector<Class> allClasses) throws DBGenerationException
	{
		Digraph d= new Digraph();
		Vector<Classifier> order=new Vector<Classifier>(0,1);
	    // Creating the vertices in the digraph
		Iterator<Class> itClAll = allClasses.iterator();
		while (itClAll.hasNext()) {
			Class class1 = (Class) itClAll.next();
			d.addNode(class1);
		}
		//Creating the edges
		Iterator<Class> itClAll2 = allClasses.iterator();
		while (itClAll2.hasNext()) {
			Class class1 = (Class) itClAll2.next();
			Iterator<S_ForeignKey> itFK= class1.getForeignKeys().iterator();
			while (itFK.hasNext()) {
				S_ForeignKey foreignKey = (S_ForeignKey) itFK.next();
				Class refClass= foreignKey.getReferencedClass();
				if (refClass!=class1)
				{ //Adding the edge
					d.addEdgeFromNodeToNodeUsingUniqueLabels(class1,refClass);
				}
			}			
		}
		while (d.numberOfNodes()>0 && d.sources().size()>0)
		{
			ArrayList<DigraphNode> al=d.sources();
			Iterator<DigraphNode> iN= al.iterator();
		    while (iN.hasNext()) {
				DigraphNode digraphNode = (DigraphNode) iN.next();
				order.add(digraphNode.getLabel());
				d.removeNode(digraphNode);
			}
	       
		}
		if (d.numberOfNodes()>0) throw new DBGenerationException("Cyclic Foreign Key Dependencies");	
		return order;
		
	}
	
	//Gets the text for that datatype
	protected String getTypeInformation(Attribute a, Classifier d)
	{
		String type="";
		if (d instanceof DT_UnsignedInteger)
		{
			type=getTypeInfoDT_UnsignedInteger(a,d);
		}
		else if (d instanceof DT_UnsignedLongInteger)
		{
			type=getTypeInfoDT_UnsignedLongInteger(a,d);
		}
		else if (d instanceof DT_LongInteger)
		{
			type=getTypeInfoDT_LongInteger(a,d);
		}
		else if (d instanceof DT_UnsignedShortInteger)
		{
			type=getTypeInfoDT_UnsignedShortInteger(a,d);
		}
		else if (d instanceof DT_ShortInteger)
		{
			type=getTypeInfoDT_ShortInteger(a,d);
		}
		else if (d instanceof DT_Integer)
		{
			type=getTypeInfoDT_Integer(a,d);
		}
	
		else if (d instanceof DT_UnsignedReal)
		{
			type=getTypeInfoDT_UnsignedReal(a,d);
		}
		else if (d instanceof DT_Currency)
		{
			type=getTypeInfoDT_Currency(a,d);
		}
		else if (d instanceof DT_UnsignedLongReal)
		{
			type=getTypeInfoDT_UnsignedLongReal(a,d);
		}
		else if (d instanceof DT_LongReal)
		{
			type=getTypeInfoDT_LongReal(a,d);
		}
		else if (d instanceof DT_Real)
		{
			type=getTypeInfoDT_Real(a,d);
		}
		else if (d instanceof DT_Boolean)
		{ 
			type=getTypeInfoDT_Boolean(a,d);
		}
		else if (d instanceof DT_Char)
		{
			type=getTypeInfoDT_Char(a,d);
		}
		else if (d instanceof DT_String)
		{
			type=getTypeInfoDT_String(a,d);
		}
		else if (d instanceof DT_Date)
		{ 
			type=getTypeInfoDT_Date(a,d);
		}
		else if (d instanceof DT_DateTime)
		{ 
			type=getTypeInfoDT_DateTime(a,d);
		}
		else if (d instanceof Enumeration)
		{
			type=getTypeInfoDT_Enumeration(a,d);
		}
	
		return type;
	}
		
	
	
	/***************************** Abstract methods *****************************/
	// List of methods redefined in each subclass 
	
	protected abstract String commentSymbol();
	protected abstract String commentSymbolEnd();
	//Informs whether the database has the capabilities of autoincrement primary keys
	public abstract  boolean isAutoIncrementPossible();
		
	//Generates a connection string to the database using the given parameters
	//When some of the parameters is empty it uses a default value
	public abstract String getPHPDBConnection(String host, String dbName);
	
	//Generation of the drop script 
	protected abstract void createDropScript(Vector<Class> allClasses, PrintWriter out) throws DBGenerationException;

	//String for auto increment primary keys
	protected abstract String autoIncrement(Attribute pk, Attribute at, boolean isAuto);

	protected abstract void otherOperations(StaticModel m, Vector<Class> allClasses, PrintWriter out);
	
	//Reduce the lenght of the name (due to restrictions of max length in some DBMS)
	protected abstract String reduceLength(String str);
	
	//Translation for the types
	
	protected abstract String getTypeInfoDT_UnsignedInteger(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_UnsignedLongInteger(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_LongInteger(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_UnsignedShortInteger(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_ShortInteger(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Integer(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_UnsignedReal(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_UnsignedLongReal(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Real(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_LongReal(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Boolean(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_String(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Date(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_DateTime(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Enumeration(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Currency(Attribute a, Classifier d);
	protected abstract String getTypeInfoDT_Char(Attribute a, Classifier d);

		
}

