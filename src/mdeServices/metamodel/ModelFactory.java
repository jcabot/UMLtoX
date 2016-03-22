package mdeServices.metamodel;

public class ModelFactory {

    //Creation of a new association end instance
    public static AssociationEnd createAssociationEnd(String name, Association as, Classifier type, int min, int max, ChangeabilityKind changeability,
    		VisibilityKind visibility, AggregationKind aggregation, boolean isStatic, boolean isNavigable)
    {
    	AssociationEnd ae=new AssociationEnd(name);
 		ae.setAss(as); ae.setSource(type);
 		ae.setMin(min); ae.setMax(max);  
 		ae.setChangeability(changeability);ae.setVisibility(visibility);
 		ae.setStatic(isStatic); ae.setNavigable(isNavigable); ae.setAggregation(aggregation);
 		as.addAssociationEnd(ae); //We link the association end with the association
 		return ae;
    }
	
    //Creation of a new attribute instance
    public static Attribute createAttribute(String name, Classifier type, Class cl, int min, int max,
    		VisibilityKind visibility, ChangeabilityKind changeability, boolean isStatic)
    {
    	Attribute at=new Attribute(name);
		at.setType(type); at.setSource(cl); at.setMin(min); at.setMax(max);
		at.setVisibility(visibility); at.setChangeability(changeability); at.setStatic(isStatic);
		cl.addAttribute(at);
		return at;
    }
    
  //Creates the right datatype instance depending on the type of the datatype
    public static DataType createDataType(String name) 
    {  //All comparison are done in UpperCase to avoid problems
    	DataType aux=null;
    	if (isIntegerDataType(name)) aux= new DT_Integer(name);
    	else if (isUnsignedIntegerDataType(name)) aux= new DT_UnsignedInteger(name);
    	else if (isShortIntegerDataType(name)) aux= new DT_ShortInteger(name);
    	else if (isUnsignedShortIntegerDataType(name)) aux= new DT_UnsignedShortInteger(name);
    	else if (isLongIntegerDataType(name)) aux= new DT_LongInteger(name);
    	else if (isUnsignedLongIntegerDataType(name)) aux= new DT_UnsignedLongInteger(name);
    	else if (isStringDataType(name)) aux= new DT_String(name); 
    	else if (isCharDataType(name)) aux= new DT_Char(name); 
    	else if (isBooleanDataType(name)) aux= new DT_Boolean(name); 
    	else if (isRealDataType(name)) aux= new DT_Real(name);
    	else if (isLongRealDataType(name))	aux= new DT_LongReal(name);
    	else if (isDateDataType(name)) aux= new DT_Date(name);
    	else if (isDateTimeDataType(name)) aux= new DT_DateTime(name);
    	else if (isCurrencyDataType(name)) aux= new DT_Currency(name);
    	else if (isVoidDataType(name)) aux= new DT_Void(name);
    	else if (isUnspecifiedDataType(name)) aux= new UserDefinedDataType(name);
    	//If the name is not recognized we just assume it is a user defined data type
    	else aux= new UserDefinedDataType(name);
       	return aux;
      }
	
    public static boolean isIntegerDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);
  	  	}
    	return (nameUpper.equals("INT")|| nameUpper.equals("EINT")||nameUpper.equals("INTEGER") || nameUpper.equals("EINTEGER") || nameUpper.equals("NUMERIC")  || nameUpper.equals("SIGNED INT"));
    }
    public static boolean isUnsignedIntegerDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
    	{
  	  	if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);
    	}
  	  	return (nameUpper.equals("UINT")|| nameUpper.equals("UNSIGNED INT")|| nameUpper.equals("EUNLIMITEDNATURAL") || nameUpper.equals("UNLIMITEDNATURAL"));
    } 
    
    public static boolean isShortIntegerDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);
  	  	}
  	  	return (nameUpper.equals("SHORT") ||nameUpper.equals("ESHORT") || nameUpper.equals("SIGNED SHORT") || nameUpper.equals("BYTE")|| nameUpper.equals("EBYTE")  || nameUpper.equals("SMALLINT") || nameUpper.equals("OCTET") || nameUpper.equals("SBYTE")  || nameUpper.equals("SHORT INT") );
    } 
    
    public static boolean isUnsignedShortIntegerDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{
    	if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);
  	  	}
    	return (nameUpper.equals("USHORT")|| nameUpper.equals("UNSIGNED SHORT") || nameUpper.equals("UNSIGNED SHORT INT"));
    } 
    
    public static boolean isLongIntegerDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);
  	  	}
    	return (nameUpper.equals("LONG") || nameUpper.equals("ELONG") ||nameUpper.equals("BIGINTEGER") || nameUpper.equals("EBIGINTEGER")|| nameUpper.equals("INT64") || nameUpper.equals("LONGINT") || nameUpper.equals("LONG INT") || nameUpper.equals("LONG LONG")  || nameUpper.equals("SIGNED LONG"));
    } 
    
    public static boolean isUnsignedLongIntegerDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{
    		if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);
  	  	}
    	return (nameUpper.equals("UNSIGNED LONG") || nameUpper.equals("ULONG") || nameUpper.equals("UNSIGNED LONG INT") || nameUpper.equals("UNSIGNED LONG LONG")) ;
    } 
	
    public static boolean isStringDataType(String name)
    {
    	String nameUpper=name.toUpperCase();	
    	//Removing the (idl) suffix of some Visio datatypes
  	  	if(nameUpper.length()>5)
  	  	{
  	  		if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
  	  			nameUpper=nameUpper.substring(0, nameUpper.length()-5);
  	  	}
  	  	return (nameUpper.equals("STRING") || nameUpper.equals("ESTRING") ||nameUpper.equals("CHAR[]") || nameUpper.equals("WSTRING") || nameUpper.equals("WCHAR_T") ||nameUpper.equals("VARCHAR") ||nameUpper.equals("VARCHAR2") ||nameUpper.equals("WCHAR") ||nameUpper.equals("SEQUENCE") );
    } 
    
    public static boolean isCharDataType(String name)
    { //No distinction between signed and unsigned chars
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{	if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("CHAR") ||nameUpper.equals("ECHAR") || nameUpper.equals("CHARACTER") || nameUpper.equals("WCHAR") || nameUpper.equals("UNSIGNED CHAR")  || nameUpper.equals("SIGNED CHAR"));
    } 
    
    public static boolean isBooleanDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("BOOLEAN") || nameUpper.equals("EBOOLEAN") || nameUpper.equals("BIT") || nameUpper.equals("BOOL") );
    } 
    
    public static boolean isRealDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("FLOAT") || nameUpper.equals("EFLOAT") ||nameUpper.equals("REAL") || nameUpper.equals("DECIMAL") || nameUpper.equals("NUMBER"));
    } 
	
    public static boolean isLongRealDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("DOUBLE") || nameUpper.equals("BIGDECIMAL") ||nameUpper.equals("EDOUBLE") ||  name.equals("EBIGDECIMAL")|| name.equals("LONG DOUBLE"));
    } 

    public static boolean isDateDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{	if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("DATE") || nameUpper.equals("EDATE"));
    } 

    public static boolean isDateTimeDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("TIME") || nameUpper.equals("TIMESTAMP") || nameUpper.equals("DATETIME") || nameUpper.equals("EDATETIME"));
    } 

    public static boolean isCurrencyDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("CURRENCY"));
    } 
    
    public static boolean isVoidDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("VOID"));
    } 
	
    public static boolean isUnspecifiedDataType(String name)
    {
    	String nameUpper=name.toUpperCase();
    	//Removing the (idl) suffix of some Visio datatypes
    	if(nameUpper.length()>5)
  	  	{if (nameUpper.substring(nameUpper.length()-5).equals("(IDL)"))
		  nameUpper=nameUpper.substring(0, nameUpper.length()-5);}
    	return (nameUpper.equals("<SIN ESPECIFICAR>") || nameUpper.equals("<UNSPECIFIED>") || nameUpper.equals("<USPECIFICERET>") || nameUpper.equals("<NICHT SPEZIFIZIERT>"));
    } 
    
    public static String normalize(String name)
    {
    	//removing all strange characters
    	//name= Normalizer.normalize(name, Normalizer.Form.NFD); //Normalizer cause the replaceAll fail becuase
    	//it puts the accents in a different char than the letter (e.g. [a,´,...]
    	name = name.replaceAll("[èéêë]","e"); 
    	name = name.replaceAll("[ûùúü]","u"); 
    	name = name.replaceAll("[ïîíì]","i"); 
    	name = name.replaceAll("[àâá]","a"); 
    	name = name.replaceAll("[ôóò]","o"); 
    	name = name.replaceAll("ç","c"); 
    	name = name.replaceAll("[ÈÉÊË]","E"); 
    	name = name.replaceAll("[ÛÙÚÜ]","U"); 
    	name = name.replaceAll("[ÏÎÍÌ]","I"); 
    	name = name.replaceAll("[ÀÂÁ]","A"); 
    	name = name.replaceAll("[ÔÓÒ]","O"); 
    	name = name.replaceAll("Ç","C"); 
    	name = name.replaceAll("<","");
    	name = name.replaceAll(">",""); 
   		return name; 
    }
   
	    
  
}
