package com.ihu.e_shopmanager.products;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;



public class InsertProduct extends Fragment {

    EditText product_id, product_name, product_price, product_stock, product_category;
    Button button;

    public InsertProduct(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.products_insert_fragment, container, false);
        else
            view = inflater.inflate(R.layout.products_insert_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Προϊόντα");

        product_id = view.findViewById(R.id.product_insert_id);
        product_price = view.findViewById(R.id.product_insert_price);
        product_name = view.findViewById(R.id.product_insert_name);
        product_stock = view.findViewById(R.id.product_insert_stock);
        product_category = view.findViewById(R.id.product_insert_category);
        button = view.findViewById(R.id.product_insert_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id;
            try {
                id = Integer.parseInt(product_id.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του ID. ",Toast.LENGTH_LONG).show();
                return;
            }
            String name = product_name.getText().toString();
            String category = product_category.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του ονόματος. ",Toast.LENGTH_LONG).show();
                return;
            }
            if(category.isEmpty()){
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή της κατηγορίας. ",Toast.LENGTH_LONG).show();
                return;
            }
            int stock;
            try {
                stock = Integer.parseInt(product_stock.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του αποθέματος. ",Toast.LENGTH_LONG).show();
                return;
            }
            float price;
            try {
                price = Float.parseFloat(product_price.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή της αξίας του προϊόντος. ",Toast.LENGTH_LONG).show();
                return;
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
                Toast.makeText(getActivity(), "To προϊόν προστέθηκε.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του προϊόντος. ",Toast.LENGTH_LONG).show();
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
