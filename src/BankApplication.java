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

	private HashMap<Integer, BankAccount> accountsHashMap = new HashMap<Integer, BankAccount>();
	private int accounts_counter = 0;
	private int last_account_id = 0;
	private double interestRate = 0.0;
	private int currentAccountPosition = 1;
	private boolean openValues = false;
	
	private JTable jTable;
	private JMenuBar menuBar;
	private JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;
	private JMenuItem nextAccountMenuItem, prevAccountMenuItem, firstAccountMenuItem, lastAccountMenuItem, findByAccountNumberMenuItem, findBySurnameMenuItem, listAllAccountsMenuItem, createAccountMenuItem, modifyAccountMenuItem, 
		deleteAccountMenuItem, setOverdraftMenuItem, setInterestMenuItem, depositAmountMenuItem, withdrawAmountMenuItem, calcInterestMenuItem, openFileMenuItem, saveFileMenuItem, saveAsFileMenuItem, closeAppMenuItem;
	private JButton firstAccountButton, lastAccountButton, nextAccountButton, prevAccountButton;
	private JLabel accountIDLabel, accountNumberLabel, firstNameLabel, surnameLabel, accountTypeLabel, balanceLabel, overdraftLabel;
	private JTextField accountIDTextField, accountNumberTextField, firstNameTextField, surnameTextField, accountTypeTextField, balanceTextField, overdraftTextField;
	private ActionListener setOverdraftListener, firstItemButtonActionListener, nextItemButtonActionListener, previousItemButtonActionListener, lastItemButtonActionListener, deleteItemListener, createItemListener, modifyItemListener, 
		setInterestListener, listAllListener, openFileListener, saveFileListener, saveAsFileListener, closeAppListener, findBySurnameListener, findByAccountListener, depositAmountListener, withdrawAmountListener, calcInterestListener;
	
	
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
				
			accountsHashMap.get(currentAccountPosition).setSurname(surnameTextField.getText());
			accountsHashMap.get(currentAccountPosition).setFirstName(firstNameTextField.getText());
		}
	}	
	
	public void displayDetails(int key) {	
		accountIDTextField.setText(accountsHashMap.get(key).getAccountID()+"");
		accountNumberTextField.setText(accountsHashMap.get(key).getAccountNumber());
		surnameTextField.setText(accountsHashMap.get(key).getSurname());
		firstNameTextField.setText(accountsHashMap.get(key).getFirstName());
		accountTypeTextField.setText(accountsHashMap.get(key).getAccountType());
		balanceTextField.setText(accountsHashMap.get(key).getBalance()+"");
		
		if(accountTypeTextField.getText().trim().equals("Current"))
			overdraftTextField.setText(accountsHashMap.get(key).getOverdraft()+"");
		else
			overdraftTextField.setText("Only applies to current accs");
	}

	public void saveFile(){
		FileManagement.saveToFile(accountsHashMap);
		FileManagement.closeFile();
	}
	
	public void saveFileAs(){
		FileManagement.saveToFileAs();
		FileManagement.saveToFile(accountsHashMap);	
		FileManagement.closeFile();
	}
	
	public void readFile(){
		Boolean fileSelected = false;
		fileSelected = FileManagement.openFileRead(accountsHashMap);
		if(fileSelected == true){
			accountsHashMap = FileManagement.readRecords();
			FileManagement.closeFile();
			
			accounts_counter = accountsHashMap.size();
			last_account_id = accountsHashMap.get(accounts_counter).getAccountID();
			currentAccountPosition=1;
			displayDetails(currentAccountPosition);
		}
	}
	
	private void searchStrategy(String searchType){
		boolean found = false;
		if(searchType.equals("SURNAME")){
			String sName = JOptionPane.showInputDialog("Search for surname: ");
			for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
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
			for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
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

		nextAccountButton = new JButton(new ImageIcon("next.png"));
		prevAccountButton = new JButton(new ImageIcon("previous.png"));
		firstAccountButton = new JButton(new ImageIcon("first.png"));
		lastAccountButton = new JButton(new ImageIcon("last.png"));
		
		buttonPanel.add(firstAccountButton);
		buttonPanel.add(prevAccountButton);
		buttonPanel.add(nextAccountButton);
		buttonPanel.add(lastAccountButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		menuBar = new JMenuBar();
    	setJMenuBar(menuBar);
		
		navigateMenu = new JMenu("Navigate");
    	
    	nextAccountMenuItem = new JMenuItem("Next Item");
    	prevAccountMenuItem = new JMenuItem("Previous Item");
    	firstAccountMenuItem = new JMenuItem("First Item");
    	lastAccountMenuItem = new JMenuItem("Last Item");
    	findByAccountNumberMenuItem = new JMenuItem("Find by Account Number");
    	findBySurnameMenuItem = new JMenuItem("Find by Surname");
    	listAllAccountsMenuItem = new JMenuItem("List All Records");
    	
    	navigateMenu.add(nextAccountMenuItem);
    	navigateMenu.add(prevAccountMenuItem);
    	navigateMenu.add(firstAccountMenuItem);
    	navigateMenu.add(lastAccountMenuItem);
    	navigateMenu.add(findByAccountNumberMenuItem);
    	navigateMenu.add(findBySurnameMenuItem);
    	navigateMenu.add(listAllAccountsMenuItem);
    	
    	menuBar.add(navigateMenu);
    	recordsMenu = new JMenu("Records");
    	
    	createAccountMenuItem = new JMenuItem("Create Item");
    	modifyAccountMenuItem = new JMenuItem("Modify Item");
    	deleteAccountMenuItem = new JMenuItem("Delete Item");
    	setOverdraftMenuItem = new JMenuItem("Set Overdraft");
    	setInterestMenuItem = new JMenuItem("Set Interest");
    	
    	recordsMenu.add(createAccountMenuItem);
    	recordsMenu.add(modifyAccountMenuItem);
    	recordsMenu.add(deleteAccountMenuItem);
    	recordsMenu.add(setOverdraftMenuItem);
    	recordsMenu.add(setInterestMenuItem);
    	
    	menuBar.add(recordsMenu);
    	transactionsMenu = new JMenu("Transactions");
    	
    	depositAmountMenuItem = new JMenuItem("Deposit");
    	withdrawAmountMenuItem = new JMenuItem("Withdraw");
    	calcInterestMenuItem = new JMenuItem("Calculate Interest");
    	
    	transactionsMenu.add(depositAmountMenuItem);
    	transactionsMenu.add(withdrawAmountMenuItem);
    	transactionsMenu.add(calcInterestMenuItem);
    	
    	menuBar.add(transactionsMenu);
    	fileMenu = new JMenu("File");
    	
    	openFileMenuItem = new JMenuItem("Open File");
    	saveFileMenuItem = new JMenuItem("Save File");
    	saveAsFileMenuItem = new JMenuItem("Save As");
    	
    	fileMenu.add(openFileMenuItem);
    	fileMenu.add(saveFileMenuItem);
    	fileMenu.add(saveAsFileMenuItem);
    	
    	menuBar.add(fileMenu);
    	exitMenu = new JMenu("Exit");
    	closeAppMenuItem = new JMenuItem("Close Application");
    	exitMenu.add(closeAppMenuItem);
    	menuBar.add(exitMenu);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void setListeners(){
		setOverdraftMenuItem.addActionListener(setOverdraftListener);
		nextAccountButton.addActionListener(nextItemButtonActionListener);
		nextAccountMenuItem.addActionListener(nextItemButtonActionListener);
		prevAccountButton.addActionListener(previousItemButtonActionListener);
		prevAccountMenuItem.addActionListener(previousItemButtonActionListener);
		firstAccountButton.addActionListener(firstItemButtonActionListener);
		firstAccountMenuItem.addActionListener(firstItemButtonActionListener);
		lastAccountButton.addActionListener(lastItemButtonActionListener);
		lastAccountMenuItem.addActionListener(lastItemButtonActionListener);
		deleteAccountMenuItem.addActionListener(deleteItemListener);
		createAccountMenuItem.addActionListener(createItemListener);
		modifyAccountMenuItem.addActionListener(modifyItemListener);
		setInterestMenuItem.addActionListener(setInterestListener);
		listAllAccountsMenuItem.addActionListener(listAllListener);
		openFileMenuItem.addActionListener(openFileListener);
		saveFileMenuItem.addActionListener(saveFileListener);
		saveAsFileMenuItem.addActionListener(saveAsFileListener);
		closeAppMenuItem.addActionListener(closeAppListener);	
		findBySurnameMenuItem.addActionListener(findBySurnameListener);
		findByAccountNumberMenuItem.addActionListener(findByAccountListener);
		depositAmountMenuItem.addActionListener(depositAmountListener);
		withdrawAmountMenuItem.addActionListener(withdrawAmountListener);
		calcInterestMenuItem.addActionListener(calcInterestListener);		
	}
	
	private void createListeners(){
		firstItemButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				if(!accountsHashMap.isEmpty()){
					currentAccountPosition = 1;
					displayDetails(currentAccountPosition);
				}
			}
		};
		
		nextItemButtonActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveOpenValues();
				if(!accountsHashMap.isEmpty()){
					if(currentAccountPosition < accountsHashMap.size()){
						currentAccountPosition++;
						displayDetails(currentAccountPosition);
					}
				}
			}
		};

		previousItemButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				if(!accountsHashMap.isEmpty()){
					if(currentAccountPosition > 1){
						currentAccountPosition--;
						displayDetails(currentAccountPosition);
					}
				}		
			}
		};
	
		lastItemButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				if(!accountsHashMap.isEmpty()){
					currentAccountPosition = accountsHashMap.size();
					System.out.println(accountsHashMap.get(currentAccountPosition).getAccountID()+"");
					displayDetails(currentAccountPosition);
				}
			}
		};
		
		setOverdraftListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(accountsHashMap.get(currentAccountPosition).getAccountType().trim().equals("Current")){
					String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
					overdraftTextField.setText(newOverdraftStr);
					accountsHashMap.get(currentAccountPosition).setOverdraft(Double.parseDouble(newOverdraftStr));
				}
				else
					JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");
			}
		};
		
		deleteItemListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				HashMap<Integer, BankAccount> newMap = new HashMap<Integer, BankAccount>();
				for(int i = 1; i <= accountsHashMap.size(); i++){
					if(i > currentAccountPosition){
						newMap.put(i-1, accountsHashMap.get(i));
					}
					else if(i < currentAccountPosition){
						newMap.put(i, accountsHashMap.get(i));
					}
				}
				accountsHashMap = newMap;
				JOptionPane.showMessageDialog(null, "Account Deleted");
				if(!accountsHashMap.isEmpty()){
					currentAccountPosition = 1;
					displayDetails(currentAccountPosition);
				}
			}
		};
		
		createItemListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				accounts_counter++;
				last_account_id++;
				new CreateBankDialog(accountsHashMap, last_account_id, accounts_counter);
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
				
				for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
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
			}
		};
		
		saveFileListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFile();
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
				String accNum = JOptionPane.showInputDialog("Account number to depositAmountMenuItem into: ");
				boolean found = false;
				
				for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
					if(accNum.equals(entry.getValue().getAccountNumber().trim())){
						found = true;
						String toDeposit = JOptionPane.showInputDialog("Account found, Enter Amount to deposit: ");
						
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
				String accNum = JOptionPane.showInputDialog("Account number of Account to withdraw from: ");
				if(!accNum.equals("")){
					Boolean found = false;
					for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
						if(accNum.equals(entry.getValue().getAccountNumber().trim())){
							found = true;
						}					
					}
					if(found == true){
						String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to withdraw: ");
						if(!toWithdraw.equals("")){
							if(Double.parseDouble(toWithdraw) > 0){
								for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
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
				for (Map.Entry<Integer, BankAccount> entry : accountsHashMap.entrySet()) {
					entry.getValue().calculateInterest(interestRate);
					displayDetails(entry.getKey());
				}
			}
		};		
	}
}