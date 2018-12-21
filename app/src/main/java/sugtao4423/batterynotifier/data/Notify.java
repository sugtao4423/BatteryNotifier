package sugtao4423.batterynotifier.data;

import sugtao4423.batterynotifier.Enum;

public class Notify{

    private Enum.AccountType accountType;
    private Object account;
    private boolean isEnable;
    private int threshold;
    private String text;

    public Notify(Enum.AccountType accountType, Object account, boolean isEnable, int threshold, String text){
        this.accountType = accountType;
        this.account = account;
        this.isEnable = isEnable;
        this.threshold = threshold;
        this.text = text;
    }

    public Enum.AccountType getAccountType(){
        return accountType;
    }

    public Object getAccount(){
        return account;
    }

    public boolean getIsEnable(){
        return isEnable;
    }

    public int getThreshold(){
        return threshold;
    }

    public String getText(){
        return text;
    }

}
