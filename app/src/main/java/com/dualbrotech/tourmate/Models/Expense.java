package com.dualbrotech.tourmate.Models;

/**
 * Created by Arif Rahman on 2/2/2018.
 */

public class Expense {
    private String eventId;
    private String expenseId;
    private String expenseTitle;
    private String expenseDate;
    private String expenseAmount;


    public Expense() {

    }

    public Expense(String eventId, String expenseId, String expenseTitle, String expenseDate, String expenseAmount) {
        this.eventId = eventId;
        this.expenseId = expenseId;
        this.expenseTitle = expenseTitle;
        this.expenseDate = expenseDate;
        this.expenseAmount = expenseAmount;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
