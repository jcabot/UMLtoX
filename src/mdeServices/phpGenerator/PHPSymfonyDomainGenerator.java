package mdeServices.phpGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationEnd;
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
import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_ShortInteger;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.DT_UnsignedInteger;
import mdeServices.metamodel.DT_UnsignedLongInteger;
import mdeServices.metamodel.DT_UnsignedLongReal;
import mdeServices.metamodel.DT_UnsignedReal;
import mdeServices.metamodel.DT_UnsignedShortInteger;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_ReifiedAssociation;
import mdeServices.metamodel.stereotypes.S_Unique;
import mdeServices.options.LangManager;
import mdeServices.options.Options;

import mdeServices.transformations.TransformationNotApplicable;

import mdeServices.xmi.importXMI.ImportXMIException;

import org.jdom.JDOMException;

public class PHPSymfonyDomainGenerator {
	
	protected LangManager l;
	protected Options o;
	protected Project p;
	protected StaticModel m;
	protected PrintWriter out;
	
	protected int nestedLevel=0; //For printing issues	

	public void generate(Project _p, PrintWriter _out, Options _o, LangManager _l)throws IOException,PHPDomainGenerationException
	{
		p=_p;
		m=p.getStaticModel();
		l=_l;
		o=_o;
		out=_out;
		out.println(nested()+commentSymbol()+ l.getString("heading.yml.script") + p.getName()+commentSymbolEnd());
		out.println();
		Vector<Class> allClasses= m.getAllPersistentClasses();
		
		//Generation of the definitions for each class	
		Iterator<Class> itCl= allClasses.iterator();
		while (itCl.hasNext())
		{
		  Class cl=itCl.next();
		  generateClass(cl,out);
		}
		
		//Generation of the definitions for each MN Association not reified
		Iterator<Association> itAs= m.getAllAssociationsNoAssCl().iterator();
		while (itAs.hasNext())
		{
		  Association as=itAs.next();
		  if(as.isMN()) generateAssociation(as,out);
		}
		
		out.flush();
		out.close();
					
	}
	
	//Very similar to the code for the generation of classes but simpler, the only attributes are the PKs and
	//the foreign keys to the two participant classes
	protected void generateAssociation(Association as, PrintWriter out) throws IOException,PHPDomainGenerationException
	{
		 try
		  {
			AssociationEnd ae1=as.getEnds().get(0);
			Class cl1=(Class) ae1.getSource();
			Attribute pk1=cl1.getPrimaryKey().getRefAtt();
			AssociationEnd ae2=as.getEnds().get(1);
			Class cl2=(Class)ae2.getSource();
			Attribute pk2=cl2.getPrimaryKey().getRefAtt();
			
			out.println(nested() + as.getName() + ":");
			incNested();
		//	out.println(nested()+ "tableName: " + c.getTableName()); We don't define the table name in this case
			out.println(nested() + "columns:");
			incNested();
					
			out.println(nested() + ae1.getName()+":");
			incNested();
			out.println(nested()+"type: " + getTypeInformation(pk1,pk1.getType()));
			out.println(nested()+"primary: true");
			String unsigned=getUnsignedAttributeProperty(pk1);
			if (unsigned!=null) out.println(nested()+unsigned);
			String scale=getScaleAttributeProperty(pk1);
			if (scale!=null) out.println(nested()+scale);
			String fixed=getFixedLengthAttributeProperty(pk1);
			if (fixed!=null) out.println(nested()+fixed);
			decNested();
			
			out.println(nested() + ae2.getName()+":");
			incNested();
			out.println(nested()+"type: " + getTypeInformation(pk2,pk2.getType()));
			out.println(nested()+"primary: true");
			String unsigned2=getUnsignedAttributeProperty(pk2);
			if (unsigned2!=null) out.println(nested()+unsigned2);
			String scale2=getScaleAttributeProperty(pk2);
			if (scale2!=null) out.println(nested()+scale2);
			String fixed2=getFixedLengthAttributeProperty(pk2);
			if (fixed2!=null) out.println(nested()+fixed2);
			decNested();
			
			decNested();
			out.println(nested() + "relations:");
			incNested();
			out.println(nested() + ae1.getName().substring(0, 1).toUpperCase()+ ae1.getName().substring(1,ae1.getName().length())+":");
			incNested();
			out.println(nested() + "class: " + cl1.getName());
			out.println(nested() + "local: " + ae1.getName()); 
			out.println(nested() + "foreign: " + pk1.getName());
			out.println(nested() + "onDelete: CASCADE");  //Fixed value in this case
			decNested();
			out.println(nested() + ae2.getName().substring(0, 1).toUpperCase()+ ae2.getName().substring(1,ae2.getName().length())+":");
			
			incNested();
			out.println(nested() + "class: " + cl2.getName());
			out.println(nested() + "local: " + ae2.getName()); //Name local and foreign is the same
			out.println(nested() + "foreign: " + pk2.getName());
			out.println(nested() + "onDelete: CASCADE");  //Fixed value in this case
			decNested();
		    out.println();
		 }catch(Exception e)
			{ throw new PHPDomainGenerationException("Error when creating the domain PHP schema");}
		
	}
	
	
	protected void generateClass(Class c, PrintWriter out) throws IOException,PHPDomainGenerationException

	{
	  try
	  {
		out.println(nested()+ commentSymbol() + l.getString("heading.yml.table") + " " + nameRefinedElements(c.getRefines()) + commentSymbolEnd());
		out.println(nested() + c.getName() + ":");
		incNested();
		out.println(nested()+ "tableName: " + c.getTableName());
		if (c.hasAttributes())
		{	
			out.println(nested() + "columns:");
			incNested();
			boolean auto= c.isPKAutoIncrement();
			Iterator<Attribute> itAt= c.getAtt().iterator();
			Attribute atPK=	c.getPrimaryKeyAttribute();
			while (itAt.hasNext())
			{
				Attribute at=itAt.next();
				if (at.getRefines().size()>0) //Adding comment to identify the association represented by the attribute
					out.println(nested()+ commentSymbol() + l.getString("heading.ddl.attribute") + nameRefinedElements(at.getRefines())+commentSymbolEnd());
				generateAttribute(at, atPK, auto);
			}
			decNested();
		}
		if (c.hasForeignKeys() || c.hasAssociations()) //Associations must be M:N associations that user didn't want to reify
		{
			
			out.println(nested() + "relations:");
			incNested();
			Iterator<S_ForeignKey> itFK = c.getForeignKeys().iterator();
			while (itFK.hasNext())
			{
				S_ForeignKey s= ((S_ForeignKey) itFK.next());
				//	Iterator<Attribute> itFKAtt= s.getOwnAtt().iterator();
			
				out.println(nested() + s.getName().substring(0, 1).toUpperCase()+ s.getName().substring(1,s.getName().length())+":");
				incNested();
				out.println(nested() + "class: " + s.getReferencedClass().getName());
				out.println(nested() + "local: " + s.getOwnAtt().getName());
				out.println(nested() + "foreign: " + s.getRefAtt().getName());
				out.println(nested() +  "owningSide: true");
				out.println(nested() + "foreignType: "+ reverseForeignKeyMultiplicity(s));
				out.println(nested() + "foreignKeyName: " + s.getConstraintName());
				out.println(nested() + "onDelete: " + onDeleteClause(s));
				out.println(nested() + "onUpdate: " + onUpdateClause(s));
				decNested();
			}
		
		
			Iterator<AssociationEnd> itAE=c.getAss().iterator();
		
			while (itAE.hasNext()) {
				AssociationEnd ae= (AssociationEnd) itAE.next();
			    ModelElement auxAss=ae.getAss();
			    if(auxAss instanceof Association)
			    {
			    	Association as=(Association) auxAss;
			    	if (as.isMN())
			    	{
			    	  AssociationEnd aeOpp= as.oppositeEnd(ae);
			    	  out.println(nested() + aeOpp.getName().substring(0, 1).toUpperCase()+ aeOpp.getName().substring(1,aeOpp.getName().length())+":");
					  incNested();
						out.println(nested() + "class: " + ae.getOppositeEndClass().getName());
						out.println(nested() + "local: " + ae.getName());  //these will be the values of the pk in the MN class
						out.println(nested() + "foreign: " + aeOpp.getName());
						out.println(nested() + "refClass: " + as.getName());
						decNested();
				     }
			    }
			}
			decNested();
		}
			
		//Adding unique keys (possibly more than one)
		//right now the only unique keys we may have are the ones derived from M:N associations turned into association classes
		//in Doctrine unique multi-colums must be defined as an index
		Iterator<S_Unique> itUS = c.getUniqueKeys().iterator();
		if (!(itUS.hasNext())) //If no unique key constraints we just close the class definition 
		   out.println();
		else
		{
			out.println(nested() + "indexes:"); 
			incNested();
			while (itUS.hasNext())
			{
				S_Unique su=itUS.next();
				Iterator<Attribute> itUAtt= su.getRefAtt().iterator();
				String indexFields=""; int i=1;
				while (itUAtt.hasNext())
				{
					Attribute a=itUAtt.next();
					if (itUAtt.hasNext()) indexFields=indexFields+a.getName()+",";
					else indexFields=indexFields+a.getName();
				}
				out.println(nested() + su.getConstraintName() + ":");
				incNested();
				out.println(nested()+"fields: " + "["+indexFields+"]");
				out.println(nested()+"type: " + "unique");
				decNested();
			}
			decNested();
		}
		decNested();
		out.println();
	  }catch(Exception e)
		{ throw new PHPDomainGenerationException("Error when creating the domain PHP schema");}
	}
	
	protected String onDeleteClause(S_ForeignKey s)
	{
		return (s.getOnDelete().toString().toUpperCase());
		
	}
	
	protected String onUpdateClause(S_ForeignKey s)
	{
		return (s.getOnUpdate().toString().toUpperCase());
		
	}
	
	protected String reverseForeignKeyMultiplicity(S_ForeignKey s)
	{
	  if (s.isOppositeMany()) return "many";
	  else return "one";
	}
	
	protected void generateAttribute(Attribute at, Attribute atPK, boolean auto)
	{
		out.println(nested() + at.getName()+":");
		incNested();
		out.println(nested()+"type: " + getTypeInformation(at,at.getType()));
		String pk=getPrimaryAttributeProperty(at,atPK);
		if (pk!=null) out.println(nested()+pk);
		String autoInc=getAutoIncrementAttributeProperty(at,atPK,auto);
		if (autoInc!=null) out.println(nested()+autoInc);
		String notnull=getNotNullAttributeProperty(at);
		out.println(nested()+notnull);
		String unsigned=getUnsignedAttributeProperty(at);
		if (unsigned!=null) out.println(nested()+unsigned);
		String scale=getScaleAttributeProperty(at);
		if (scale!=null) out.println(nested()+scale);
		String fixed=getFixedLengthAttributeProperty(at);
		if (fixed!=null) out.println(nested()+fixed);
		String values=getEnumValuesAttributeProperty(at);
		if (values!=null) out.println(nested()+values);
		decNested();
	}
	
	
	protected String getPrimaryAttributeProperty(Attribute at, Attribute atPK)
	{
		String pk=null;
		if (at==atPK) pk="primary: true";
		return pk;				
	}
	
	protected String getAutoIncrementAttributeProperty(Attribute at, Attribute atPK, boolean auto)
	{
		String autoInc=null;
		if (at==atPK && auto) autoInc="autoincrement: true";
		return autoInc;				
	}
	

	protected String getNotNullAttributeProperty(Attribute at)
	{
		String notnull=null;
		if (at.getMin()>=1) notnull="notnull: true";
		else notnull="notnull: false";
		return notnull;				
	}
	
	protected String getUnsignedAttributeProperty(Attribute at)
	{
		String unsigned=null;
		if (isUnsignedType(at.getType())) unsigned="unsigned: true";
		return unsigned;				
	}
	
	protected String getScaleAttributeProperty(Attribute at)
	{
		String scale=null;  
		//All types with precision are instance of DT_Real
		if (at.getType() instanceof DT_Real) scale="scale: " + ((DT_Real)at.getType()).getPrecision();
		return scale;				
	}
	
	protected String getFixedLengthAttributeProperty(Attribute at)
	{
		String fixed=null;  
		//All types with precision are instance of DT_Real
		if (at.getType() instanceof DT_String) 
		  if( ((DT_String) at.getType()).getFixLength().booleanValue())
			fixed="fixed: true";
		return fixed;				
	}
	
	protected String getEnumValuesAttributeProperty(Attribute at)
	{
		String enumValues=null;
		if(at.getType() instanceof Enumeration)
		{
			Enumeration e=(Enumeration) at.getType();
			Iterator<String> itS=e.getValues().iterator();
			String values="";
			while (itS.hasNext()) {
				String string = (String) itS.next();
				values=values+ string;
				if (itS.hasNext()) values=values + ",";
			}
			if (!values.equals("")) enumValues="values: [" + values + "]";
			
		}
		return enumValues;				
	}
	
	
	//Auxiliary methods
	
	
	protected String commentSymbol(){ return "#";}

	protected String commentSymbolEnd(){return "";}
	
	protected void incNested(){nestedLevel++;}
	
	protected void decNested(){nestedLevel--;}
	
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
	
	protected String nested(){
		String tab= "";
		for(int i=0;i<nestedLevel;i++){tab=tab.concat("  ");}
		return tab;
	}
		
	
	/****** DATA TYPE METHODS *****************************/

	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
	  String type="boolean";
		return type;
	}


	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "date";
	}


	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "timestamp";
	}

   // the possibles values the enumeration can take must be indicated in the property values of the attribute
	protected String getTypeInfoDT_Enumeration(Attribute a, Classifier d) {
		String type="enum";
		return type;
	}

	protected String getTypeInfoDT_Integer(Attribute a, Classifier d) {
		String type="";
		DT_Integer dI = (DT_Integer) d;
		type="integer(" + dI.getLength() + ")";
		return type;
	}

	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_Integer(a,d);
	}


	protected String getTypeInfoDT_LongReal(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_Real(a,d);
	}

//the precision is not indicated as part of the type but as a property
	protected String getTypeInfoDT_Real(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		String type="";
		DT_Real dR = (DT_Real ) d;
		type="decimal(" + dR.getLength()+")";
		return type;
	}

//Fixed and variable lenght fields have the same type but for fixed ones we need to add the fixed:true property
	protected String getTypeInfoDT_String(Attribute a, Classifier d) {
		String type="";
		DT_String dS = (DT_String) d;
		type="string(" + dS.getLength() + ")";
		return type;
	}


//The unsigned are generated as singed plus the "unsigned" validator set to true
	protected String getTypeInfoDT_UnsignedInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_Integer(a,d);
	}


	protected String getTypeInfoDT_UnsignedLongInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_LongInteger(a,d);
	}


	protected String getTypeInfoDT_UnsignedLongReal(Attribute a, Classifier d) {
		return getTypeInfoDT_LongReal(a,d);
	}


	protected String getTypeInfoDT_UnsignedReal(Attribute a, Classifier d) {
		return getTypeInfoDT_Real(a,d);
	}
	

	protected String getTypeInfoDT_UnsignedShortInteger(Attribute a,Classifier d) {
		return getTypeInfoDT_ShortInteger(a,d);
	}

	protected String getTypeInfoDT_ShortInteger(Attribute a, Classifier d) {
		return getTypeInfoDT_Integer(a,d);
	}

	protected String getTypeInfoDT_Currency(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_Real(a,d);
	}

	protected String getTypeInfoDT_Char(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return getTypeInfoDT_String(a,d);
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
		
	//Determines if the attribute is of an unsigned type
	protected boolean isUnsignedType(Classifier d)
	{
		boolean unsigned=false;
		if ((d instanceof DT_UnsignedInteger) || (d instanceof DT_UnsignedLongInteger) || (d instanceof DT_UnsignedShortInteger) ||
				(d instanceof DT_UnsignedReal) || (d instanceof DT_UnsignedLongReal) ) 
			unsigned=true;
	
		return unsigned;
	}
	
}
