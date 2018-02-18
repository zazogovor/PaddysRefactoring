import javax.swing.JOptionPane;

public class BankAccount {
	
	private int accountID;
	private String accountNumber;
	private String surname;
	private String firstName;
	private String accountType;
	private double balance;
	private double overdraft;
	
	public BankAccount(int accountID, String accountNumber, String surname, String firstName, String accountType, double balance, double overdraft){
		this.accountID = accountID;
		this.accountNumber = accountNumber;
		this.surname = surname;
		this.firstName = firstName;
		this.accountType = accountType;
		this.balance = balance;
		this.overdraft = overdraft;
	}
	
	public BankAccount(){
		this(0, "", "", "", "", 0.0, 0.0);
	}
	
	public int getAccountID() {
		return accountID;
	}
	
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getAccountType() {
		return accountType;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public double getOverdraft() {
		return overdraft;
	}
	
	public void setOverdraft(double overdraft) {
		this.overdraft = overdraft;
	}
	
	public String toString(){
		return "\nAccount id: " + accountID +  "Account Num: " + accountNumber + "\nName: " + surname + " " + firstName+"\n";
	}
	
	public void deposit(String depositAmount){
		setBalance(getBalance() + Double.parseDouble(depositAmount));
	}
	
	public void withdraw(String withdrawAmount){
		if(getAccountType().trim().equals("Current")){
			if(Double.parseDouble(withdrawAmount) > getBalance() + getOverdraft())
				JOptionPane.showMessageDialog(null, "Transaction exceeds overdraft limit");
			else{
				setBalance(getBalance() - Double.parseDouble(withdrawAmount));
			}
		}
		else if(getAccountType().trim().equals("Deposit")){
			if(Double.parseDouble(withdrawAmount) <= getBalance()){
				setBalance(getBalance()-Double.parseDouble(withdrawAmount));
			}
			else
				JOptionPane.showMessageDialog(null, "Insufficient funds.");
		}
	}
	
	public void calculateInterest(Double interestRate){
		if(getAccountType().equals("Deposit")){
			double equation = 1 + ((interestRate)/100);
			setBalance(getBalance()*equation);
			JOptionPane.showMessageDialog(null, "Balances Updated");
		}
	}

}
