package maintenance.bin.src.ResultTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.AbstractTableModel;

import maintenance.bin.src.DBConn.DBConnect;

public class ResultSetTableModel extends AbstractTableModel  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection;
	public Statement statement;
	public ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;

	// keep track of database connection status
	private boolean connectedToDatabase = false;

	// constructor initializes resultSet and obtains its meta data object;
	// determines number of rows
	public ResultSetTableModel( String query ) throws SQLException, ClassNotFoundException {         

		// create Statement to query database
		statement = DBConnect.getConnection().createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY );

		// update database connection status
		connectedToDatabase = true;

		// set query and execute it
		setQuery( query );
	} // end constructor ResultSetTableModel



	// get number of columns in ResultSet
	public int getColumnCount() throws IllegalStateException {   
		// ensure database connection is available
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		// determine number of columns
		try {
			return metaData.getColumnCount(); 
		} // end try
		catch ( SQLException sqlException ) {
			System.out.println(sqlException.getMessage() + " line 94");
		} // end catch
		catch(NullPointerException e) {

		}
		return 0; // if problems occur above, return 0 for number of columns
	} // end method getColumnCount

	// get name of a particular column in ResultSet
	public String getColumnName( int column ) throws IllegalStateException {    
		// ensure database connection is available
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		// determine column name
		try {
			return metaData.getColumnName( column + 1 );  
		} // end try
		catch ( SQLException sqlException ) {
			System.out.println(sqlException.getMessage() + " line 114");
		} // end catch

		return ""; // if problems, return empty string for column name
	} // end method getColumnName

	// return number of rows in ResultSet
	public int getRowCount() throws IllegalStateException {      
		// ensure database connection is available
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		return numberOfRows;
	} // end method getRowCount

	// obtain value in particular row and column
	public Object getValueAt( int row, int column ) throws IllegalStateException {
		// ensure database connection is available
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		// obtain a value at specified ResultSet row and column
		try {
			resultSet.absolute( row + 1 );
			return resultSet.getObject( column + 1 );
		} // end try
		catch ( SQLException sqlException ) 
		{
			sqlException.printStackTrace();
			//JOptionPane.showMessageDialog(null,"Record locked....","Information",JOptionPane.INFORMATION_MESSAGE);
		} // end catch

		return ""; // if problems, return empty string object
	} // end method getValueAt

	public void again(String q)throws SQLException, IllegalStateException {
		try {
			statement.setQueryTimeout(1000);
			statement.executeQuery(q);
		} catch (SQLException e) {
		}
	}

	// set new database query string
	public void setQuery( String query ) throws SQLException, IllegalStateException	{
		// ensure database connection is available
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		try {
			// specify query and execute it 
			resultSet = statement.executeQuery( query );
			//resultSet = statement.execute( query );


			// obtain meta data for ResultSet
			metaData = resultSet.getMetaData();


			// determine number of rows in ResultSet
			resultSet.last();                   // move to last row
			numberOfRows = resultSet.getRow();  // get row number      

			// notify JTable that model has changed
			//fireTableDataChanged();
			fireTableDataChanged();

		}catch(SQLException sqlE){

		}
	} // end method setQuery



	public void setInsert( String query1 ) throws SQLException, IllegalStateException {
		// ensure database connection is available
		if ( !connectedToDatabase ) 
			throw new IllegalStateException( "Not Connected to Database" );

		statement.execute(query1);

		// notify JTable that model has changed
		fireTableStructureChanged();
	} // end method setQuery

	// close Statement and Connection               
	public void disconnectFromDatabase() {              
		if ( !connectedToDatabase )                  
			return;

		// close Statement and Connection            
		try                                          
		{                                            
			statement.close();                        
			connection.close();                       
		} // end try                                 
		catch ( SQLException sqlException )          
		{                                            
			System.out.println(sqlException.getMessage() + " line 210");          
		} // end catch                               
		finally  // update database connection status
		{                                            
			connectedToDatabase = false;              
		} // end finally                             
	} // end method disconnectFromDatabase     

}
