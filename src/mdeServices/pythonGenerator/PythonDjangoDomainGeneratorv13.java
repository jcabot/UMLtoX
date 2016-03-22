package mdeServices.pythonGenerator;

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
import mdeServices.metamodel.DT_LongString;
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

public class PythonDjangoDomainGeneratorv13 {
	
	protected LangManager l;
	protected Options o;
	protected Project p;
	protected StaticModel m;
	protected PrintWriter out;
	Vector<String> treatedMNAssoc = new Vector<String>(0,1); //ManyToMany fields can only appear in one end
	Vector<String> treatedReifiedAssoc = new Vector<String>(0,1); //ManyToMany fields can only appear in one end
	
	
	protected int nestedLevel=0; //For printing issues	

	public void generate(Project _p, PrintWriter _out, Options _o, LangManager _l)throws IOException,PythonDomainGenerationException
	{
		p=_p;
		m=p.getStaticModel();
		l=_l;
		o=_o;
		out=_out;
		out.println(nested()+commentSymbol()+ l.getString("heading.python.domain.script") + p.getName()+commentSymbolEnd());
		out.println();
		out.println(nested()+ "from django.db import models");
		out.println(nested()+ "from django.utils.encoding import smart_unicode");
		out.println();		
		Vector<Class> allClasses= m.getAllPersistentClasses();
		
		//Generation of the tables for each class	
		Iterator<Class> itCl= allClasses.iterator();
	
		while (itCl.hasNext())
		{
		  Class cl=itCl.next();
		  generateClass(cl,out);
		}
		out.flush();
		out.close();
					
	}
		
	protected void generateClass(Class c, PrintWriter out) throws IOException,PythonDomainGenerationException

	{
	  try
	  {
		out.println(nested()+ commentSymbol() + l.getString("heading.python.table") + " " + nameRefinedElements(c.getRefines()) + commentSymbolEnd());
		
		String parentClass="";
		//If there is superclass, we just use the generic models class
		S_ForeignKey fkGen=c.getGeneralizationForeingnKey();
		if (fkGen!=null) parentClass=fkGen.getReferencedClass().getName();
		else parentClass="models.Model";
			
		out.println(nested() + "class " + c.getName() + "(" + parentClass+"):");
		incNested();
		
		if (c.hasAttributes())
		{	
			boolean auto= c.isPKAutoIncrement();
			Iterator<Attribute> itAt= c.getAtt().iterator();
			Attribute atPK=	c.getPrimaryKeyAttribute();
			while (itAt.hasNext())
			{
				Attribute at=itAt.next();
				if (!(at.isPartOfFK()))
				{		
					generateAttribute(at, atPK, auto);
				}
			}
		}
		if (c.hasForeignKeys())
		{   //Adding fields corresponding to foreign keys
			Iterator<S_ForeignKey> itFK = c.getForeignKeys().iterator();
			while (itFK.hasNext())
			{
				S_ForeignKey s= ((S_ForeignKey) itFK.next());
				String refClass="";
				if (s.getReferencedClass()==c)
				{
					refClass="self"; //For reflexive associations we need to indicate the self
				}
				else{
					refClass=s.getReferencedClass().getName();
				}
				String nullFK="False"; 	if (s.getOwnAtt().getMin()==0) nullFK="True";
				
				String modelFieldType="";
				if (s.isOppositeMany()) modelFieldType="models.ForeignKey"; //one to many or many to many relationship
				else modelFieldType="models.OneToOneField";
				
				String parentLink="";
				if (s.isGeneralization()) //this foreign key links to the primary key of the parent table
					parentLink=",parent_link=True";
				else parentLink=""; //since this attribute is only valid for one-to-one relationships when it is not
				//necessary, we just ignore it
				
				String relatedName="";
				if (s.isReification())
					relatedName=s.getOppositeName()+"fk";  //the related_name of the FK from an associated class 
				    //to the participants can clash with the name of the implicit set fiels created by django
				    //when a manytomany field is around in the participants, that´s why we add "fk" by default
				else 
					relatedName=s.getOppositeName();
					
				
				//this foreign key links to the primary key of the parent table
				out.println(nested()+s.getOwnAtt().getName()+" = " + modelFieldType+"('"+refClass+"'" + parentLink + ", related_name='"+relatedName+"'"+
					  ",to_field='"+ s.getRefAtt().getName()+"'"+",null="+nullFK+",blank="+nullFK+")");				
			}
		}
		
		if (c.hasAssociations()) //They must be MN associations that the user didn´t want to reify
		{
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
			    	  String nullFK="False"; 	if (aeOpp.getMin()==0) nullFK="True";
					  if (!treatedMNAssoc.contains(as.getName()))
					  { //if we have not added already a manytomany field for the association
						treatedMNAssoc.add(as.getName());	
						if (ae.getOppositeEndClass()!=c)
			    	    {
				   	      out.println(nested()+aeOpp.getName().toLowerCase()+" = models.ManyToManyField('" + ae.getOppositeEndClass().getName()+"', related_name='"+ae.getName()+"',null="+nullFK+",blank="+nullFK+")");
			     	    }
				        else
				        {
					      out.println(nested()+aeOpp.getName().toLowerCase()+" = models.ManyToManyField('self', related_name='"+ae.getOppositeEndClass().getName()+"'"+"',null="+nullFK+",blank="+nullFK+")");
					    }
			    	   }
				     }
			      }
			}
		}
			
		
		if (c.hasReifiedAssociations())
		{
			//Adding manytomany fields to link the participants in an association class
			Iterator<S_ReifiedAssociation> itRA = c.getReifiedAssociations().iterator();
			while (itRA.hasNext())
			{
				//Treatment of reified reflexive associations is slightly special in djano
				//the class must be marked as self, we need to add symmetric=False and only one of the manytomany fields to the
				//reified class must be added
				
				S_ReifiedAssociation r= ((S_ReifiedAssociation) itRA.next());
				
				String nullFK="False"; 	if (r.getOppositeEnd().getMin()==0) nullFK="True";
				if (!treatedReifiedAssoc.contains(r.getAssociationClass().getName()))
				{
				  treatedReifiedAssoc.add(r.getAssociationClass().getName());	
				  System.out.println(r.getAssociationClass().getName());
				  if (r.getReferencedClass()!=c)
				  {
					  out.println(nested()+r.getName().toLowerCase()+" = models.ManyToManyField('" + r.getReferencedClass().getName()+"', through='"+ r.getAssociationClass().getName()+ "'"+", related_name='"+r.getOwnEndName()+"',null="+nullFK+",blank="+nullFK+")");
			      }
				  else  //for ManytoMany with an intermeidate model we must force symmetrical=false 
				  {
					 //if we have not added already a manytomany fields for that reified association
						out.println(nested()+r.getName().toLowerCase()+" = models.ManyToManyField('self', through='"+ r.getAssociationClass().getName()+ "'"+", related_name='"+r.getOwnEndName()+"', symmetrical=False, null="+nullFK+",blank="+nullFK+")");
				  }
				}
			}
		}
		decNested();
			
		//Redefining the string parameter to be used when referencing objects of this model 
		//
		incNested();
		int indexAttName=new Integer(o.getProperty("django.domain.str")).intValue();
		if(indexAttName<=c.getAtt().size())
		{
			out.println();
			out.println(nested()+ "def __unicode__(self):");
			incNested();
			out.println(nested()+"return smart_unicode(self."+ c.getAtt().get(indexAttName-1).getName()+ ", encoding='utf-8', strings_only=False, errors='strict')");
			decNested();
		}
		decNested();
	
		//adding the Meta internal class for additional configuration capabilities
		out.println();
		incNested();
		out.println(nested()+ "class Meta:");
		incNested();
		out.println(nested()+ "db_table = '" + c.getTableName()+"'");
		decNested();
		
		//Adding unique keys (possibly more than one)
		Iterator<S_Unique> itUS = c.getUniqueKeys().iterator();
		if (!(itUS.hasNext())) //If no unique key constraints we just close the class definition 
		   out.println();
		else
		{
			String uniqueTogether="unique_together = ("; //unique together is a list of lists
			while (itUS.hasNext())
			{
				S_Unique su=itUS.next();
				Iterator<Attribute> itUAtt= su.getRefAtt().iterator();
				String indexFields=""; 
				while (itUAtt.hasNext())
				{
					Attribute a=itUAtt.next();
					if (itUAtt.hasNext()) indexFields=indexFields+a.getName()+",";
					else indexFields=indexFields+a.getName();
				}
				uniqueTogether=uniqueTogether+ "("+indexFields+")";
			}
			uniqueTogether=uniqueTogether+")";
		}
		decNested();
		out.println();
	  }catch(Exception e)
		{ throw new PythonDomainGenerationException("Error when creating the domain Django schema");}
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
		if(at.getType() instanceof Enumeration)
		{ //Before the attribute we define the enumeration type
			Enumeration en=(Enumeration) at.getType();
			out.println(nested()+en.getName().toUpperCase()+" = (");
			incNested();
			out.println(nested()+ getEnumValues(en));
			decNested();
			out.println(nested()+")");
		}
		String attDef=at.getName()+" = " ; //start of the string
		
		//Detecting if the field is an AutoIncrement field (special type in Django)
		//Right now, only the PK attribute can be autoincrement
		if(at==atPK && auto) attDef=attDef + getAutoIncrementAttributeProperty(at);
		else attDef=attDef + getTypeInformation(at,at.getType()); //Adding type info
		
		//adding other properties
		String pk=getPrimaryAttributeProperty(at,atPK);
		if (pk!=null) attDef=setComma(attDef)+pk;
		
		String notnull=getNotNullAttributeProperty(at);
		if (notnull!=null) attDef=setComma(attDef)+notnull;
	
			
		attDef=attDef+")"; //closing and writing the definition
		out.println(nested() + attDef); 
	}
	
	
	protected String getPrimaryAttributeProperty(Attribute at, Attribute atPK)
	{
		String pk=null;
		if (at==atPK) pk="primary_key=True";
		return pk;				
	}
	
	protected String getAutoIncrementAttributeProperty(Attribute at)
	{
		return "models.AutoField(";				
	}
	

	protected String getNotNullAttributeProperty(Attribute at)
	{  //if the field is null in the database but we don´t indicate the blank option
		//then the validation will still request the user to enter info
		String notnull=null;
		if (at.getMin()>=1) notnull="null=False,blank=False";
		else notnull="null=True,blank=True";
		return notnull;				
	}
	
	//List of possible enumeration values. we assume that the code of enumeration literal is the literal itself
	protected String getEnumValues(Enumeration _en)
	{
		Iterator<String> itS=_en.getValues().iterator();
		String values="";
		while (itS.hasNext()) {
			String str = (String) itS.next();
			values=values+ "('"+str+"','"+str+"'),";
		}
		return values;				
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
		for(int i=0;i<nestedLevel;i++){tab=tab.concat("    ");}
		return tab;
	}
		
	
	protected String setComma(String _def)
	{
		//Checks if we need to add a comma after the String or not,
		//depending on whether the parameter we are going to add is the first one or not
		String def="";
		if (_def.substring(_def.length()-1).equals("("))
			def=_def;
		else def=_def+",";
		return def;
	}
	
	/****** DATA TYPE METHODS *****************************/

	protected String getTypeInfoDT_Boolean(Attribute a, Classifier d) {
	  String type="models.BooleanField(";
		return type;
	}


	protected String getTypeInfoDT_Date(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "models.DateField(auto_now=False, auto_now_add=False";
	}


	protected String getTypeInfoDT_DateTime(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "models.DateTimeField(auto_now=False, auto_now_add=False";
	}

   // the possible values for the enum are declared before the attribute. 
	// An enum attribute is declared by default as a char
	protected String getTypeInfoDT_Enumeration(Attribute a, Classifier d) {
		Enumeration e=(Enumeration) d;
		String type="models.CharField(max_length=" + e.getMaxLength()+ ",choices="+e.getName().toUpperCase();
		return type;
	}

	protected String getTypeInfoDT_Integer(Attribute a, Classifier d) {
		String type="";
		DT_Integer dI = (DT_Integer) d;
		type="models.IntegerField(";
		return type;
	}

	protected String getTypeInfoDT_LongInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		String type="";
		type="models.BigIntegerField(";
		return type;
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
		type="models.DecimalField(max_digits="+ dR.getLength()+",decimal_places="+dR.getPrecision();
		return type;
	}

//Fixed and variable lenght fields have the same type but for fixed ones we need to add the fixed:true property
	protected String getTypeInfoDT_String(Attribute a, Classifier d) {
		String type="";
		DT_String dS = (DT_String) d;
		type="models.CharField(max_length=" + dS.getLength();
		return type;
	}


	//Fixed and variable lenght fields have the same type but for fixed ones we need to add the fixed:true property
	protected String getTypeInfoDT_LongString(Attribute a, Classifier d) {
		String type="";
		DT_LongString dS = (DT_LongString) d;
		type="models.TextField(";
		return type;
	}
	
//The unsigned are generated as singed plus the "unsigned" validator set to true
	protected String getTypeInfoDT_UnsignedInteger(Attribute a, Classifier d) {
		// TODO Auto-generated method stub
		return "models.PositiveIntegerField(";
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
		return "models.PositiveSmallIntegerField(";
	}

	protected String getTypeInfoDT_ShortInteger(Attribute a, Classifier d) {
		return "models.SmallIntegerField(";
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
