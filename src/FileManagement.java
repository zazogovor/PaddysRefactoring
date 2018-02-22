import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileManagement {
	private static RandomAccessFile input;
	private static RandomAccessFile output;
	private static JFileChooser fc;
	private static String fileToSaveAs = "";
	private static int accounts_counter;
	
	public static HashMap<Integer, BankAccount> readRecords(){
		RandomAccessBankAccount record = new RandomAccessBankAccount();
		HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
		try {
			while( true )
			{
		        do{
		        	if(input!=null)
		        		record.read( input );
		        } while ( record.getAccountID() == 0 );
	
		        BankAccount ba = new BankAccount(record.getAccountID(), record.getAccountNumber(), record.getFirstName(),
		                record.getSurname(), record.getAccountType(), record.getBalance(), record.getOverdraft());
		        
		        accounts_counter++;
		        table.put(accounts_counter, ba);
			}
	  }
	  catch ( EOFException eofException ){
			return table;
	  }
	  catch ( IOException ioException ){
		  JOptionPane.showMessageDialog(null, "Error reading file.");
		  System.exit( 1 );
		  return null;
	  }
	}
	
	public static void saveToFile(HashMap<Integer, BankAccount> table){
		RandomAccessBankAccount record = new RandomAccessBankAccount();
	    
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
		   record.setAccountID(entry.getValue().getAccountID());
		   record.setAccountNumber(entry.getValue().getAccountNumber());
		   record.setFirstName(entry.getValue().getFirstName());
		   record.setSurname(entry.getValue().getSurname());
		   record.setAccountType(entry.getValue().getAccountType());
		   record.setBalance(entry.getValue().getBalance());
		   record.setOverdraft(entry.getValue().getOverdraft());
		   
		   if(output!=null){
		      try {
					record.write( output );
				} catch (IOException u) {
					u.printStackTrace();
				}
		   }   
		} 
	}
	
	public static void openFileRead(HashMap<Integer, BankAccount> table){
		table.clear();
		fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
        } 
        else {
        }
		try{
			if(fc.getSelectedFile()!=null)
				input = new RandomAccessFile( fc.getSelectedFile(), "r" );
		}
		catch ( IOException ioException ){
			JOptionPane.showMessageDialog(null, "File Does Not Exist.");
		}
	}
	
	public static void openFileWrite(){
		if(fileToSaveAs!=""){
	      try{
	         output = new RandomAccessFile( fileToSaveAs, "rw" );
	         JOptionPane.showMessageDialog(null, "Accounts saved to " + fileToSaveAs);
	      }
	      catch ( IOException ioException ){
	    	  JOptionPane.showMessageDialog(null, "File does not exist.");
	      }
		}
		else
		   saveToFileAs();
	}
	
	public static void saveToFileAs(){
		fc = new JFileChooser();
		
		 int returnVal = fc.showSaveDialog(null);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
             File file = fc.getSelectedFile();
           
             fileToSaveAs = file.getName();
             JOptionPane.showMessageDialog(null, "Accounts saved to " + file.getName());
         } 
         else
             JOptionPane.showMessageDialog(null, "Save cancelled by user");
	     try {
	    	 if(fc.getSelectedFile()==null){
	        	JOptionPane.showMessageDialog(null, "Cancelled");
	    	 }
	    	 else
	        	output = new RandomAccessFile(fc.getSelectedFile(), "rw" );
		} 
	    catch (FileNotFoundException e) {
			e.printStackTrace();
		}   
	}
	
	public static void closeFile(){
		try{
		    if ( input != null )
		      input.close();
		}
		catch ( IOException ioException ){
			JOptionPane.showMessageDialog(null, "Error closing file.");//System.exit( 1 );
		}
	}

}
