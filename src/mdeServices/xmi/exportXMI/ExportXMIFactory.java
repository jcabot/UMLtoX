package mdeServices.xmi.exportXMI;

public class ExportXMIFactory {

	
	public static ExportXMI createExportXMI(String exportTo) throws ExportXMIException
	{
		ExportXMI exp=null;
	    if (exportTo.equals("ArgoUML_v024")) exp=new ExportXMIArgoUML_v024();
        else if (exportTo.equals("ArgoUML_v028")) exp=new ExportXMIArgoUML_v028();
	    else if (exportTo.equals("Poseidon_v5")) exp=new ExportXMIPoseidon_v5();
        else if (exportTo.equals("EclipseUML2")) exp=new ExportXMIEclipseUML2();
        else if (exportTo.equals("MOSKitt")) exp=new ExportXMIMoskitt();
        else if (exportTo.equals("yUML")) exp=new ExportXMIYUML();
        else if (exportTo.equals("PlantUML")) exp=new ExportXMIPlantUML();
        else if (exportTo.equals("Modelio_v1")) exp=new ExportXMIModelio_v1();
	    else throw new ExportXMIException("Tool not supported " + exportTo);
      	return exp;
	}

}
