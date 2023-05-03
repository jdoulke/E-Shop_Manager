package com.ihu.e_shopmanager.products;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;

import java.util.List;


public class UpdateProduct extends Fragment {

    EditText product_id, product_name, product_category, product_stock, product_price;
    Button button;

    public UpdateProduct() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.products_update_fragment, container, false);
        product_id = view.findViewById(R.id.product_update_id);
        product_name = view.findViewById(R.id.product_update_name);
        product_category = view.findViewById(R.id.product_update_category);
        product_stock = view.findViewById(R.id.product_update_stock);
        product_price = view.findViewById(R.id.product_update_price);
        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();

        product_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing

            }

            @Override
            public void afterTextChanged(Editable s) {
                Product product = new Product();
                for (Product i : products) {
                    int code = i.getId();
                    if (s.toString().equals(String.valueOf(code))) {
                        product = i;
                        break;
                    }
                }

                // Populate the other EditTexts with the customer details
                product_name.setText(product.getName());
                product_category.setText(product.getCategory());
                if(product.getStock() != 0)
                    product_stock.setText(String.valueOf(product.getStock()));
                else
                    product_stock.setText("");
                if(product.getPrice() != 0)
                    product_price.setText(String.valueOf(product.getPrice()));
                else
                    product_price.setText("");
            }
        });
        button = view.findViewById(R.id.product_update_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = 0;
            try {
                id = Integer.parseInt(product_id.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            Product product = new Product();
            String name, category;
            name = product_name.getText().toString();
            category = product_category.getText().toString();
            float price = 0;
            try {
                price = Float.parseFloat(product_price.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            int stock = 0;
            try {
                stock = Integer.parseInt(product_stock.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            try {
                product.setPrice(price);
                product.setStock(stock);
                product.setId(id);
                product.setName(name);
                product.setCategory(category);
                MainActivity.myAppDatabase.myDao().updateProduct(product);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getActivity(),"Τα στοιχεία ενημερώθηκαν",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String message = e.getMessage();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
            product_id.setText("");
            product_name.setText("");
            product_stock.setText("");
            product_category.setText("");
            product_price.setText("");
        });
        return view;
    }

}
