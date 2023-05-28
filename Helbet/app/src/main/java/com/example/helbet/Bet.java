package com.example.helbet;

public class Bet extends DBModel {
    int announcedResult;
    int amount;
    boolean checked;
    double oddValue;

    public Bet() {
    }

    public Bet(int announcedResult, int amount, double oddValue) {
        this.announcedResult = announcedResult;
        this.amount = amount;
        this.checked = false;
        this.oddValue = oddValue;
    }

    public int getAnnouncedResult() {
        return announcedResult;
    }

    public void setAnnouncedResult(int announcedResult) {
        this.announcedResult = announcedResult;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double getOddValue() {
        return oddValue;
    }

    public void setOddValue(double oddValue) {
        this.oddValue = oddValue;
    }

    @Override
    public String toString() {
        return "Bet{" +
                "announcedResult=" + announcedResult +
                ", amount=" + amount +
                "} " + super.toString();
    }
}
