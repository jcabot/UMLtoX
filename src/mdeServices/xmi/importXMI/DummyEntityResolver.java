package mdeServices.xmi.importXMI;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *  Dummy class to avoid a DTD file not found error when importing Visio files
 * 
 * @version 0.1 April 2009
 * @author jcabot
 *
 */
public class DummyEntityResolver implements EntityResolver
{
    @Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// TODO Auto-generated method stub
		return new InputSource(new StringReader(" "));
	}

}

