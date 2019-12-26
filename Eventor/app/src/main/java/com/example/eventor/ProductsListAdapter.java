package com.example.eventor;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import java.util.Map;

class ProductsListAdapter extends ArrayAdapter<String> {

    private Context context;
    private boolean isManager;
    private String userName;
    private ArrayList<String> products;
    private LayoutInflater inflater;
    private Event event;
    private Map productsMap;

    private CheckBox productCheckBox;





    public ProductsListAdapter(@NonNull Context context, boolean isManager, String userName, ArrayList<String> products/*, ArrayList<String> gets*/, Event currentEvent) {
        super(context, R.layout.item_curren_event, products);

        this.context = context;
        this.userName = userName;
        this.isManager = isManager;
        this.event = currentEvent;
        this.productsMap = event.getProductsMap();
        this.products = products;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View productRow = convertView;
        if (productRow == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            productRow = inflater.inflate(R.layout.item_curren_event, parent, false);
        }
        productCheckBox = (CheckBox) productRow.findViewById(R.id.item_product_checkbox);
        final TextView productTextView = (TextView) productRow.findViewById(R.id.item_product_text_view);
        productTextView.setText(products.get(position));
        final TextView userNameTextView = (TextView) productRow.findViewById(R.id.user_name_text_view_event);

        String name = (String) productsMap.get(products.get(position));
        if (name.equals("")){
            productCheckBox.setVisibility(View.VISIBLE);
            productCheckBox.setChecked(false);
            userNameTextView.setText(name);
            //toggleCrossLineText(productTextView);
        } else if (name.equals(userName)){
            productCheckBox.setVisibility(View.VISIBLE);
            productCheckBox.setChecked(true);
            userNameTextView.setText(name);
            toggleCrossLineText(productTextView);
        } else {
            productCheckBox.setVisibility(View.INVISIBLE);
            userNameTextView.setText(name);
            productCheckBox.setChecked(true);
            toggleCrossLineText(productTextView);
        }

        //Checkbox listener
        productCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userNameTextView.setText(userName);
                    toggleCrossLineText(productTextView);
                } else {
                    userNameTextView.setText("");
                    toggleCrossLineText(productTextView);
                }
            }
        });
        return productRow;
    }

    /**
     * This method add or remove cross line on given TextView
     * @param tv TextView for add or remove cross line
     */
    private void toggleCrossLineText(TextView tv){
        if (!tv.getPaint().isStrikeThruText()){
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
