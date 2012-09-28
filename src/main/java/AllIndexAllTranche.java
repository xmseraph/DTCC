import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVReader;


public class AllIndexAllTranche {
	private static final int COLUMNNUM = 6;
	CSVReader reader;
	String tableName;
	DataSource data_source;
	
	public AllIndexAllTranche(String csvFileName,String tableName) throws FileNotFoundException {
		super();
		reader = new CSVReader(new FileReader(csvFileName));
		ApplicationContext ctx = new ClassPathXmlApplicationContext("db-bean.xml");
		this.tableName = tableName;
		data_source = (DataSource) ctx.getBean("dataSource");
	}

	public void startPersistIntoDatabase() throws IOException, SQLException{
		String [] nextLine;
		String insertStatement = "insert into "+tableName+" VALUES (?, ?, ?, ?, ?)";
		Connection dbConn = null;
        dbConn = data_source.getConnection();
        PreparedStatement ps = dbConn.prepareStatement(insertStatement);
        int counter = 0;
	    while ((nextLine = reader.readNext()) != null) {
	    	if(nextLine.length==COLUMNNUM){
	    		counter++;
	    		//prepare statement.
	    		ps.setString(1, nextLine[0]);
	    		ps.setBoolean(2, "Tranched".equalsIgnoreCase(nextLine[1]));
	    		ps.setLong(3, getBigInt(nextLine[2]));
	    		ps.setLong(4, getBigInt(nextLine[3]));
	    		ps.setInt(5, getInt(nextLine[4]));

	    		ps.addBatch();
	    		
	    	}else{
	    		for(int i=0;i<nextLine.length;i++){
	    			System.out.print(nextLine[i]);
	    			System.out.print('\t');
	    		}	    		
	    	}
	        System.out.print('\n');
	    }
	    System.out.println();
	    
	    if(isAllSuccessful(ps.executeBatch())){
	    	System.out.println("persist done, complete "+counter+" row");
	    }else{
	    	System.out.println("not all success.");
	    };
	    
	    
	    ps.close();
	    dbConn.close();
	}
	private static boolean isAllSuccessful(int[] returnArray) {
		for (int i = 0; i < returnArray.length; i++) {
			if (returnArray[i] == 0) {
				return false;
			}
		}
		System.out.println("executed "+returnArray.length+" rows");
		return true;
	}
	public static long getBigInt(String bigInt){
		return Long.valueOf(bigInt.replace(",", ""));
	}
	public static int getInt(String bigInt){
		return Integer.valueOf(bigInt.replace(",", ""));
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {
		System.out.print(getBigInt("1,234"));
		AllIndexAllTranche ooit = new AllIndexAllTranche("DataTable7.csv", "all_index_all_tranche");
		ooit.startPersistIntoDatabase();
	}

}
