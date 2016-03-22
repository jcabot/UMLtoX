package mdeServices;

import java.io.File;
import java.io.IOException;

import mdeServices.dbGenerator.DBGenerationException;
import mdeServices.dbGenerator.DBGeneratorDB2;
import mdeServices.dbGenerator.DBGeneratorFirebird;
import mdeServices.dbGenerator.DBGeneratorInterbase;
import mdeServices.dbGenerator.DBGeneratorMySQL;
import mdeServices.dbGenerator.DBGeneratorOracle;
import mdeServices.dbGenerator.DBGeneratorPostgreSQL;
import mdeServices.dbGenerator.DBGeneratorSQLServer;
import mdeServices.dbGenerator.DBGeneratorVoltDB;
 
public class DBGeneratorFactory {

	public static DBGenerator createDBGenerator(String dbms) throws DBGenerationException	{
		if(dbms.equals("mysql")) return new DBGeneratorMySQL();
		else if (dbms.equals("oracle")) return new DBGeneratorOracle();
		else if (dbms.equals("firebird")) return new DBGeneratorFirebird();
		else if (dbms.equals("interbase")) return new DBGeneratorInterbase();
		else if (dbms.equals("postgresql")) return new DBGeneratorPostgreSQL();
		else if (dbms.equals("db2")) return new DBGeneratorDB2();
		else if (dbms.equals("sqlserver")) return new DBGeneratorSQLServer();
		else if (dbms.equals("voltdb")) return new DBGeneratorVoltDB();
		else throw new DBGenerationException("Database not supported");
		
	}
}
