package com.example.eventor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ContactsListAdapter extends ArrayAdapter {

    private CheckBox selectContactCheckBox;
    private TextView nameContactTextView;
    private TextView phoneNumberContactTextView;

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Contact> contacts;

    private ArrayList<Integer> selectedIndex;
    private boolean isCheckable;


    public ContactsListAdapter(@NonNull Context context, ArrayList<Contact> contacts, boolean isCheckable) {
        super(context, R.layout.item_select_contact, contacts);
        this.context = context;
        this.contacts = contacts;
        selectedIndex = new ArrayList<>();
        this.isCheckable = isCheckable;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View contactRow = convertView;
        ViewHolder holder = null;

        if (contactRow == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contactRow = inflater.inflate(R.layout.item_select_contact, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = contactRow.findViewById(R.id.name_contact_text_view);
            holder.phoneNumberTextView = contactRow.findViewById(R.id.phone_number_contact_text_view);
            holder.checkBox = contactRow.findViewById(R.id.select_contact_check_box);
            holder.checkBox.setClickable(false);
            if (isCheckable){
                holder.checkBox.setVisibility(View.VISIBLE);
            }
            contactRow.setTag(holder);
        } else {
            holder = (ViewHolder)contactRow.getTag();
        }

        //final Contact contact = contacts.get(position);
        holder.nameTextView.setText(contacts.get(position).getName());
        holder.phoneNumberTextView.setText(contacts.get(position).getPhoneNumber());
        if (contacts.get(position).isSelected()){
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(null);
        return contactRow;
    }

    class ViewHolder{
        CheckBox checkBox;
        TextView nameTextView;
        TextView phoneNumberTextView;
    }


    public void update(ArrayList<Contact> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }
}
