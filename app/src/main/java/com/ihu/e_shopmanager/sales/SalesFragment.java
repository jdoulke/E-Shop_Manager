package com.ihu.e_shopmanager.sales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.orders.CompletionOfOrder;
import com.ihu.e_shopmanager.orders.DeleteOrder;
import com.ihu.e_shopmanager.orders.InsertOrder;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.orders.ProductWithQuantity;
import com.ihu.e_shopmanager.orders.SearchOrder;
import com.ihu.e_shopmanager.products.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesFragment extends Fragment implements View.OnClickListener{

    Button searchSale, removeSale;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sales_fragment, container, false);
        searchSale = view.findViewById(R.id.sale_search_button);
        searchSale.setOnClickListener(this);
        removeSale = view.findViewById(R.id.sale_remove_button);
        removeSale.setOnClickListener(this);

        CollectionReference salesReference = MainActivity.firestoreDatabase.collection("Sales");

        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        List<Order> orders = MainActivity.myAppDatabase.myDao().getOrders();
        List<ProductWithQuantity> products = new ArrayList<>();
        List<Sale> sales = new ArrayList<>();

        HashMap<Integer, Client> clientMap = new HashMap<>();


        salesReference.get().addOnSuccessListener(querySnapshot -> {
            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            for (DocumentSnapshot document : documents) {
                if (document.exists()) {
                    List<Map<String, Object>> productList = (List<Map<String, Object>>) document.get("productsList");
                    for (Map<String, Object> productMap : productList) {
                        String category = (String) productMap.get("category");
                        int id = ((Long) productMap.get("id")).intValue();
                        String name = (String) productMap.get("name");
                        float price = ((Double) productMap.get("price")).floatValue();
                        int stock = ((Long) productMap.get("stock")).intValue();
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
        }).addOnFailureListener(e -> {
            // Handle any errors
        });

        for (Client client : clients)
            clientMap.put(client.getId(), client);

        View headerView  = inflater.inflate(R.layout.order_list_item, null);
        TextView idTextView = headerView.findViewById(R.id.order_child_id);
        TextView clientNameTextView = headerView.findViewById(R.id.order_child_client_name);
        TextView priceTextView = headerView.findViewById(R.id.order_child_total_price);
        TextView dateTextView = headerView.findViewById(R.id.order_child_date);

        LinearLayout mLinearLayout = view.findViewById(R.id.sale_linearlayout);

        idTextView.setText("ID");
        clientNameTextView.setText("Πελάτης");
        priceTextView.setText("Αξία");
        dateTextView.setText("Ημ/νια Πώλησης");
        mLinearLayout.addView(headerView);
        // Inflate client_item.xml for each client and add them to LinearLayout
        for (Sale sale : sales) {
            View productView = inflater.inflate(R.layout.order_list_item, null);
            idTextView = productView.findViewById(R.id.order_child_id);
            clientNameTextView = productView.findViewById(R.id.order_child_client_name);
            priceTextView = productView.findViewById(R.id.order_child_total_price);
            dateTextView = productView.findViewById(R.id.order_child_date);

            idTextView.setText(String.valueOf(sale.getSale_id()));
            Client client = clientMap.get(sale.getClient_id());
            if(client != null) {
                clientNameTextView.setText(client.getName() + " " + client.getLastname());
                String formattedPrice = String.format("%.2f", sale.getValue());
                priceTextView.setText(formattedPrice + "€");
                dateTextView.setText(String.valueOf(sale.getSale_date()));
            }

            mLinearLayout.addView(productView);
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(40);
        if (v.getId() == R.id.order_add_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new InsertOrder()).addToBackStack(null).commit();
        else if (v.getId() == R.id.order_edit_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new DeleteOrder()).addToBackStack(null).commit();
        else if (v.getId() == R.id.order_search_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchOrder()).addToBackStack(null).commit();
        else if (v.getId() == R.id.order_finish_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new CompletionOfOrder()).addToBackStack(null).commit();

    }

}
