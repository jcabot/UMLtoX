package mdeServices.transformations.database;

import java.util.Iterator;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.ChangeabilityKind;
import mdeServices.metamodel.VisibilityKind;
import mdeServices.metamodel.stereotypes.S_PrimaryKey;
import mdeServices.options.Options;

import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;

/**
 * Transformation class for adding primary keys to the classes in the model
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AddPKeys extends Transformation{
	
	//Model where to apply the transformation
	protected StaticModel m;

	//Name for the PK attributes
	String name;
	
	public T_AddPKeys(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		this.o=o; 
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Class> itCl;
		boolean auto=o.getProperty("db.constraint.pk.autoincrement").equals("true");
		if (o.getProperty("db.pk.generation").equals("new")) // a new attribute is added to be the PK for the class
		{
			name=this.o.getProperty("db.pk.att.name");
			while (itP.hasNext())
			{
				Package p=itP.next();
				itCl=p.getAllClassesNoSuperType().iterator();
				while (itCl.hasNext())
				{
					Class cl=itCl.next();
					if (cl.getPrimaryKey()==null && !(cl instanceof AssociationClass)) //If the class does not have a PK already 
					{
						createNewPrimaryKeyAttribute(name,cl,auto);
						
					}
				}
			}
		}
		else if (o.getProperty("db.pk.generation").equals("first"))
		{
			while (itP.hasNext())
			{
				Package p=itP.next();
				itCl=p.getAllClassesNoSuperType().iterator();
				while (itCl.hasNext())
				{
					Class cl=itCl.next();
					if (cl.getPrimaryKey()==null && !(cl instanceof AssociationClass)) //If the class does not have a PK already 
					{
						if(!(cl.getAtt().isEmpty()))
						{
								Attribute a=cl.getAtt().firstElement();
								//We must change some of the attribute values to make sure it is adequate
								a.setChangeability(ChangeabilityKind.C_CHANGEABLE);
								a.setMax(1);
								a.setMin(1);
								a.setStatic(false);
								a.setVisibility(VisibilityKind.V_PUBLIC);
								S_PrimaryKey s= new S_PrimaryKey();
								s.setRefAtt(a);s.setAutoIncrement(auto);
								cl.setPrimaryKey(s);
						}
						else createNewPrimaryKeyAttribute(name, cl, auto);  //if there are no attributes we need to create a new attribute for the PK
					}
				}
			}
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when creating PKs for classes");}

	}
	
	private void createNewPrimaryKeyAttribute(String name, Class cl, boolean auto)
	{
		Attribute a= new Attribute(name);
		cl.addAttributeBeginning(a);
		a.setChangeability(ChangeabilityKind.C_CHANGEABLE);
		a.setMax(1);
		a.setMin(1);
		a.setSource(cl);
		a.setStatic(false);
		a.setVisibility(VisibilityKind.V_PUBLIC);
		a.setType(m.findDataType("Integer"));
		S_PrimaryKey s= new S_PrimaryKey();
		s.setRefAtt(a);s.setAutoIncrement(auto);
		cl.setPrimaryKey(s);
	}
	
	//Returns the position of the class in Vector classOwnPk (if exists)
	/*private int hasOwnPk(String name)
	{
	  boolean found=false; int i=0;
	  Iterator<String> it=classOwnPk.iterator();
	  while (it.hasNext() && !found)
	  {
		 if (it.next().equals(name)) found=true;
		 else i=i+1;
	  }
	if (found) return i;
	else return -1;
		
	}
	
	private Attribute selectIDAttribute(Class c, int position)
	{
	  boolean found=false; Attribute a=null;
	  Iterator<Attribute> it=c.getAtt().iterator();
	  while (it.hasNext() && !found)
	  {
		 a=it.next();
		 if (a.getName().equals(pk_Atts.get(position))) found=true;
	  }
	return a;
		
	}*/
}
