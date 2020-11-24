package Bank;

public class Account {
    private int balance;
    private int portNumber;

    public int getBalance() {
        return balance;
    }

    public int withdraw(int withdrawlAmount){
        if(this.balance > withdrawlAmount){
            this.balance -= withdrawlAmount;
        }
        return withdrawlAmount;
    }

    public void deposit(int depositAmount){
        this.balance += depositAmount;
    }
}
