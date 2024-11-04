package com.ihu.e_shopmanager;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;
import com.ihu.e_shopmanager.products.ProductWithQuantity;
import com.ihu.e_shopmanager.sales.Sale;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private final List<Sale> sales = new ArrayList<>();
    HashMap<Integer, Client> clientMap = new HashMap<>();
    HashMap<Integer, Product> productMap = new HashMap<>();

    private CollectionReference salesReference;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.home_fragment, container, false);
        else
            view = inflater.inflate(R.layout.home_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Αρχική");

        Spinner infoSpinner = view.findViewById(R.id.infoSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.add("Πωλήσεις στις 08/05/2023");
        adapter.add("Πωλήσεις άνω των 2000€");
        adapter.add("Πωλήσεις που ολοκληρώθηκαν σε μία μέρα");
        adapter.add("Παραγγελίες στις 08/05/2023");
        adapter.add("Παραγγελίες άνω των 2000€");
        adapter.add("Προϊόντα με απόθεμα άνω των 20");
        adapter.add("Προϊόντα στην κατηγορία Laptop");
        adapter.add("Πελάτες με Παραγγελίες άνω των 2500€");
        adapter.add("Πελάτες που έχουν παραγγείλει κάποιο Desktop");
        adapter.add("Παραγγελίες που δεν έχουν όνομα Κώστας");
        adapter.add("Παραγγελίες του Λιγκουίνη με αξία άνω των 700€");
        infoSpinner.setAdapter(adapter);
        salesReference = MainActivity.firestoreDatabase.collection("Sales");
        infoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                runQueries(position, view, inflater);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();
        for (Product product : products)
            productMap.put(product.getId(), product);

        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        for (Client client : clients)
            clientMap.put(client.getId(), client);

        getSales(view);


        return view;

    }


    @SuppressLint("SetTextI18n")
    private void getSales(View view) {

        List<ProductWithQuantity> products = new ArrayList<>();
        TextView bestSale = view.findViewById(R.id.bestSale);
        TextView bestClient = view.findViewById(R.id.bestClient);
        TextView bestProduct = view.findViewById(R.id.bestProduct);
        TextView turnover = view.findViewById(R.id.turnover);
        sales.clear();
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
            Map<Integer, Integer> clientSalesCounts = new HashMap<>();
            Map<Integer, Integer> productSalesCounts = new HashMap<>();
            float totalPrice = 0;

            for (Sale sale : sales) {
                int clientId = sale.getClient_id();
                for(ProductWithQuantity productWithQuantity : sale.getProductsList()){
                    Product tempProduct = productWithQuantity.getProduct();
                    if(productSalesCounts.containsKey(productWithQuantity.getProduct().getId())){
                        productSalesCounts.put(tempProduct.getId(), productWithQuantity.getQuantity() + productSalesCounts.get(tempProduct.getId()));
                    }else
                        productSalesCounts.put(tempProduct.getId(), productWithQuantity.getQuantity());
                }
                if(clientSalesCounts.containsKey(clientId)) {
                    clientSalesCounts.put(clientId, clientSalesCounts.get(clientId) + 1);
                }else
                    clientSalesCounts.put(clientId, 1);
                totalPrice += sale.getValue();
            }

            int maxSalesCount = 0;
            int clientWithMaxSales = -1;
            int productWithMaxSales = -1;

            for (Map.Entry<Integer, Integer> entry : clientSalesCounts.entrySet()) {
                int clientId = entry.getKey();
                int count = entry.getValue();

                if (count > maxSalesCount) {
                    maxSalesCount = count;
                    clientWithMaxSales = clientId;
                }
            }
            Client client = clientMap.get(clientWithMaxSales);

            maxSalesCount = 0;

            for (Map.Entry<Integer, Integer> entry : productSalesCounts.entrySet()) {
                int productId = entry.getKey();
                int count = entry.getValue();

                if (count > maxSalesCount) {
                    maxSalesCount = count;
                    productWithMaxSales = productId;
                }
            }
            Product product = productMap.get(productWithMaxSales);
            if(client != null && product != null) {
                bestClient.setText("Καλύτερος Πελάτης: " + client.getName() + " " + client.getLastname());
                bestProduct.setText("Δημοφιλέστερο Προϊόν: " + product.getName());
            }
            @SuppressLint("DefaultLocale") String formattedPrice = String.format("%.2f", totalPrice);
            turnover.setText("Συνολικός Τζίρος: "+ formattedPrice + "€");
        }).addOnFailureListener(e -> {
            Log.d("FireStore ERROR: ", e.getMessage());
        });
        Query query = salesReference.orderBy("value",  Query.Direction.DESCENDING).limit(1);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.getDocuments().isEmpty()) {
                if (queryDocumentSnapshots.getDocuments().get(0).exists()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    Sale sale = documentSnapshot.toObject(Sale.class);
                    if (sale != null)
                        bestSale.setText("Μεγαλύτερη Πώληση: " + sale.getValue() + "€");
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(),"query operation failed.",Toast.LENGTH_LONG).show());

    }

    @SuppressLint("SetTextI18n")
    private void runQueries(int position, View view, LayoutInflater inflater) {

        LinearLayout linearLayout = view.findViewById(R.id.info_linearlayout);
        TextView info_item = view.findViewById(R.id.info_item);

    }


}
