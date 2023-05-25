package com.example.helbet;

public class Bet extends DBModel {
    int announcedResult;
    int amount;

    public Bet() {
    }

    public Bet(int announcedResult, int amount) {
        this.announcedResult = announcedResult;
        this.amount = amount;
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

    @Override
    public String toString() {
        return "Bet{" +
                "announcedResult=" + announcedResult +
                ", amount=" + amount +
                "} " + super.toString();
    }
}
