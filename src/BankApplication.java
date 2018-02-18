import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class BankApplication extends JFrame {
	
	private final static int TABLE_SIZE = 29;
	private int accouts_counter = 0;
	private int last_account_id = 0;
	private JMenuBar menuBar;
	private JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;
	private JMenuItem nextItem, prevItem, firstItem, lastItem, findByAccount, findBySurname, listAll, createItem, modifyItem, deleteItem, setOverdraft, setInterest;
	private JMenuItem deposit, withdraw, calcInterest, open, save, saveAs, closeApp;
	private JButton firstItemButton, lastItemButton, nextItemButton, prevItemButton;
	private JLabel accountIDLabel, accountNumberLabel, firstNameLabel, surnameLabel, accountTypeLabel, balanceLabel, overdraftLabel;
	private JTextField accountIDTextField, accountNumberTextField, firstNameTextField, surnameTextField, accountTypeTextField, balanceTextField, overdraftTextField;
	private JTable jTable;
	private double interestRate;
	private int currentItem = 1;
	private boolean openValues;
	private ActionListener setOverdraftListener, firstItemButtonActionListener, nextItemButtonActionListener, previousItemButtonActionListener, lastItemButtonActionListener, deleteItemListener, createItemListener, modifyItemListener, 
		setInterestListener, listAllListener, openFileListener, saveFileListener, saveAsFileListener, closeAppListener, findBySurnameListener, findByAccountListener, depositAmountListener, withdrawAmountListener, calcInterestListener;
	private static RandomAccessFile input;
	private static RandomAccessFile output;

	static JFileChooser fc;
	static String fileToSaveAs = "";
	static HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	
	public BankApplication() {
		super("Bank Application");
		initComponents();
    	createListeners();
    	setListeners();
	}
	
	public static void main(String[] args) {
		BankApplication ba = new BankApplication();
		ba.setSize(1200,400);
		ba.pack();
		ba.setVisible(true);
	}
	
	public void initComponents() {
		setLayout(new BorderLayout());
		JPanel displayPanel = new JPanel(new MigLayout());
		
		accountIDLabel = new JLabel("Account ID: ");
		accountIDTextField = new JTextField(15);
		accountIDTextField.setEditable(false);
		
		displayPanel.add(accountIDLabel, "growx, pushx");
		displayPanel.add(accountIDTextField, "growx, pushx, wrap");
		
		accountNumberLabel = new JLabel("Account Number: ");
		accountNumberTextField = new JTextField(15);
		accountNumberTextField.setEditable(false);
		
		displayPanel.add(accountNumberLabel, "growx, pushx");
		displayPanel.add(accountNumberTextField, "growx, pushx, wrap");

		surnameLabel = new JLabel("Last Name: ");
		surnameTextField = new JTextField(15);
		surnameTextField.setEditable(false);
		
		displayPanel.add(surnameLabel, "growx, pushx");
		displayPanel.add(surnameTextField, "growx, pushx, wrap");

		firstNameLabel = new JLabel("First Name: ");
		firstNameTextField = new JTextField(15);
		firstNameTextField.setEditable(false);
		
		displayPanel.add(firstNameLabel, "growx, pushx");
		displayPanel.add(firstNameTextField, "growx, pushx, wrap");

		accountTypeLabel = new JLabel("Account Type: ");
		accountTypeTextField = new JTextField(5);
		accountTypeTextField.setEditable(false);
		
		displayPanel.add(accountTypeLabel, "growx, pushx");
		displayPanel.add(accountTypeTextField, "growx, pushx, wrap");

		balanceLabel = new JLabel("Balance: ");
		balanceTextField = new JTextField(10);
		balanceTextField.setEditable(false);
		
		displayPanel.add(balanceLabel, "growx, pushx");
		displayPanel.add(balanceTextField, "growx, pushx, wrap");
		
		overdraftLabel = new JLabel("Overdraft: ");
		overdraftTextField = new JTextField(10);
		overdraftTextField.setEditable(false);
		
		displayPanel.add(overdraftLabel, "growx, pushx");
		displayPanel.add(overdraftTextField, "growx, pushx, wrap");
		
		add(displayPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

		nextItemButton = new JButton(new ImageIcon("next.png"));
		prevItemButton = new JButton(new ImageIcon("previous.png"));
		firstItemButton = new JButton(new ImageIcon("first.png"));
		lastItemButton = new JButton(new ImageIcon("last.png"));
		
		buttonPanel.add(firstItemButton);
		buttonPanel.add(prevItemButton);
		buttonPanel.add(nextItemButton);
		buttonPanel.add(lastItemButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		menuBar = new JMenuBar();
    	setJMenuBar(menuBar);
		
		navigateMenu = new JMenu("Navigate");
    	
    	nextItem = new JMenuItem("Next Item");
    	prevItem = new JMenuItem("Previous Item");
    	firstItem = new JMenuItem("First Item");
    	lastItem = new JMenuItem("Last Item");
    	findByAccount = new JMenuItem("Find by Account Number");
    	findBySurname = new JMenuItem("Find by Surname");
    	listAll = new JMenuItem("List All Records");
    	
    	navigateMenu.add(nextItem);
    	navigateMenu.add(prevItem);
    	navigateMenu.add(firstItem);
    	navigateMenu.add(lastItem);
    	navigateMenu.add(findByAccount);
    	navigateMenu.add(findBySurname);
    	navigateMenu.add(listAll);
    	
    	menuBar.add(navigateMenu);
    	recordsMenu = new JMenu("Records");
    	
    	createItem = new JMenuItem("Create Item");
    	modifyItem = new JMenuItem("Modify Item");
    	deleteItem = new JMenuItem("Delete Item");
    	setOverdraft = new JMenuItem("Set Overdraft");
    	setInterest = new JMenuItem("Set Interest");
    	
    	recordsMenu.add(createItem);
    	recordsMenu.add(modifyItem);
    	recordsMenu.add(deleteItem);
    	recordsMenu.add(setOverdraft);
    	recordsMenu.add(setInterest);
    	
    	menuBar.add(recordsMenu);
    	transactionsMenu = new JMenu("Transactions");
    	
    	deposit = new JMenuItem("Deposit");
    	withdraw = new JMenuItem("Withdraw");
    	calcInterest = new JMenuItem("Calculate Interest");
    	
    	transactionsMenu.add(deposit);
    	transactionsMenu.add(withdraw);
    	transactionsMenu.add(calcInterest);
    	
    	menuBar.add(transactionsMenu);
    	fileMenu = new JMenu("File");
    	
    	open = new JMenuItem("Open File");
    	save = new JMenuItem("Save File");
    	saveAs = new JMenuItem("Save As");
    	
    	fileMenu.add(open);
    	fileMenu.add(save);
    	fileMenu.add(saveAs);
    	
    	menuBar.add(fileMenu);
    	exitMenu = new JMenu("Exit");
    	closeApp = new JMenuItem("Close Application");
    	exitMenu.add(closeApp);
    	menuBar.add(exitMenu);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void saveOpenValues(){		
		if (openValues){
			surnameTextField.setEditable(false);
			firstNameTextField.setEditable(false);
				
			table.get(currentItem).setSurname(surnameTextField.getText());
			table.get(currentItem).setFirstName(firstNameTextField.getText());
		}
	}	
	
	public void displayDetails(int key) {	
		accountIDTextField.setText(table.get(key).getAccountID()+"");
		accountNumberTextField.setText(table.get(key).getAccountNumber());
		surnameTextField.setText(table.get(key).getSurname());
		firstNameTextField.setText(table.get(key).getFirstName());
		accountTypeTextField.setText(table.get(key).getAccountType());
		balanceTextField.setText(table.get(key).getBalance()+"");
		
		if(accountTypeTextField.getText().trim().equals("Current"))
			overdraftTextField.setText(table.get(key).getOverdraft()+"");
		else
			overdraftTextField.setText("Only applies to current accs");
	}
	
	public static void openFileRead(){
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
	
	public void readRecords(){
		RandomAccessBankAccount record = new RandomAccessBankAccount();
		try {
			while ( true ){
		        do{
		        	if(input!=null)
		        		record.read( input );
		        } while ( record.getAccountID() == 0 );
	
	        BankAccount ba = new BankAccount(record.getAccountID(), record.getAccountNumber(), record.getFirstName(),
	                record.getSurname(), record.getAccountType(), record.getBalance(), record.getOverdraft());
	        
	        accouts_counter++;
	        last_account_id = ba.getAccountID();
	        table.put(accouts_counter, ba);
	     }
	  }
	  catch ( EOFException eofException ){
		  return;
	  }
	  catch ( IOException ioException ){
		  JOptionPane.showMessageDialog(null, "Error reading file.");
		  System.exit( 1 );
	  }
   }
	
	public void saveToFile(){
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

	public void writeFile(){
		openFileWrite();
		saveToFile();
		closeFile();
	}
	
	public void saveFileAs(){
		saveToFileAs();
		saveToFile();	
		closeFile();
	}
	
	public void readFile(){
	    openFileRead();
	    readRecords();
	    closeFile();		
	}
	
	private void searchStrategy(String searchType){
		boolean found = false;
		if(searchType.equals("SURNAME")){
			String sName = JOptionPane.showInputDialog("Search for surname: ");
			for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
				if(sName.equalsIgnoreCase((entry.getValue().getSurname().trim()))){
					found = true;
					displayDetails(entry.getKey());
				}
			}		
			if(found)
				JOptionPane.showMessageDialog(null, "Surname  " + sName + " found.");
			else
				JOptionPane.showMessageDialog(null, "Surname " + sName + " not found.");
		}
		else if(searchType.equals("ACCOUNT_NUMBER")){
			String accNum = JOptionPane.showInputDialog("Search for account number: ");
			for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
				if(accNum.equals(entry.getValue().getAccountNumber().trim())){
					found = true;
					displayDetails(entry.getKey());			
				}			 
			}
			if(found)
				JOptionPane.showMessageDialog(null, "Account number " + accNum + " found.");
			else
				JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
		}
	}
	
	private void setListeners(){
		setOverdraft.addActionListener(setOverdraftListener);
		nextItemButton.addActionListener(nextItemButtonActionListener);
		nextItem.addActionListener(nextItemButtonActionListener);
		prevItemButton.addActionListener(previousItemButtonActionListener);
		prevItem.addActionListener(previousItemButtonActionListener);
		firstItemButton.addActionListener(firstItemButtonActionListener);
		firstItem.addActionListener(firstItemButtonActionListener);
		lastItemButton.addActionListener(lastItemButtonActionListener);
		lastItem.addActionListener(lastItemButtonActionListener);
		deleteItem.addActionListener(deleteItemListener);
		createItem.addActionListener(createItemListener);
		modifyItem.addActionListener(modifyItemListener);
		setInterest.addActionListener(setInterestListener);
		listAll.addActionListener(listAllListener);
		open.addActionListener(openFileListener);
		save.addActionListener(saveFileListener);
		saveAs.addActionListener(saveAsFileListener);
		closeApp.addActionListener(closeAppListener);	
		findBySurname.addActionListener(findBySurnameListener);
		findByAccount.addActionListener(findByAccountListener);
		deposit.addActionListener(depositAmountListener);
		withdraw.addActionListener(withdrawAmountListener);
		calcInterest.addActionListener(calcInterestListener);		
	}
	
	private void createListeners(){
		firstItemButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				if(!table.isEmpty()){
					currentItem = 1;
					displayDetails(currentItem);
				}
			}
		};
		
		nextItemButtonActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveOpenValues();
				if(!table.isEmpty()){
					if(currentItem < table.size()){
						currentItem++;
						displayDetails(currentItem);
					}
				}
			}
		};

		previousItemButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				if(!table.isEmpty()){
					if(currentItem > 1){
						currentItem--;
						displayDetails(currentItem);
					}
				}		
			}
		};
	
		lastItemButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				if(!table.isEmpty()){
					currentItem = table.size();
					System.out.println(table.get(currentItem).getAccountID()+"");
					displayDetails(currentItem);
				}
			}
		};
		
		setOverdraftListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(table.get(currentItem).getAccountType().trim().equals("Current")){
					String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
					overdraftTextField.setText(newOverdraftStr);
					table.get(currentItem).setOverdraft(Double.parseDouble(newOverdraftStr));
				}
				else
					JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");
			}
		};
		
		deleteItemListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				HashMap<Integer, BankAccount> newMap = new HashMap<Integer, BankAccount>();
				for(int i = 1; i <= table.size(); i++){
					if(i > currentItem){
						newMap.put(i-1, table.get(i));
					}
					else if(i < currentItem){
						newMap.put(i, table.get(i));
					}
				}
				table = newMap;
				JOptionPane.showMessageDialog(null, "Account Deleted");
				if(!table.isEmpty()){
					currentItem = 1;
					displayDetails(currentItem);
				}
			}
		};
		
		createItemListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				accouts_counter++;
				last_account_id++;
				new CreateBankDialog(table, last_account_id, accouts_counter);
			}
		};
		
		modifyItemListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				surnameTextField.setEditable(true);
				firstNameTextField.setEditable(true);
				openValues = true;
			}
		};
		
		setInterestListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String interestRateStr = JOptionPane.showInputDialog("Enter Interest Rate: (do not type the % sign)");
				if(interestRateStr!=null)
					interestRate = Double.parseDouble(interestRateStr);
			}
		};
		
		listAllListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFrame frame = new JFrame("TableDemo");
				
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				String col[] = {"ID","Number","Name", "Account Type", "Balance", "Overdraft"};
				
				DefaultTableModel tableModel = new DefaultTableModel(col, 0);
				jTable = new JTable(tableModel);
				JScrollPane scrollPane = new JScrollPane(jTable);
				jTable.setAutoCreateRowSorter(true);
				
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
				    Object[] objs = {entry.getValue().getAccountID(), entry.getValue().getAccountNumber(), 
				    				entry.getValue().getFirstName().trim() + " " + entry.getValue().getSurname().trim(), 
				    				entry.getValue().getAccountType(), entry.getValue().getBalance(), 
				    				entry.getValue().getOverdraft()};

				    tableModel.addRow(objs);
				}
				frame.setSize(600,500);
				frame.add(scrollPane);
		        frame.setVisible(true);			
			}
		};
		
		openFileListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				readFile();
				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);
			}
		};
		
		saveFileListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				writeFile();
			}
		};
		
		saveAsFileListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFileAs();
			}
		};
		
		closeAppListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int answer = JOptionPane.showConfirmDialog(BankApplication.this, "Do you want to save before quitting?");
				if (answer == JOptionPane.YES_OPTION) {
					saveFileAs();
					dispose();
				}
				else if(answer == JOptionPane.NO_OPTION)
					dispose();
				else if(answer==0)
					;
			}
		};	
		
		findBySurnameListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				searchStrategy("SURNAME");
			}
		};
		
		findByAccountListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				searchStrategy("ACCOUNT_NUMBER");
			}
		};
		
		depositAmountListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String accNum = JOptionPane.showInputDialog("Account number to deposit into: ");
				boolean found = false;
				
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					if(accNum.equals(entry.getValue().getAccountNumber().trim())){
						found = true;
						String toDeposit = JOptionPane.showInputDialog("Account found, Enter Amount to Deposit: ");
						
						if(!toDeposit.equals("")){
							if(Double.parseDouble(toDeposit) > 0){
								entry.getValue().deposit(toDeposit);
								displayDetails(entry.getKey());
							}
							else{
								JOptionPane.showMessageDialog(null, "Please enter a number above 0.");
							}
						}
						else{
							JOptionPane.showMessageDialog(null, "Please enter a number above 0.");
						}
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
			}
		};
		
		withdrawAmountListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String accNum = JOptionPane.showInputDialog("Account number to withdraw from: ");
				if(!accNum.equals("")){
					Boolean found = false;
					for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
						if(accNum.equals(entry.getValue().getAccountNumber().trim())){
							found = true;
						}					
					}
					if(found == true){
						String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ");
						if(!toWithdraw.equals("")){
							if(Double.parseDouble(toWithdraw) > 0){
								for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
									if(accNum.equals(entry.getValue().getAccountNumber().trim())){
										entry.getValue().withdraw(toWithdraw);
										displayDetails(entry.getKey());
									}					
								}
							}
							else{
								JOptionPane.showMessageDialog(null, "Please enter a number above 0.");
							}
						}
						else{
							JOptionPane.showMessageDialog(null, "Please enter a number above 0.");
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Please enter a valid Account Number");
					}
				}
			}
		};
		
		calcInterestListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					entry.getValue().calculateInterest(interestRate);
					displayDetails(entry.getKey());
				}
			}
		};		
	}
}


//deleteItemListener, createItemListener, modifyItemListener, 
//setInterestListener, listAllListener, openFileListener, saveFileListener, saveAsFileListener, closeAppListener, findBySurnameListener, findByAccountListener, depositAmountListener, withdrawAmountListener, calcInterestListener;


/*
The task for your second assignment is to construct a system that will allow users to define a data structure representing a collection of records that can be displayed both by means of a dialog that can be scrolled through and by means of a table to give an overall view of the collection contents. 
The user should be able to carry out tasks such as adding records to the collection, modifying the contents of records, and deleting records from the collection, as well as being able to save and retrieve the contents of the collection to and from external random access files.
The records in the collection will represent bank account records with the following fields:

AccountID (this will be an integer unique to a particular account and 
will be automatically generated when a new account record is created)

AccountNumber (this will be a string of eight digits and should also 
be unique - you will need to check for this when creating a new record)

Surname (this will be a string of length 20)

FirstName (this will be a string of length 20)

AccountType (this will have two possible options - "Current " and 
"Deposit" - and again will be selected from a drop down list when 
entering a record)

Balance (this will a real number which will be initialised to 0.0 
and can be increased or decreased by means of transactions)

Overdraft (this will be a real number which will be initialised 
to 0.0 but can be updated by means of a dialog - it only applies 
to current accounts)

You may consider whether you might need more than one class to deal with bank accounts.
The system should be menu-driven, with the following menu options:

Navigate: First, Last, Next, Previous, Find By Account Number 
(allows you to find a record by account number entered via a 
dialog box), Find By Surname (allows you to find a record by 
surname entered via a dialog box),List All (displays the 
contents of the collection as a dialog containing a JTable)

Records: Create, Modify, Delete, Set Overdraft (this should 
use a dialog to allow you to set or update the overdraft for 
a current account), Set Interest Rate (this should allow you 
to set the interest rate for deposit accounts by means of a 
dialog)

Transactions: Deposit, Withdraw (these should use dialogs which
allow you to specify an account number and the amount to withdraw
or deposit, and should check that a withdrawal would not cause
the overdraft limit for a current account to be exceeded, or be 
greater than the balance in a deposit account, before the balance 
is updated), Calculate Interest (this calculates the interest rate 
for all deposit accounts and updates the balances)

File: Open, Save, Save As (these should use JFileChooser dialogs. 
The random access file should be able to hold 25 records. The position 
in which a record is stored and retrieved will be determined by its account 
number by means of a hashing procedure, with a standard method being used when 
dealing with possible hash collisions)

Exit Application (this should make sure that the collection is saved - or that 
the user is given the opportunity to save the collection - before the application closes)

When presenting the results in a JTable for the List All option, the records should be sortable on all fields, but not editable (changing the records or adding and deleting records can only be done through the main dialog).
For all menu options in your program, you should perform whatever validation, error-checking and exception-handling you consider to be necessary.
The programs Person.java and PersonApplication.java (from OOSD2) and TableDemo.java may be of use to you in constructing your interfaces. The set of Java programs used to create, edit and modify random access files will also provide you with a basis for your submission.

*/