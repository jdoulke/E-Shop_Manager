package com.ihu.e_shopmanager.products;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
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



public class InsertProduct extends Fragment {

    EditText product_id, product_name, product_price, product_stock, product_category;
    Button button;

    public InsertProduct(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.products_insert_fragment, container, false);
        product_id = view.findViewById(R.id.product_insert_id);
        product_price = view.findViewById(R.id.product_insert_price);
        product_name = view.findViewById(R.id.product_insert_name);
        product_stock = view.findViewById(R.id.product_insert_stock);
        product_category = view.findViewById(R.id.product_insert_category);
        button = view.findViewById(R.id.product_insert_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = 0;
            try {
                id = Integer.parseInt(product_id.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            String name = product_name.getText().toString();
            String category = product_category.getText().toString();
            int stock = 0;
            try {
                stock = Integer.parseInt(product_stock.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            float price = 0;
            try {
                price = Float.parseFloat(product_price.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            try {
                Product product = new Product();
                product.setId(id);
                product.setName(name);
                product.setCategory(category);
                product.setStock(stock);
                product.setPrice(price);
                MainActivity.myAppDatabase.myDao().insertProduct(product);
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getActivity(),"To προϊόν προστέθηκε",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String message = e.getMessage();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
            product_id.setText("");
            product_category.setText("");
            product_stock.setText("");
            product_price.setText("");
            product_name.setText("");
        });
        return view;
    }

}
