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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class SearchSale extends Fragment {

    private final HashMap<Integer, Client> clientMap = new HashMap<>();
    private final HashMap<Integer, Product> productHashMap = new HashMap<>();
    private final HashMap<Integer, Sale> salesMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.sale_search_fragment, container, false);
        else
            view = inflater.inflate(R.layout.sale_search_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πωλήσεις");

        EditText sale_search_sale_id = view.findViewById(R.id.sale_search_sale_id);

        TextView sale_search_price_view = view.findViewById(R.id.sale_search_price_view);
        TextView orderDateView = view.findViewById(R.id.sale_search_order_date_view);
        TextView saleDateView = view.findViewById(R.id.sale_search_sale_date_view);

        LinearLayout mLinearLayout = view.findViewById(R.id.sale_search_linearlayout);

        Button searchButton = view.findViewById(R.id.sale_search_button);


        CollectionReference salesReference = MainActivity.firestoreDatabase.collection("Sales");

        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();
        List<Sale> sales = new ArrayList<>();
        List<ProductWithQuantity> productsWithQuantity = new ArrayList<>();

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        for (Client client : clients) {
            clientMap.put(client.getId(), client);
        }
        for(Product product : products)
            productHashMap.put(product.getId(), product);

        mLinearLayout.addView(headerViews(inflater));

        registerEditTextListeners(view);

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

        searchButton.setOnClickListener(v -> {

            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            int id = parseInt(sale_search_sale_id.getText().toString());
            Sale sale = salesMap.get(id);

            if(sale == null){
                Toast.makeText(getActivity(),"Δε βρέθηκε Πώληση",Toast.LENGTH_LONG).show();
                resetViews(view, inflater);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return;
            }
            List<ProductWithQuantity> productWithQuantities = sale.getProductsList();
            orderDateView.setText(" Ημερομηνία Παραγγελίας: " + sale.getOrder_date());
            saleDateView.setText(" Ημερομηνία Πώλησης: " + sale.getSale_date());
            for(ProductWithQuantity productWithQuantity : productWithQuantities) {
                View productView = inflater.inflate(R.layout.order_search_item, null);
                Product product = productHashMap.get(productWithQuantity.getProduct().getId());
                if(product != null) {
                    TextView idView = productView.findViewById(R.id.order_search_child_id);
                    TextView nameView = productView.findViewById(R.id.order_search_child_name);
                    TextView categoryView = productView.findViewById(R.id.order_search_child_category);
                    TextView quantityView = productView.findViewById(R.id.order_search_child_quantity);
                    TextView priceView = productView.findViewById(R.id.order_search_child_price);
                    idView.setText(String.valueOf(product.getId()));
                    nameView.setText(product.getName());
                    categoryView.setText(product.getCategory());
                    quantityView.setText(String.valueOf(productWithQuantity.getQuantity()));
                    priceView.setText(product.getPrice() + "€");
                    ViewGroup currentParent = (ViewGroup) productView.getParent();
                    if (currentParent != null) {
                        currentParent.removeView(productView);
                    }
                    mLinearLayout.addView(productView);
                }
            }


            String formattedPrice = String.format("%.2f", sale.getValue());
            sale_search_price_view.setText("Σύνολο  " + formattedPrice + "€");
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });



        return view;
    }

    private void resetViews(View view, LayoutInflater inflater) {



        TextView order_search_price_view = view.findViewById(R.id.sale_search_price_view);
        TextView orderDateView = view.findViewById(R.id.sale_search_order_date_view);
        TextView saleDateView = view.findViewById(R.id.sale_search_sale_date_view);

        LinearLayout order_search_linearlayout = view.findViewById(R.id.order_search_linearlayout);


        order_search_linearlayout.removeAllViews();
        order_search_linearlayout.addView(headerViews(inflater));
        order_search_price_view.setText(" Σύνολο: 0€");
        orderDateView.setText(" Ημερομηνία Παραγγελίας: ");
        saleDateView.setText(" Ημερομηνία Πώλησης: ");

    }


    private void registerEditTextListeners(View view){

        EditText sale_search_sale_id = view.findViewById(R.id.sale_search_sale_id);


        TextView client_view = view.findViewById(R.id.sale_search_client_view);

        sale_search_sale_id.addTextChangedListener(new TextWatcher() {
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
                    client_view.setText(" Πελάτης: ");
                } else {
                    int id = parseInt(s.toString());
                    Sale sale = salesMap.get(id);

                    if(sale == null){
                        client_view.setText(" Πελάτης: ");
                        return;
                    }
                    Client client = clientMap.get(sale.getClient_id());
                    if(client != null)
                        client_view.setText(" Πελάτης: " + client.getName() + " " + client.getLastname());
                }
            }
        });

    }

    private View headerViews(LayoutInflater inflater) {

        View headerView = inflater.inflate(R.layout.order_search_item, null);
        TextView idTextView = headerView.findViewById(R.id.order_search_child_id);
        TextView nameTextView = headerView.findViewById(R.id.order_search_child_name);
        TextView categoryTextView = headerView.findViewById(R.id.order_search_child_category);
        TextView quantityTextView = headerView.findViewById(R.id.order_search_child_quantity);
        TextView priceTextView = headerView.findViewById(R.id.order_search_child_price);
        idTextView.setText("ID");
        nameTextView.setText("Όνομα Προϊόντος");
        categoryTextView.setText("Κατηγορία");
        quantityTextView.setText("Τμχ");
        priceTextView.setText("Αξία");

        return headerView;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            System.out.println("Could not parse " + ex);
            return 0;
        }
    }

}
