package com.dualbrotech.tourmate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dualbrotech.tourmate.Models.Event;
import com.dualbrotech.tourmate.Models.Expense;
import com.dualbrotech.tourmate.R;

import java.util.ArrayList;

/**
 * Created by Arif Rahman on 2/2/2018.
 */

public class ExpenseAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Expense> expenses;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @Override
    public int getCount() {
        return expenses.size();
    }

    @Override
    public Object getItem(int position) {
        return expenses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.single_expense_item,parent,false);

        TextView tv_expenseTitle = view.findViewById(R.id.tv_expenseTitle);
        TextView tv_expenseDate = view.findViewById(R.id.tv_expenseDate);
        TextView tv_expenseAmount = view.findViewById(R.id.tv_expenseAmount);
        TextView tv_expenseRank = view.findViewById(R.id.expenseRankTV);

        tv_expenseTitle.setText(expenses.get(position).getExpenseTitle());
        tv_expenseRank.setText(String.valueOf(position+1)+".");
        tv_expenseDate.setText(expenses.get(position).getExpenseDate());
        tv_expenseAmount.setText(expenses.get(position).getExpenseAmount());

        return view;
    }

}
