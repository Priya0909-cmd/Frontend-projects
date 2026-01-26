import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

interface Authentication{
    boolean verifyPin(int enteredPin);
}
class Transaction{
    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime time;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-YYYY, HH:MM:SS");
    public Transaction(String type, double amount, double balanceAfter){
        this.type=type;
        this.amount=amount;
        this.balanceAfter=balanceAfter;
        this.time=LocalDateTime.now();
    }
    @Override
    public String toString(){
        return time.format(FORMATTER) + " | " + type + " | Amount: RS. " + amount + " | Balance: RS. " + balanceAfter;
    }
}   

class BankAccount implements Authentication{
    private String Holdername;
    private long accountnumber;
    private double balance;
    private boolean locked = false;
    private int pin;
    private int failedAttempts = 0;

    private ArrayList<Transaction> transactionHistory = new ArrayList<>();

    BankAccount(String Holdername, long accountnumber, int pin){
        this.Holdername=Holdername;
        this.accountnumber=accountnumber;
        this.pin = pin;
        this.balance=0.0;
    }
    public boolean islocked(){
        return locked;
    }

    @Override
    public boolean verifyPin(int enteredPin){
       if(islocked()){
        System.out.println("Account is locked, contact bank support !!");
        return false;
       }
       if(this.pin == enteredPin){
        failedAttempts = 0;
        return true;
       }
       else{
        failedAttempts++;
        if(failedAttempts >= 3){
            System.out.println("Account locked due to multiple wrong PIN attempts !!");
            locked = true;
        }
        return false;
       }
    }
    void deposit(double amount){//edge case : no negative amount should be deposited
       if (amount > 0){
        balance+=amount;
        transactionHistory.add(new Transaction("Deposit", amount, balance));
        System.out.println("Amount Deposited sucessfully !! "); 
       }
       else{
        System.out.println("Invalid amount entered :(");
       }
       
    }
    void withdraw(double amount){
        if(amount > 0 && amount<=balance){
            balance-=amount;
            transactionHistory.add(new Transaction("Withdraw", amount, balance));
            System.out.println("Amount withdrawn sucessfully !! ");
        }
        else{
            System.out.println("Insufficient balance!!");
        }
    }
    void checkBalance(){
        System.out.println("Current Balance : RS. " + balance);
    }
    void changePIN(int newpin){
        this.pin = newpin;
    }
    void ShowtransactionHistory(){
        if(transactionHistory.isEmpty()){
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("Transaction History:");
        for (Transaction t : transactionHistory) {
            System.out.println(t);
        }
    }
}

public class mainApp {
    static Scanner sc = new Scanner(System.in);
    static HashMap<Long, BankAccount> acc = new HashMap<>();
    public static void main(String[] args) {
        
        int choice;
        do { 
            System.out.println("\n-------Menu-------");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Change PIN");
            System.out.println("6. Transaction History");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1 -> CreateAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> checkBalance();
                case 5 -> changePIN();
                case 6 -> ShowtransactionHistory();
                case 7 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 7);
    }
    static void CreateAccount(){
        System.out.print("Enter Account Number: ");
        Long accno = sc.nextLong();

        if(acc.containsKey(accno)){
            System.out.println("Account already exists!");
            return;
        }
        sc.nextLine();
        System.out.print("Enter Holder Name: ");
        String name = sc.nextLine();

        int pin;
        while (true) { 
            System.out.print("Set a 4 digit pin : ");
            pin = sc.nextInt();
        if(pin >= 1000 && pin <= 9999){
            break;
        }
        System.out.println("Invalid PIN format !!");
        }

        BankAccount newAccount = new BankAccount(name, accno, pin);
        acc.put(accno, newAccount);
        System.out.println("Account created successfully!");
    }
    static void deposit(){
        System.out.print("Enter account number : ");
        long accnum = sc.nextLong();

        BankAccount Account = acc.get(accnum);

        if(Account != null){
            System.out.print("Enter the amount to be deposited : RS.");
            Account.deposit(sc.nextDouble());
        }
        else{
            System.out.println("Account Not Found !");
        }
    }
    static void withdraw(){
        System.out.print("Enter account number : ");
        long accnum = sc.nextLong();

         BankAccount Account = acc.get(accnum);

        if(Account != null){
            if(!authenticate(Account)){
                System.out.println("Authentication Failed !");
                return ;
            } 

            System.out.print("Enter Amount to be withdrawn : RS ");
            Account.withdraw(sc.nextDouble());
        }
        else{
            System.out.println("Account Not Found !");
        }
        
    }
    static void checkBalance(){
        System.out.print("Enter account number : ");
        long accnum = sc.nextLong();

        BankAccount Account = acc.get(accnum);

        if(Account != null){
           if(!authenticate(Account)){
                System.out.println("Authentication Failed !");
                return ;
           }
            Account.checkBalance();
        }
        else{
            System.out.println("Account Not Found !");
        }
        
    }
    static boolean authenticate(Authentication acc){

        if(((BankAccount)acc).islocked()){
            System.out.println("Access denied Account is locked !!");
            return false;
        }
        for(int i = 0 ; i<3 ; i++){
            System.out.print("Enter PIN : ");
            int pin = sc.nextInt();

            if(pin < 1000 || pin > 9999){
                System.out.println("Invalid PIN format !!");
                i--;
            }
            else if(acc.verifyPin(pin)) return true;
            else{
                System.out.println("Wrong PIN !!");
            }
        }
        return false;
    }
    static void changePIN(){
        System.out.print("Enter account number : ");
        long accnum = sc.nextLong();

        BankAccount Account = acc.get(accnum);

        if(Account == null){
            System.out.println("Account not found!!");
            return;
        }
        if(!authenticate(Account)){
            System.out.println("Authentication Failed !");
            return;
        } 
        int newpin , confirmpin;
        while(true){
            System.out.print("Enter new PIN : ");
            newpin = sc.nextInt();

            if(newpin < 1000 || newpin > 9999){
                System.out.println("Invalid PIN format !!");
                continue;
            }
            System.out.print("Confirm new PIN : ");
            confirmpin = sc.nextInt();

            if(newpin != confirmpin){
                System.out.println("PINs do not match !!");
            }
            else{
                break;
            }
        }
        Account.changePIN(newpin);
    }
    static void ShowtransactionHistory(){
        System.out.print("Enter account number : ");
        long accnum = sc.nextLong();

        BankAccount Account = acc.get(accnum);

        if(Account != null){
           if(!authenticate(Account)){
                System.out.println("Authentication Failed !");
                return ;
           }
            Account.ShowtransactionHistory();
        }
        else{
            System.out.println("Account Not Found !");
        }
        
    }
    

}
