package com.ihu.e_shopmanager.sales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
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

public class SalesFragment extends Fragment implements View.OnClickListener{

    Button searchSale, editSale;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.sales_fragment, container, false);
        else
            view = inflater.inflate(R.layout.sales_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πωλήσεις");

        searchSale = view.findViewById(R.id.sale_search_button);
        searchSale.setOnClickListener(this);
        editSale = view.findViewById(R.id.sale_edit_button);
        editSale.setOnClickListener(this);

        TextView total_sales = view.findViewById(R.id.total_sales);

        LinearLayout mLinearLayout = view.findViewById(R.id.sale_linearlayout);

        CollectionReference salesReference = MainActivity.firestoreDatabase.collection("Sales");

        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        List<ProductWithQuantity> products = new ArrayList<>();
        List<Sale> sales = new ArrayList<>();

        HashMap<Integer, Client> clientMap = new HashMap<>();


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
                        products.add(productWithQuantity);
                    }
                    Sale sale = new Sale();
                    sale.setProductsList(products);
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

            for (Client client : clients)
                clientMap.put(client.getId(), client);

            @SuppressLint("InflateParams") View headerView  = inflater.inflate(R.layout.order_list_item, null);
            TextView idTextView = headerView.findViewById(R.id.order_child_id);
            TextView clientNameTextView = headerView.findViewById(R.id.order_child_client_name);
            TextView priceTextView = headerView.findViewById(R.id.order_child_total_price);
            TextView dateTextView = headerView.findViewById(R.id.order_child_date);
            idTextView.setText("ID");
            clientNameTextView.setText("Πελάτης");
            priceTextView.setText("Αξία");
            dateTextView.setText("Ημ/νια Πώλησης");
            float totalPrice = 0;
            mLinearLayout.addView(headerView);
            for (Sale sale : sales) {
                @SuppressLint("InflateParams") View productView = inflater.inflate(R.layout.order_list_item, null);
                TextView idView = productView.findViewById(R.id.order_child_id);
                TextView clientNameView = productView.findViewById(R.id.order_child_client_name);
                TextView priceView = productView.findViewById(R.id.order_child_total_price);
                TextView dateView = productView.findViewById(R.id.order_child_date);
                idView.setText(String.valueOf(sale.getSale_id()));
                Client client = clientMap.get(sale.getClient_id());
                if(client != null) {
                    clientNameView.setText(client.getName() + " " + client.getLastname());
                    @SuppressLint("DefaultLocale") String formattedPrice = String.format("%.2f", sale.getValue());
                    priceView.setText(formattedPrice + "€");
                    dateView.setText(sale.getSale_date());
                    totalPrice += sale.getValue();
                }
                mLinearLayout.addView(productView);
            }
            @SuppressLint("DefaultLocale") String formattedPrice = String.format("%.2f", totalPrice);
            total_sales.setText("Σύνολο Πωλήσεων: " + formattedPrice + "€");
            Toast.makeText(getActivity(),"Φορτώθηκαν τα δεδομένα.",Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Log.d("FireStore ERROR: ", e.getMessage());
        });


        return view;
    }

    @Override
    public void onClick(View v) {

        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(40);
        if (v.getId() == R.id.sale_search_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchSale()).addToBackStack(null).commit();
        else if (v.getId() == R.id.sale_edit_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new EditSale()).addToBackStack(null).commit();

    }



}
