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
	
	private int accounts_counter = 0;
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

	public void writeFile(){
		FileManagement.openFileWrite();
		FileManagement.saveToFile(table);
		FileManagement.closeFile();
	}
	
	public void saveFileAs(){
		FileManagement.saveToFileAs();
		FileManagement.saveToFile(table);	
		FileManagement.closeFile();
	}
	
	public void readFile(){
		FileManagement.openFileRead(table);
		table = FileManagement.readRecords();
		FileManagement.closeFile();
		
		accounts_counter = table.size();
		last_account_id = table.get(accounts_counter).getAccountID();
		
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
				accounts_counter++;
				last_account_id++;
				new CreateBankDialog(table, last_account_id, accounts_counter);
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