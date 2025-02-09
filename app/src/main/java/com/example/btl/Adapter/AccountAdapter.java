package com.example.btl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.btl.R;
import com.example.btl.models.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {
    private LayoutInflater inflater;
    private List<Account> accounts;

    public AccountAdapter(Context context, int resource, List<Account> accounts) {
        super(context, resource, accounts);
        inflater = LayoutInflater.from(context);
        this.accounts = accounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.auto_fill, parent, false);
        }

        Account account = accounts.get(position);

        TextView txtEmail = convertView.findViewById(R.id.tvAccountName);
        txtEmail.setText(account.getEmail());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = accounts;
                    results.count = accounts.size();
                } else {
                    List<Account> filteredAccounts = new ArrayList<>();

                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Account account : accounts) {
                        if (account.getEmail().toLowerCase().startsWith(filterPattern)) {
                            filteredAccounts.add(account);
                        }
                    }

                    results.values = filteredAccounts;
                    results.count = filteredAccounts.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();

                if (results.count > 0) {
                    addAll((List<Account>) results.values);
                }

                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((Account) resultValue).getEmail();
            }
        };
    }
}
