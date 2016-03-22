package mdeServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.gui.CheckBox;
import mdeServices.metamodel.gui.ComboBox;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.gui.EditBox;
import mdeServices.metamodel.gui.Form;
import mdeServices.metamodel.gui.FormActionKind;
import mdeServices.metamodel.gui.GUIElement;
import mdeServices.metamodel.gui.Grid;
import mdeServices.metamodel.gui.GridColumn;
import mdeServices.metamodel.gui.MenuItem;
import mdeServices.metamodel.gui.Menu;
import mdeServices.metamodel.gui.TFieldControl;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.transformations.gui.T_AddHomeMenuItem;


/**
 *  Class for generating a PHP application 
 *   
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class AppGeneratorPHP extends AppGenerator{

	
	public AppGeneratorPHP() {
		super();
	}
	
	@Override
	protected void additionalTransformations() throws TransformationNotApplicable {
		Transformation t= new T_AddHomeMenuItem(p,o,l);
		t.exec();
	}

	@Override
	protected void generateScripts() throws FileNotFoundException {
		
		// TODO Auto-generated method stub
		DynamicModel d=p.getDynamicModel();
		createUtilsFile();
		if (d.getLoginForm()!=null)
		{
			createConnectionFile(d.getLoginForm());
			generateLoginForm();
			generateHomePage(d.getLoginForm());
			generateMainForm(true);
		}
		else 
		{
			createConnectionFile(d.getInitialForm());
			generateHomePage(d.getInitialForm());
			generateMainForm(false);
		}
		Iterator<Form> itF= d.getForms().iterator();
		while (itF.hasNext()) {
			Form form = (Form) itF.next();
			if (form.getActionKind()==FormActionKind.FA_VISUALIZE)
				generateVisualizeForm(form);
		//	else if (form.getActionKind()==FormActionKind.FA_VISDETAILS)
		//		generateVisDetailsForm(form);
			else if (form.getActionKind()==FormActionKind.FA_INSERT)
				generateInsertForm(form);
		//	else if (form.getActionKind()==FormActionKind.FA_UPDATE)
		//		generateUpdateForm(form);*/
		}
	}
		
	protected void generateLoginForm() throws FileNotFoundException
	{
	  //Generation of the login form
		Form f=d.getLoginForm();
		File fForm= new File(baseDirApp+File.separator+f.getName()+filePHPExtension());
	    PrintWriter out= new PrintWriter(fForm);
	    out.println(nested()+"<?php");
	    genInclude(out, o.getProperty("app.utils.file.name") + filePHPExtension() );
	    genInclude(out, o.getProperty("app.connection.file.name") + filePHPExtension() );
	    out.println(nested()+"$connString = \"" + dbGen.getPHPDBConnection(o.getProperty("app.database.host"),o.getProperty("app.database.name"))+"\";");
	    //If we have submitted the form we try to connect 
	    out.println(nested()+"if (($_POST['login']!=\"\") && ($_POST['password']!=\"\"))"); 
	    out.println(nested()+ "{");
	    incNested();
	    out.println(nested()+ "try");
	    out.println(nested()+ "{");
	    incNested();
	    out.println(nested()+"connect($connString, $_POST['login'], $_POST['password']);");
	    //If we reach this line, the connection was successful and we can access the main page 
	    addComment(out,l.getString("app.comment.code.connection.succesful"));
	    out.println(nested()+ "header(\"location:"+d.getInitialForm().getName()+filePHPExtension()+"\");");
	    decNested();
	    out.println(nested()+ "}");
	    out.println(nested()+"catch(PDOException $e)");
	    out.println(nested() + "{");
	    incNested();
	    out.println(nested()+"$warnings[] = \"" + l.getString("error.connecting.database") + "\";");
	    out.println(nested()+"$warnings[] = $e->getMessage();");
	    decNested();
	    out.println(nested()+ "}");
	    decNested();
	    out.println(nested()+ "}");
	  //Informing that both the user name and password must be entered
	    out.println(nested() + "else { if ($_POST[submit]!=\"\") {$warnings[] = \"" + l.getString("error.connecting.database.info.missing")+ "\";}}");
	    decNested();
	    out.println(nested()+"?>");
	    //If the form was not previously submitted or it has errors we show the form 
	 // 	generateHTMLPageHead(out,f.getCaption());
	    addHTMLHead(out, f.getCaption());
	    generateHTMLWarnings(out);
		out.println(nested()+"<?php"); //If no warnings, we show the form
		incNested();
		generateHTMLForm(out,f);
		decNested();
		out.println(nested()+"?>"); //If no warnings, we show the form
		
		//	generateHTMLPageTail(out);
	    addHTMLEnd(out);
	   	out.flush();
	   	out.close();
	}
	
	protected void generateMainForm(boolean loginForm) throws FileNotFoundException
	{
		Form f=d.getInitialForm();
		File fForm= new File(baseDirApp+File.separator+f.getName()+filePHPExtension());
	    PrintWriter out= new PrintWriter(fForm);
	    out.println("<?php");
	    genInclude(out, o.getProperty("app.utils.file.name") + filePHPExtension() );
	    genInclude(out, o.getProperty("app.connection.file.name") + filePHPExtension() );
	   	if (!loginForm)
		{  //We set the connection in this form using the default values.
		    out.println(nested()+"$connString = \"" + dbGen.getPHPDBConnection(o.getProperty("app.database.host"),o.getProperty("app.database.name"))+"\";");		out.println(nested()+"$login =" + o.getProperty("app.database.login") );
			out.println(nested()+"$password =" + o.getProperty("app.database.password"));
			out.println(nested()+"connect($connString, $login, $password);");
			//If the connection is not successful, the connect function already displays an error page and exits
		}
	   	out.println("?>");
	   	generateHTMLPageHead(out,f.getCaption());
	   	out.println(nested() + "<h2>" + l.getString("app.caption.form.main.prefix") + "</h2>");
	   	out.println(nested() + "<h3>" + l.getString("app.caption.form.main.suffix") + "</h3>");
	   	generateHTMLPageTail(out);
	   	out.flush();
	   	out.close();
	}
	
	protected void generateVisualizeForm(Form form) throws FileNotFoundException 
	{
	  	File fForm= new File(baseDirApp+File.separator+form.getName()+filePHPExtension());
	    PrintWriter out= new PrintWriter(fForm);
	    File fFormAux= new File(baseDirApp+File.separator+o.getProperty("app.form.db.prefix") + form.getName()+ o.getProperty("app.form.db.suffix") + filePHPExtension());
	    PrintWriter outAux= new PrintWriter(fFormAux);
	    //Creation of the contents of the auxiliary file
	    outAux.println("<?php");
	    generateSelects(outAux, form);
	    generateSQLDelete(outAux,form.getBaseClass());
	    outAux.println("?>");
	    phpHeading(out,form);
	    Iterator<GUIElement> itEls=form.getItems().iterator();
	    while (itEls.hasNext()) {
			GUIElement element = (GUIElement) itEls.next();
			if (element instanceof Grid)
			{
				Grid gr=(Grid) element;
				out.println(nested() + "try");
			    out.println(nested() + "{");
			    incNested();
			    out.println(nested()+"if(isset($_REQUEST['id']))");
			    out.println(nested()+"{");
			    incNested();
			    out.println(nested()+ "delete"+ form.getBaseClass().getTableName()+ "($conn,$_REQUEST['id']);");
			    decNested();
			    out.println(nested()+"}");
			    out.println(nested() + "global $" + gr.getName()+"s ;");
			    out.println(nested() + "$" + gr.getName()+ "s = select"+ gr.getName()+"($conn);");
			    decNested();
			    out.println(nested() + "}");
			    out.println(nested()+"catch(PDOException $e)");
			    out.println(nested() + "{");
			    incNested();
			    out.println(nested()+"$warnings[] = $e->getMessage();");
			    decNested();
			    out.println(nested() + "}");
			}
	    }
	    phpEnd(out);
		generateHTMLPageHead(out,form.getCaption());
		generateHTMLWarnings(out); //Printing of possible warnings during the processing of submitted forms
		out.println(nested()+"<?php if(!count($warnings)) {"); //If no warnings, we show the form
		incNested();
		generateHTMLForm(out,form);
		out.println(nested()+"}");
		decNested();
		out.println(nested() + "?>");
	   	generateHTMLPageTail(out);
	    outAux.flush();
	    outAux.close();
	    out.flush();
	    out.close();
	}
	
	protected void generateVisDetailsForm(Form form) throws FileNotFoundException 
	{
	  	File fForm= new File(baseDirApp+File.separator+form.getName()+filePHPExtension());
	    PrintWriter out= new PrintWriter(fForm);
	    
	    File fFormAux= new File(baseDirApp+File.separator+o.getProperty("app.form.db.prefix") + form.getName()+ o.getProperty("app.form.db.prefix") + filePHPExtension());
	    PrintWriter outAux= new PrintWriter(fForm);
	    //Creation of the contents of the auxiliary file
	    outAux.println("<?php");
	    generateSelects(outAux, form);
	    outAux.println("?>");
	    phpHeading(out,form);
	    genInclude(out,o.getProperty("app.form.db.prefix") + form.getName()+ o.getProperty("app.form.db.prefix"));
	    Iterator<GUIElement> itEls=form.getItems().iterator();
		out.println(nested() + "try");
	    out.println(nested() + "{");
	    incNested();
	    //Select for the main elements in the form
	    out.println(nested() + "$" + form.getName()+ " = select"+ form.getName()+ "($_REQUEST['" + form.getBaseClass().getPrimaryKey().getName() +"'];");
	    //Definition of individual variables plus additional selects for grids and combo boxes 
	    while (itEls.hasNext()) {
			GUIElement element = (GUIElement) itEls.next();
			if (element instanceof Grid)
			{
				Grid gr=(Grid) element;
				out.println(nested() + "$" + gr.getName()+ " = select"+ gr.getName());
		   	}
			else if (element instanceof ComboBox)
			{
				ComboBox c=(ComboBox) element;
				out.println(nested() + "$" + c.getName()+ " = select"+ c.getName()+"($" + form.getName() + "['" + c.getOwn().getName()+ "'])");
		   	}
			else if (element instanceof TFieldControl)
			{
				out.println(nested() +"$" + element.getName()+ " = $" + form.getName() + "['" + element.getName() + "'];" );
			}
	    }
	    out.println(nested() + "}");
	    decNested();
	    out.println("catch(PDOException $e)");
	    out.println(nested() + "{");
	    out.println("$warnings[] = e->message()'");
	    phpEnd(out);
		generateHTMLPageHead(out,form.getCaption());
		generateHTMLWarnings(out); //Printing of possible warnings during the processing of submitted forms
	//	out.println(nested()+"<?php if(count($warnings)) {"); //If no warnings, we show the form
	//	incNested();
		generateHTMLForm(out,form);
	   	generateHTMLPageTail(out);
	  //out.println(nested()+" } ?>");
	    outAux.flush();
	    outAux.close();
	    out.flush();
	    out.close();
	}
	
	protected void generateUpdateForm(Form form) throws FileNotFoundException 
	{
	  	File fForm= new File(baseDirApp+File.separator+form.getName()+filePHPExtension());
	    PrintWriter out= new PrintWriter(fForm);
	    
	    File fFormAux= new File(baseDirApp+File.separator+o.getProperty("app.form.db.prefix") + form.getName()+ o.getProperty("app.form.db.prefix") + filePHPExtension());
	    PrintWriter outAux= new PrintWriter(fForm);
	    //Creation of the contents of the auxiliary file
	    outAux.println("<?php");
	    generateSelects(outAux, form);
	    generateSQLUpdate(outAux,form);
	    outAux.println("?>");
	    phpHeading(out,form);
	    genInclude(out,o.getProperty("app.form.db.prefix") + form.getName()+ o.getProperty("app.form.db.prefix"));
	    Iterator<GUIElement> itEls=form.getItems().iterator();
	    //Checking if the form has been submitted, which implies that we need to update the data
	    out.println(nested() +  "if $_POST['submit']");
	    out.println(nested() + "{");
	    incNested();
		out.println(nested() + "try");
	    out.println(nested() + "{");
	    incNested();
	    out.println(nested() + "update" + form.getName());
	    //If we are here the update has been successful we can go back to the previous page
	    out.println(nested() + "header(\"Location: $REQUEST['"+ o.getProperty("app.back.link.param.name")+ "']");
	    out.println(nested() + "}");
	    decNested();
	    out.println("catch(PDOException $e)");
	    out.println(nested() + "{");
	    out.println("$warnings[] = e->message()'");
	    out.println(nested() + "}");
	    decNested();
	    out.println(nested() + "}");
	    //If not, we show the data in the form
	    out.println(nested() +  "else");
	    out.println(nested() + "{");
	    incNested();
	    out.println(nested() + "try");
	    out.println(nested() + "{");
	    incNested();
	    //Select for the main elements in the form
	    out.println(nested() + "$" + form.getName()+ " = select"+ form.getName());
	    //Definition of individual variables plus additional selects for grids and combo boxes 
	    while (itEls.hasNext()) {
			GUIElement element = (GUIElement) itEls.next();
			if (element instanceof Grid)
			{
				Grid gr=(Grid) element;
				out.println(nested() + "$" + gr.getName()+ " = select"+ gr.getName());
		   	}
			else if (element instanceof ComboBox)
			{
				ComboBox c=(ComboBox) element;
				out.println(nested() + "$" + c.getName()+ " = select"+ c.getName()+"($" + form.getName() + "['" + c.getOwn().getName()+ "'])");
		   	}
			else if (element instanceof TFieldControl)
			{
				out.println(nested() +"$" + element.getName()+ " = $" + form.getName() + "['" + element.getName() + "'];" );
			}
	    }
	    out.println(nested() + "}");
	    decNested();
	    out.println("catch(PDOException $e)");
	    out.println(nested() + "{");
	    out.println("$warnings[] = e->message()'");
	    out.println(nested() + "}");
	    decNested();
	    out.println(nested() + "}");
	    decNested();
	    phpEnd(out);
		generateHTMLPageHead(out,form.getCaption());
		generateHTMLWarnings(out); //Printing of possible warnings during the processing of submitted forms
	//	out.println(nested()+"<?php if(count($warnings)) {"); //If no warnings, we show the form
	//	incNested();
		generateHTMLForm(out,form);
	   	generateHTMLPageTail(out);
	  //out.println(nested()+" } ?>");
	    outAux.flush();
	    outAux.close();
	    out.flush();
	    out.close();
	}

	
	protected void generateInsertForm(Form form) throws FileNotFoundException 
	{
	  	File fForm= new File(baseDirApp+File.separator+form.getName()+filePHPExtension());
	    PrintWriter out= new PrintWriter(fForm);
	    File fFormAux= new File(baseDirApp+File.separator+o.getProperty("app.form.db.prefix") + form.getName()+ o.getProperty("app.form.db.suffix") + filePHPExtension());
	    PrintWriter outAux= new PrintWriter(fFormAux);
	    //Creation of the contents of the auxiliary file
	    outAux.println("<?php");
	   // generateSelects(outAux, form);
	    generateSQLInsert(outAux,form);
	    outAux.println("?>");
	    phpHeading(out,form);
	    //Checking if the form has been submitted, which implies that we need to update the data
	    out.println(nested() +  "if (isset($_POST['submit']))");
	    out.println(nested() + "{");
	    incNested();
		out.println(nested() + "try");
	    out.println(nested() + "{");
	    incNested();
	    out.println(nested() + "insert" + form.getBaseClass().getName()+"($conn);");
	    //If we are here the insert has been successful we can go back to the previous page
	    out.println(nested() + "header(\"location:\".$_REQUEST['"+ o.getProperty("app.back.link.param.name")+ "']);");
	    decNested();
	    out.println(nested() + "}");
	    out.println(nested()+"catch(PDOException $e)");
	    out.println(nested() + "{");
	    incNested();
	    out.println(nested()+"$warnings[] = $e->getMessage();");
	    decNested();
	    out.println(nested() + "}");
	    decNested();
	    out.println(nested() + "}");
	    phpEnd(out);
		generateHTMLPageHead(out,form.getCaption());
		generateHTMLWarnings(out); //Printing of possible warnings during the processing of submitted forms
		out.println(nested()+"<?php "); //If no warnings, we show the form
		incNested();
		generateHTMLForm(out,form);
		decNested();
		out.println(nested() + "?>");
	   	generateHTMLPageTail(out);
	    outAux.flush();
	    outAux.close();
	    out.flush();
	    out.close();
	}

	protected void  generateSelects(PrintWriter out, Form form)
	{ //Creation of a new select function for each grid in the form
	  //and another for the main class of the form (for forms of a type different from FA_VISUALIZE)
		if (form.getActionKind()!=FormActionKind.FA_VISUALIZE)
			generateSQLSelect(out, form.getName(), form.getBaseClass().getTableName(),form.getBaseClass().getAtt(), form.getBaseClass().getPrimaryKeyAttribute(),null,true);
	 	Iterator<GUIElement> itEls=form.getItems().iterator();
		Vector<Attribute> vAtSel= new Vector<Attribute>(0,1);
		while (itEls.hasNext()) {
			GUIElement element = (GUIElement) itEls.next();
			if (element instanceof Grid)
			{   
				Grid gr= (Grid) element;
				Iterator<GridColumn> itGC = gr.getColumns().iterator();
				Vector<Attribute> vAtCols= new Vector<Attribute>(0,1);
				while (itGC.hasNext()) {
					GridColumn gridColumn = (GridColumn) itGC.next();
					vAtCols.add(gridColumn.getAtt());
				}
				generateSQLSelect(out, gr.getName(), gr.getBaseClass().getTableName(), vAtCols, gr.getParam(), gr.getOrder(), gr.getBaseClass()!=form.getBaseClass());  
			}
			if (element instanceof ComboBox)
			{   ComboBox c=(ComboBox) element;
				if (form.getActionKind()==FormActionKind.FA_VISDETAILS)
				{//We only need to retrieve the show attributes of the linked row
					generateSQLSelect(out,c.getName(), c.getRefClass().getTableName(), c.getShow(), c.getRefClass().getPrimaryKeyAttribute(), null ,true);  
				}
				else
				{ //We retrieve all the data of the related table so that the user can select a new link
					Vector<Attribute> atShow= (Vector) c.getShow().clone();
					atShow.add(c.getRefClass().getPrimaryKeyAttribute());
					generateSQLSelect(out,c.getName(), c.getRefClass().getTableName(), c.getShow(), null, c.getOrder() ,false);  
				}
			}
		}
	}
			
	protected void generateSQLSelect(PrintWriter out, String name, String table, Vector<Attribute> vAtts, Attribute param, Vector<Attribute> vOrder, boolean bWhere)
	{
		if (param!=null) out.println(nested()+"function select" + name + "($conn,$selector)");
		else out.println(nested()+"function select" + name + "($conn)");
		out.println(nested() + "{");
		incNested();
		String select="";
		Iterator<Attribute> itSel=vAtts.iterator();
		while (itSel.hasNext()) {
			Attribute attribute = (Attribute) itSel.next();
			select= select + attribute.getName();
			if (itSel.hasNext()) select= select + ", ";
		}
		String sql= "SELECT " + select + " FROM " + table;
	   //If we need to show only part of the data we add a where clause
		String where="";
		if (bWhere)
		{
			where="WHERE " + param.getName() + " =  $selector ";
		}
		String order="";
		if(vOrder.size()>0)
		{ order=" ORDER BY ";
		 Iterator<Attribute> itAt= vOrder.iterator();
		  while (itAt.hasNext()) {
			Attribute attribute = (Attribute) itAt.next();
			order = order + attribute.getName();
			if (itAt.hasNext()) order= order + ",";
		 }
		}
		if (!bWhere)
		{
			out.println(nested()+ "$sql=" + "\"" + sql + order + "\";");
		}
		else
		{//Even if the query must have a where clause, this clause only makes sense when
			//the primary key used in the where has a value (e.g. for insert forms the initial value will be empty)
			out.println(nested() + "if ($selector) {");
			out.println(nested()+"$sql=" + "\"" + sql + where + order + "\";");
			out.println(nested() + "}");
			out.println(nested()+ "else {");
			out.println(nested()+ "$sql=" + "\"" + sql + order + "\";");
			out.println(nested() + "}");
			out.println(nested() + order);
		}
		out.println(nested() + "$q = $conn->query($sql);");
		out.println(nested() + "return $q ;");
		decNested();
		out.println(nested() + "}");
	}		
			
	protected void generateSQLDelete(PrintWriter out, Class cl)
	{
		
		String sql= "DELETE FROM " + cl.getTableName() + " WHERE " + cl.getPrimaryKey().getName()+ " = $pk";
		out.println(nested()+"function delete" + cl.getTableName() + "($conn,$pk)");
		out.println(nested() + "{");
		incNested();
		out.println(nested()+"$sql=" + "\"" + sql + "\";");
		out.println(nested() + "$q = $conn->query($sql);");
		decNested();
		out.println(nested() + "}");
	}		
			
	
	protected void generateSQLUpdate(PrintWriter out, Form form)
	{
	    out.println(nested() + "function update" + form.getName() + "()");
		out.println(nested() + "{");
		incNested();
		Iterator<Attribute> itSel=form.getBaseClass().getAtt().iterator();
		out.println(nested() + "$sql= \"UPDATE TABLE " + form.getBaseClass().getTableName()+ "\"");
		while (itSel.hasNext()) {
		    Attribute attribute = (Attribute) itSel.next();
		    if (!attribute.isPartOfPK())
		    {
		    	out.println(nested() + "if ($_POST['" + attribute.getName() +  "'])");
		    	out.println(nested() + "{");
		    	incNested();
		    	if (itSel.hasNext()) 
		    		out.println(nested() +  " $sql = $sql . \"set " + attribute.getName() + "= $_POST['" + attribute.getName() +  "']),\";");
		    	else
		    		out.println(nested() +  " $sql = $sql . \"set " + attribute.getName() + "= $_POST['" + attribute.getName() +  "'])\";");
				
		    	out.println(nested() + "}");
		    	decNested();
		    }
		}
		out.println("$sql= $sql . \" WHERE " + form.getBaseClass().getPrimaryKey() + " = $_POST['" + form.getBaseClass().getPrimaryKey() +  "']),\";");
		out.println(nested() + "$q = (global) $conn->query($sql)");
		out.println(nested() + "return $q; }");
		decNested(); 
	}	
	
	protected void generateSQLInsert(PrintWriter out, Form form)
	{
	    out.println(nested() + "function insert" + form.getBaseClass().getName() + "($conn)");
		out.println(nested() + "{");
		incNested();
		Iterator<Attribute> itSel=form.getBaseClass().getAtt().iterator();
		out.println(nested() + "$sql= \"INSERT INTO " + form.getBaseClass().getTableName()+ " (\";");
		out.println(nested() + "$values= \" VALUES (\";");
		boolean autoIncrement= form.getBaseClass().isPKAutoIncrement() && dbGen.isAutoIncrementPossible();
		while (itSel.hasNext()) {
		    Attribute attribute = (Attribute) itSel.next();
		    if (! (autoIncrement && attribute.isPartOfPK()))
		    {
		    	out.println(nested() + "if ($_POST['" + attribute.getName() +  "'])");
		    	out.println(nested() + "{");
		    	incNested();
		    	String termInsert="";
		    	String termValues="";
		    	String stAttrib="";
		    	if (attribute.getType() instanceof DT_String)
		    		stAttrib="\"'\".";
		    	if (itSel.hasNext()) 
		    	{
		    		termInsert=",\";"; termValues="',';";
		    	}
		    	else
		    	{
		    		termInsert=")\";"; termValues="\")\";";
		    	}
		    	out.println(nested() +  " $sql = $sql . \""+ attribute.getName() + termInsert);
		    	out.println(nested() +  " $values = $values ." +stAttrib+" $_POST['" + attribute.getName() +"']." + stAttrib + termValues);
		    	decNested();
		       	out.println(nested() + "}");
		    }
		}
		out.println(nested()+"$sql= $sql . $values;");
		out.println(nested() + "$q = $conn->query($sql);");
		decNested(); 
		out.println(nested() + "}");
		
	}	
	
    protected void generateHTMLWarnings(PrintWriter out)
    {
	out.println(nested()+"<?php");
	out.println(nested() + "if(count($warnings)) {");
	incNested();
	out.println(nested() + "echo \"<b>" + l.getString("error.correction.request") + "</b><br>\";");
	out.println(nested() +"foreach($warnings as $w)");
	out.println(nested() + "{");
	incNested();
	out.println(nested() +"echo \"- \", htmlspecialchars($w), \"<br>\";");
	decNested();
	out.println(nested() + "}");
	decNested();
	out.println(nested() + "}");
	out.println(nested()+"?>");
    }
		
    protected  void generateHTMLForm(PrintWriter out, Form f)
    {
    	if (f.getActionKind()==FormActionKind.FA_INSERT || f.getActionKind()==FormActionKind.FA_UPDATE || f.getActionKind()==FormActionKind.FA_VISDETAILS  || f==d.getLoginForm())
    	{
    		String action="";
    		if (f.getB_Submit()!=null) action= f.getB_Submit().getDestination().getName()+filePHPExtension();
    		else if (f.getB_Close()!=null) action= f.getB_Close().getDestination().getName()+filePHPExtension();
    		out.println(nested()+ "echo \"<FORM action=\\\""+ action+"\\\" method=\\\"post\\\">\";");
    		incNested();
    		out.println(nested() + "echo '<TABLE>';");
    		Iterator<GUIElement> it= f.getItems().iterator();
    		int numRow=new Integer(o.getProperty("app.form.numelements.row")).intValue();
    		int i=0;
    		while (it.hasNext()) {
    			GUIElement element = (GUIElement) it.next();
    			if (!(element instanceof Grid))
    			{
    				if ( (i % numRow) == 0) //Every time that the module is zero we need to start a new row
    				{ 
    					out.println(nested()+ "echo '<TR>';"); 
    				}
    				if (element instanceof EditBox) generateHTMLEditBox(out,f,(EditBox) element,i);
    				else if (element instanceof CheckBox) generateHTMLCheckBox(out,f,(CheckBox) element,i);
    				else if (element instanceof ComboBox) generateHTMLComboBox(out,f,(ComboBox) element,i);
    				i=i+1;
    			}
    		}
    		//Adding the submit button to the form
    		if (f.getB_Submit()!=null)
    		{
    			out.println(nested()+ "echo '<TR>';"); 
    			out.println(nested() + "echo \" <TD> <button name=\\\"submit\\\" type=\\\"submit\\\" tabindex=\\\"" + i + "\\\">" + f.getB_Submit().getCaption() + "</button> </TD>\";");
    	   	}
    		out.println(nested() + "echo '</TABLE>';");
        	out.println(nested() + "echo '</FORM>';");
       	}
    	
     	//Adding the grids showing related elements
    	Iterator<GUIElement> it= f.getItems().iterator();
    	 while (it.hasNext()) {
			GUIElement el= it.next();
			if (el instanceof Grid) generateHTMLGrid(out,f,(Grid) el);
			
		}
    	//Adding the insert button to open an insert form
     	if (f.getB_Insert()!=null)
     	{
     		out.println(nested()+"echo \"<a href=\"" + ",'"+ f.getB_Insert().getDestination().getName()+ filePHPExtension()+ "',\">" + f.getB_Insert().getDestination().getCaption()+"</a>\";");
     	}
    	 //Adding the close button at the end
     	if (f.getB_Close()!=null)
     	{
     		out.println(nested() + "echo \"<a href=\"" + ",'"+ f.getB_Close().getDestination().getName()+ filePHPExtension()+ "',\">" + f.getB_Close().getCaption()+"</a>\";");
     	}
    }
    
    protected void generateHTMLEditBox(PrintWriter out, Form form, EditBox ed, int index)
    {
    	out.println(nested() + "echo \"<TD><LABEL for=\\\"" + ed.getName() + "\\\">"+ ed.getCaption()+"</LABEL>\";");
    	String tabIndex="";
    	if (o.getProperty("app.tab.order").equals("true")) tabIndex=" tabindex = \\\"" + index + "\\\"";
    	String readOnly="";
    	if (ed.isReadOnly()) readOnly= " readonly=\\\"true\\\"";
    		
    	if (!ed.isPassword())
    	{
    		if (ed.getAttribute()!=null)
    			out.println(nested() + "echo \"<TD><INPUT type=\\\"text\\\" name=\\\""+ed.getAttribute().getName()+"\\\" id=\\\"" + ed.getName()+ "\\\" size = \\\"" + ed.getSize() +"\\\" maxlength = \\\"" + ed.getContentSize() + "\\\" "+ readOnly + tabIndex + " value = \"" +", $_POST['"+ ed.getAttribute().getName()+"'], '>';") ;
    		else //Login form
    			out.println(nested() + "echo \"<TD><INPUT type=\\\"text\\\" name=\\\"login\\\" id=\\\"" + ed.getName()+ "\\\" size = \\\"" + ed.getSize() +"\\\" maxlength = \\\"" + ed.getContentSize() + "\\\" "+ readOnly + tabIndex + ">\";");
    	}
    	else
    	{
      		out.println(nested() + "echo \"<TD><INPUT type=\\\"password\\\" name=\\\"password\\\" id=\\\"" + ed.getName()+"\\\" size = \\\"" + ed.getSize() +"\\\" maxlength = \\\"" + ed.getContentSize()+ "\\\" "+ tabIndex + ">\";") ;
      	}
   }
   
    protected void generateHTMLComboBox(PrintWriter out, Form form, ComboBox cb, int index)
    {
    	out.println(nested() + "<TD><LABEL for=\"" + cb.getName() + "\">"+ cb.getCaption()+"</LABEL>");
    	String tabIndex="";
    	if (o.getProperty("app.tab.order").equals("true")) tabIndex=" tabindex = " + index;
    	String readOnly="";
    	if (cb.isReadOnly()) readOnly= " readonly=\"true\"";
    	out.println(nested() + "<TD><SELECT name=\""+cb.getName()+"\" id=\"" + cb.getName()+ readOnly + tabIndex + ">") ;
    	//Starting the php script to create the list of options
    	//We insert each element of the associated class as an option
    	out.println(nested() + "<? foreach($" + cb.getRefClass()+"s" + "as $"+ cb.getRefClass() +"{ ?>");
    	//If the value of the variable is the same as the current selected value we preselect the option
      	out.println(nested() + "<option value=\"<?=$" + cb.getRefClass()+ "['" + cb.getRefClass().getPrimaryKey().getName() + "']?>\" <?= $" + cb.getRefClass()+ " == $_POST['"+ cb.getRefClass() +"'] ? 'selected' : ''?>>");
      	out.println(nested()+ "<?=htmlspecialchars($" + cb.getRefClass()+"?> </option>)");
      	out.println(nested() + "</SELECT>");
   }
    
    protected void generateHTMLCheckBox(PrintWriter out, Form form, CheckBox ck, int index)
    {
    	out.println(nested() + "<TD><LABEL for=\"" + ck.getName() + "\">"+ ck.getCaption()+"</LABEL>");
    	String tabIndex="";
    	if (o.getProperty("app.tab.order").equals("true")) tabIndex=" tabindex = " + index;
    	String readOnly="";
    	if (ck.isReadOnly()) readOnly= " readonly=\"true\"";
    	String checked="<? if $"+ ck.getAttribute().getName()+"==true echo 'checked'?>"; 
    	out.println(nested() + "<TD><INPUT type=\"checkbox\" name=\""+ck.getName()+"\" id=\"" + ck.getName()+ readOnly + tabIndex + checked + ">") ;
     }
    	
    protected void generateHTMLGrid(PrintWriter out, Form form, Grid g)
    {
    	out.println(nested() + "echo \"<TABLE title= \",'" + g.getCaption() +"',\"> \";");
    	//Column names
    	Iterator<GridColumn> gc = g.getColumns().iterator();
    	while (gc.hasNext()) {
			GridColumn gridColumn = (GridColumn) gc.next();
			out.println(nested() + "echo '<TD>'"+ ",'"+ gridColumn.getAtt().getName()+"','</TD>';" ); 
		}
       	out.println(nested() + "foreach($" + g.getName()+"s" + " as $"+ g.getName() +") {");
        incNested();
    	out.println(nested() + "echo '<TR>';"); //Adding the values for each column
    	gc = g.getColumns().iterator();
    	while (gc.hasNext()) {
			GridColumn gridColumn = (GridColumn) gc.next();
			out.println(nested() + "echo '<TD>'"+ ",htmlspecialchars($" + g.getName()+"['" + gridColumn.getAtt().getName()+"']),'</TD>';" ); 
		}
    	String params="";
    	if( (g.getB_Update()!=null) || (g.getB_Delete()!=null) || (g.getB_Details()!=null))
    	{	
    		Attribute attribute  = g.getBaseClass().getPrimaryKeyAttribute();
   			params= attribute.getName() + "={$" + g.getName()+ "['" + attribute.getName()+"']}";
   			params=params + "&" + o.getProperty("app.back.link.param.name") + "=" + form.getName() + filePHPExtension();
       	}
    	
    	if (g.getB_Update()!=null)
    	{
    	  out.println(nested() + "echo \"<TD> <a href=\",\"" + g.getB_Update().getDestination().getName()+filePHPExtension()+ "?" + params +"\",\">" + g.getB_Update().getCaption()+"</a> </TD>\";");
     	}
    	if (g.getB_Delete()!=null)
    	{
    	  out.println(nested() + "echo \"<TD> <a href=\",\"" + g.getB_Delete().getDestination().getName()+filePHPExtension()+ "?" + params +"\",\">" + g.getB_Delete().getCaption()+"</a></TD>\";");
    	}
       	if (g.getB_Details()!=null)
    	{
    	  out.println(nested() + "<a href=\"" + g.getB_Details().getDestination().getName()+filePHPExtension()+ "?" + params +"\">" + g.getB_Details().getCaption()+"</a>");
     	}
    	decNested();
    	out.println(nested() + "}");
    	out.println(nested() + "echo '</TABLE>';");
      }
    
    
    
	protected void genHTMLMenu(PrintWriter out, Menu m)
	{
		out.println(nested()+ "<ul>");
		Iterator<MenuItem> sub= m.getItems().iterator();
		incNested();
		while (sub.hasNext()) {
			MenuItem menu =  sub.next();
			if (menu.getItems().size()==0)
			{
				String name= menu.getDestination().getName();
				out.println(nested()+ "<li><a href=\"" + name + filePHPExtension()+ "\">" + menu.getCaption() + "</a></li>");
			}
			else
			{
				out.println(nested()+ "<li>"+ menu.getCaption()+"</li>");
				genHTMLMenu(out,menu);
			}
		}
		decNested();
		out.println(nested()+ "</ul>");
	}
	
	//Initial home page 
	protected void generateHomePage(Form form) throws FileNotFoundException
	{
		File fHome= new File(baseDirApp+File.separator+o.getProperty("app.homepage.file.name")+fileHTMLExtension());
	    PrintWriter out= new PrintWriter(fHome);
	    addHTMLHead(out, p.getName());
	    out.println(nested()+ "<h2> " + l.getString("app.caption.form.main.prefix") + " " + p.getName() +"</h2>");
	    out.println(nested()+ "<h3> <a href=" + form.getName()+filePHPExtension()+">" +  l.getString("app.caption.homepage.suffix") +"</a> </h3>");
	    addHTMLEnd(out);
	    out.flush();
	    out.close();
	}
	
	
	protected void generateHTMLPageHead(PrintWriter out, String title)
	{
		addHTMLHead(out, title);
		out.println(nested() + "<table summary=\"\">");
		incNested();
		out.println(nested() + "<tr>");
		out.println(nested() + "<td width=\"" + o.getProperty("app.menu.width") +"%\">");
		incNested();
		genHTMLMenu(out,d.getMainMenu());
		decNested();
		out.println(nested() + "</td>");
		out.println(nested() + "<td width=\"" + o.getProperty("app.content.width") +"%\">");	
    }
	
	protected void generateHTMLPageTail(PrintWriter out)
	{	
	  out.println(nested() + "</td>");	
	  out.println(nested() + "</tr>");
	  decNested();
	  out.println(nested() + "</table>");
	  addHTMLEnd(out);
	}

	protected void createConnectionFile(Form form) throws FileNotFoundException
	{
		File fConn= new File(baseDirApp+File.separator+o.getProperty("app.connection.file.name")+filePHPExtension());
	    PrintWriter out= new PrintWriter(fConn);
	    out.println("<?php");
	    out.println(nested()+"session_start();");
   	    addComment(out,l.getString("app.comment.code.persistent.connection"));
	    out.println();
	    out.println(nested()+"function connect($connString, $login, $password)");
	    out.println(nested()+"{");
	    incNested();
	    addComment(out,l.getString("app.comment.code.persistent.connection"));
	    out.println(nested()+ "$conn = new PDO($connString, $login, $password, array(PDO::ATTR_PERSISTENT => true));");
	    //Set the error handling in exception mode
	    out.println(nested() + "$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);");
	    out.println(nested() + "$_SESSION['connection']=$connString;");
	    out.println(nested() + "$_SESSION['login']=$login;");
	    out.println(nested() + "$_SESSION['password']=$password;");
	    decNested();
	    out.println(nested()+"}");
	    out.println();
	    out.println(nested()+"function isConnected()");
	    out.println(nested()+"{");
	    incNested();
	    out.println(nested()+"return (isset($_SESSION['connection']) && isset($_SESSION['login']) && isset($_SESSION['password']) );");
	    decNested();
	    out.println(nested()+"}");
	    out.println();
	    out.println(nested()+"function getConnection()");
	    out.println(nested()+"{");
	    incNested();
	    out.println(nested()+"$connection= new PDO($_SESSION['connection'], $_SESSION['login'], $_SESSION['password'], array(PDO::ATTR_PERSISTENT => true));");
	    out.println(nested()+"$connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);");
	    out.println(nested()+"return $connection;");
	    decNested();
	    out.println(nested()+"}");
	    out.println("?>");
	    out.flush();
	    out.close();
	}

	protected void exceptionHandling(PrintWriter out, String var, String message, Form form)
	{
	   String url="";
	   if (form!=null) url=form.getName()+filePHPExtension();
	   out.println(" showError("+ message+ "," + var + "->getMessage(),+" + url + ");"); 
	   out.println();
	}
	


	protected void createUtilsFile() throws FileNotFoundException
	{
	/*	File fConn= new File(baseDirApp+File.separator+o.getProperty("app.utils.file.name")+filePHPExtension());
	    PrintWriter out= new PrintWriter(fConn);
	    out.println("<?php");
	    addComment(out,l.getString("app.comment.code.utils"));
		incNested();
	    out.println(nested()+"function showError($text, $error, $form)");
	    out.println(nested()+"{");
	    incNested();
	    out.println(nested() + "echo \"<h2>Error</h2>\";");
	    out.println(nested()+ "echo $text;");
	    out.println(nested()+ "echo nl2br(htmlspecialchars($error));");
	    out.println(nested()+ "if !$formName==\"\"");
	    out.println(nested()+"{");
	    incNested();
	    out.println(nested() + "echo " +" <a href=$form>" +  l.getString("app.exception.handling.goback") + "</a>;");
	    decNested();
	    out.println(nested()+"}");
	    out.println("exit();");
	    decNested();
	    out.println(nested()+"}");
	    phpEnd(out);
	    out.flush();
	    out.close();
	    */
		
	}
	
	protected void phpHeading(PrintWriter out, Form f)
	{
	  out.println("<?php");
	  String text="";
	  if (f.getActionKind()==FormActionKind.FA_VISUALIZE) text= l.getString("app.comment.code.form.visualize");
	  addComment(out,text + f.getName());
	  incNested();
	  genInclude(out, o.getProperty("app.connection.file.name")+"2.php");
	  genInclude(out, o.getProperty("app.utils.file.name")+".php");
	  genInclude(out,o.getProperty("app.form.db.prefix") + f.getName()+ o.getProperty("app.form.db.suffix")+ ".php");

	  if(d.getLoginForm()!=null)
		  out.println(nested() + "if (!isConnected()) { " + "header(\"location:"+d.getLoginForm().getName()+filePHPExtension()+"\");}");
	  else 	out.println(nested() + "if (!isConnected()) { " + "header(\"location:"+d.getInitialForm().getName()+filePHPExtension()+"\");}");
	  out.println(nested()+"else{");
	  incNested();
	  out.println(nested()+ "try");
	  out.println(nested() + "{");
	  incNested();
	  out.println(nested() + "global $conn;");
	  out.println(nested() + "$conn=getConnection();");
	  decNested();
	  out.println(nested() + "}");
      out.println(nested() + "catch(PDOException $e)");
      out.println(nested() + "{");
      incNested();
      out.println(nested() + "$warnings[] = \""+ l.getString("error.connecting.database")+"\";");
      out.println(nested() + "$warnings[] = $e->getMessage();");
      decNested();
      out.println(nested()+ "}");
      decNested();
      out.println(nested()+ "}");
	}
      
	protected void addComment(PrintWriter out, String comment)
	{
	  out.println(nested()+ "/**" + comment + "*/");
		
	}
	protected void phpEnd(PrintWriter out)
	{
	  decNested();
	  out.println("?>");
	}
	
	protected void addHTMLHead(PrintWriter out, String title)
	{
	  out.println(nested() + "<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	  out.println(nested() + "<head>");
	  incNested();
	  out.println(nested() +"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\"/>");
	  out.println(nested()+ "<title>" + title + "</title>");
	  decNested();
	  out.println(nested()+ "</head>");
	  out.println(nested()+ "<body>");
	  incNested();
	}

	protected void addHTMLEnd(PrintWriter out)
	{
		decNested();
		out.println(nested()+ "</body>");
		out.println(nested()+ "</html>");
	}
	
	protected String filePHPExtension() {return ".php";}
	
	protected String fileHTMLExtension() {return ".html";}
	
	//Adds an include directive into the file
	protected void genInclude(PrintWriter out, String file)
	 {
	   	out.println(nested()+"include('"+file+"');");
	 }
	
}
