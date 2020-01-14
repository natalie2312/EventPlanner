package com.example.eventor;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class ProductsListAdapter extends ArrayAdapter<Product> {

    private Context context;
    private boolean isManager;
    private boolean isEditable;
    private String userName;
    private ArrayList<Product> products;
    private LayoutInflater inflater;

    private CheckBox productCheckBox;
    private TextView productTextView;
    private TextView userNameTextView;





    public ProductsListAdapter(@NonNull Context context, boolean isManager, String userName, ArrayList<Product> products, boolean isEditable/*, Event currentEvent*/) {
        super(context, R.layout.item_current_event, products);

        this.context = context;
        this.userName = userName;
        this.isManager = isManager;
        this.isEditable = isEditable;

        this.products = products;


    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View productRow = convertView;
        if (productRow == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            productRow = inflater.inflate(R.layout.item_current_event, parent, false);
        }
        productCheckBox = (CheckBox) productRow.findViewById(R.id.item_product_checkbox);
        productCheckBox.setClickable(false);
        if (isEditable){
            productCheckBox.setVisibility(View.VISIBLE);
        }
        productTextView = (TextView) productRow.findViewById(R.id.item_product_text_view);
        productTextView.setText(products.get(position).getName());
        userNameTextView = (TextView) productRow.findViewById(R.id.user_name_text_view_event);

        Product p = products.get(position);
        if (isEditable) {
            userNameTextView.setVisibility(View.VISIBLE);
            userNameTextView.setText(p.getHowBring());
            if (p.getHowBring().equals("")) {
                productCheckBox.setVisibility(View.VISIBLE);
                productCheckBox.setChecked(false);
            } else if (p.getHowBring().equals(userName)) {
                productCheckBox.setVisibility(View.VISIBLE);
                productCheckBox.setChecked(true);
            } else {
                productCheckBox.setVisibility(View.INVISIBLE);
                productCheckBox.setChecked(true);
           }
            toggleCrossLineText();
        } else {
            productTextView.setText(p.getName());
        }
        return productRow;
    }

    /**
     * This method add or remove cross line on given TextView
     */
    private void toggleCrossLineText(){
        if (userNameTextView.getText().toString().equals("")){
            productTextView.setPaintFlags(productTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            productTextView.setPaintFlags(productTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }



    public void updateProducts(ArrayList<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

}
