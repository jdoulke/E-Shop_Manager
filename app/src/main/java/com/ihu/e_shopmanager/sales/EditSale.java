package com.ihu.e_shopmanager.sales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.ProductWithQuantity;
import com.ihu.e_shopmanager.products.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditSale extends Fragment {


    EditText sale_id_text, client_id_text, sale_date_text, sale_order_text;
    Button sale_edit_button, sale_remove_button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.sale_edit_fragment, container, false);
        else
            view = inflater.inflate(R.layout.sale_edit_lanscape_fragment, container, false);


        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πωλήσεις");

        sale_id_text = view.findViewById(R.id.sale_edit_id);
        client_id_text = view.findViewById(R.id.sale_edit_client_id);
        sale_date_text = view.findViewById(R.id.sale_date_view);
        sale_order_text = view.findViewById(R.id.sale_order_date_view);

        sale_edit_button = view.findViewById(R.id.sale_edit_button);
        sale_remove_button = view.findViewById(R.id.sale_remove_button);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        CollectionReference salesReference = MainActivity.firestoreDatabase.collection("Sales");

        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();

        List<Sale> sales = new ArrayList<>();
        List<ProductWithQuantity> productsWithQuantity = new ArrayList<>();



        HashMap<Integer,Sale> salesMap = new HashMap<>();

        salesReference.get().addOnSuccessListener(querySnapshot -> {
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            for (DocumentSnapshot document : documents) {
                if (document.exists()) {
                    List<Map<String, Object>> productList = (List<Map<String, Object>>) document.get("productsList");
                    for (Map<String, Object> productMap : productList) {
                        Map<String, Object> productFromFirestore = (Map<String, Object>) productMap.get("product");
                        String category = (String) productFromFirestore.get("category");
                        int id = ((Long) productFromFirestore.get("id")).intValue();
                        String name = (String) productFromFirestore.get("name");
                        float price = ((Double) productFromFirestore.get("price")).floatValue();
                        int stock = ((Long) productFromFirestore.get("stock")).intValue();
                        int quantity = ((Long) productMap.get("quantity")).intValue();
                        Product product = new Product();
                        product.setName(name);
                        product.setId(id);
                        product.setStock(stock);
                        product.setCategory(category);
                        product.setPrice(price);
                        ProductWithQuantity productWithQuantity = new ProductWithQuantity();
                        productWithQuantity.setProduct(product);
                        productWithQuantity.setQuantity(quantity);
                        productsWithQuantity.add(productWithQuantity);
                    }
                    Sale sale = new Sale();
                    sale.setProductsList(productsWithQuantity);
                    String orderDate, saleDate;
                    int sale_id, client_id;
                    float value;
                    orderDate = document.getString("order_date");
                    saleDate = document.getString("sale_date");
                    sale_id = document.getLong("sale_id").intValue();
                    client_id = document.getLong("client_id").intValue();
                    value = document.getDouble("value").floatValue();
                    sale.setSale_id(sale_id);
                    sale.setClient_id(client_id);
                    sale.setValue(value);
                    sale.setOrder_date(orderDate);
                    sale.setSale_date(saleDate);
                    sales.add(sale);
                }
            }


            for(Sale sale : sales)
                salesMap.put(sale.getSale_id(), sale);


            Toast.makeText(getActivity(),"Φορτώθηκαν τα δεδομένα",Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Log.d("FireStore ERROR: ", e.getMessage());
        });

        sale_remove_button.setOnClickListener(v -> {

            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            int id = parseInt(sale_id_text.getText().toString());
            Sale sale = salesMap.get(id);

            if(sale == null){
                Toast.makeText(getActivity(),"Δε βρέθηκε Πώληση",Toast.LENGTH_LONG).show();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return;
            }
            DocumentReference documentRef = salesReference.document(sale_id_text.getText().toString());
            documentRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(),"Η πώληση διαγράφτηκε",Toast.LENGTH_LONG).show();
                        salesMap.remove(id);
                        sales.remove(sale);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        sale_id_text.setText("");
                        client_id_text.setText("");
                        sale_date_text.setText("");
                        sale_order_text.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(),"Δε βρέθηκε Πώληση",Toast.LENGTH_LONG).show();
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        sale_id_text.setText("");
                        client_id_text.setText("");
                        sale_date_text.setText("");
                        sale_order_text.setText("");
                    });
        });

        sale_edit_button.setOnClickListener(v -> {

            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            int id = parseInt(sale_id_text.getText().toString());
            Sale sale = salesMap.get(id);

            if(sale == null){
                Toast.makeText(getActivity(),"Δε βρέθηκε Πώληση",Toast.LENGTH_LONG).show();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return;
            }
            sale.setSale_date(sale_order_text.getText().toString());
            sale.setOrder_date(sale_date_text.getText().toString());
            sale.setClient_id(parseInt(client_id_text.getText().toString()));
            Map<String, Object> updates = new HashMap<>();
            updates.put("client_id", sale.getClient_id());
            updates.put("value", sale.getValue());
            updates.put("sale_date", sale.getSale_date());
            updates.put("order_date", sale.getOrder_date());
            DocumentReference documentRef = salesReference.document(sale_id_text.getText().toString());
            documentRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(),"Τα στοιχεία ενημερώθηκαν",Toast.LENGTH_LONG).show();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                sale_id_text.setText("");
                client_id_text.setText("");
                sale_date_text.setText("");
                sale_order_text.setText("");
            }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(),"Τα στοιχεία δεν ενημερώθηκαν",Toast.LENGTH_LONG).show();
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        sale_id_text.setText("");
                        client_id_text.setText("");
                        sale_date_text.setText("");
                        sale_order_text.setText("");
                    });
        });


        sale_id_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    client_id_text.setText("");
                    sale_date_text.setText("");
                    sale_order_text.setText("");

                } else {
                    int saleId = parseInt(s.toString());
                    Sale sale = salesMap.get(saleId);

                    if(sale == null) {
                        client_id_text.setText("");
                        sale_date_text.setText("");
                        sale_order_text.setText("");
                        return;
                    }

                    client_id_text.setText(String.valueOf(sale.getClient_id()));
                    sale_date_text.setText(sale.getSale_date());
                    sale_order_text.setText(sale.getOrder_date());

                }
            }
        });
        return view;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            System.out.println("Could not parse " + ex);
            return -1;
        }
    }

}
