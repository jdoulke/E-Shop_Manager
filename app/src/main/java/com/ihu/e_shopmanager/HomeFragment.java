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

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.products.Product;
import com.ihu.e_shopmanager.products.ProductWithQuantity;
import com.ihu.e_shopmanager.sales.Sale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private List<Sale> sales = new ArrayList<>();
    HashMap<Integer, Client> clientMap = new HashMap<>();
    HashMap<Integer, Product> productMap = new HashMap<>();

    private CollectionReference salesReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
            String formattedPrice = String.format("%.2f", totalPrice);
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
        switch (position){
            case 0: //Πωλήσεις στις 08/05/2023
                Query query1 = salesReference.whereEqualTo("sale_date", "08/05/2023");
                linearLayout.removeAllViews();
                query1.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    View headerView = inflater.inflate(R.layout.order_list_item, null);
                    TextView idTextView = headerView.findViewById(R.id.order_child_id);
                    TextView clientNameTextView = headerView.findViewById(R.id.order_child_client_name);
                    TextView priceTextView = headerView.findViewById(R.id.order_child_total_price);
                    TextView dateTextView = headerView.findViewById(R.id.order_child_date);
                    idTextView.setText("ID");
                    clientNameTextView.setText("Πελάτης");
                    priceTextView.setText("Αξία");
                    dateTextView.setText("Ημ/νια Πώλησεις");
                    linearLayout.addView(headerView);
                    for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        Sale sale = documentSnapshot.toObject(Sale.class);
                        View saleView = inflater.inflate(R.layout.order_list_item, null);
                        idTextView = saleView.findViewById(R.id.order_child_id);
                        clientNameTextView = saleView.findViewById(R.id.order_child_client_name);
                        priceTextView = saleView.findViewById(R.id.order_child_total_price);
                        dateTextView = saleView.findViewById(R.id.order_child_date);

                        idTextView.setText(String.valueOf(sale.getSale_id()));
                        Client client = clientMap.get(sale.getClient_id());
                        clientNameTextView.setText(client.getName() + " " + client.getLastname());
                        String formattedPrice = String.format("%.2f", sale.getValue());
                        priceTextView.setText(formattedPrice + "€");
                        dateTextView.setText(String.valueOf(sale.getSale_date()));
                        linearLayout.addView(saleView);
                    }

                }).addOnFailureListener(e -> Toast.makeText(getActivity(),"Query operation failed.",Toast.LENGTH_LONG).show());
                break;
            case 1: //Πωλήσεις άνω των 2000€
                Query query2 = salesReference.whereGreaterThan("value", 2000);
                linearLayout.removeAllViews();
                query2.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    View headerView = inflater.inflate(R.layout.order_list_item, null);
                    TextView idTextView = headerView.findViewById(R.id.order_child_id);
                    TextView clientNameTextView = headerView.findViewById(R.id.order_child_client_name);
                    TextView priceTextView = headerView.findViewById(R.id.order_child_total_price);
                    TextView dateTextView = headerView.findViewById(R.id.order_child_date);
                    idTextView.setText("ID");
                    clientNameTextView.setText("Πελάτης");
                    priceTextView.setText("Αξία");
                    dateTextView.setText("Ημ/νια Πώλησεις");
                    linearLayout.addView(headerView);
                    for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        Sale sale = documentSnapshot.toObject(Sale.class);
                        View saleView = inflater.inflate(R.layout.order_list_item, null);
                        idTextView = saleView.findViewById(R.id.order_child_id);
                        clientNameTextView = saleView.findViewById(R.id.order_child_client_name);
                        priceTextView = saleView.findViewById(R.id.order_child_total_price);
                        dateTextView = saleView.findViewById(R.id.order_child_date);

                        idTextView.setText(String.valueOf(sale.getSale_id()));
                        Client client = clientMap.get(sale.getClient_id());
                        clientNameTextView.setText(client.getName() + " " + client.getLastname());
                        String formattedPrice = String.format("%.2f", sale.getValue());
                        priceTextView.setText(formattedPrice + "€");
                        dateTextView.setText(String.valueOf(sale.getSale_date()));
                        linearLayout.addView(saleView);
                    }

                }).addOnFailureListener(e -> Toast.makeText(getActivity(),"query operation failed.",Toast.LENGTH_LONG).show());
                break;
            case 2: //Πωλήσεις που ολοκληρώθηκαν σε μία μέρα
                boolean hasOneDayDifference;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                linearLayout.removeAllViews();
                View headerView = inflater.inflate(R.layout.home_date_item, null);
                TextView idTextView = headerView.findViewById(R.id.home_child_id);
                TextView clientNameTextView = headerView.findViewById(R.id.home_child_client_name);
                TextView orderDateTextView = headerView.findViewById(R.id.home_child_order_date);
                TextView saleDateTextView = headerView.findViewById(R.id.home_child_sale_date);
                idTextView.setText("ID");
                clientNameTextView.setText("Πελάτης");
                orderDateTextView.setText("Ημ/νια Παραγγελίας");
                saleDateTextView.setText("Ημ/νια Πώλησης");
                linearLayout.addView(headerView);
                for(Sale sale : sales) {
                    LocalDate orderDate = LocalDate.parse(sale.getOrder_date(), formatter);
                    LocalDate saleDate = LocalDate.parse(sale.getSale_date(), formatter);
                    hasOneDayDifference = orderDate.plusDays(1).isEqual(saleDate);
                    if(hasOneDayDifference){
                        View saleView = inflater.inflate(R.layout.home_date_item, null);
                        idTextView = saleView.findViewById(R.id.home_child_id);
                        clientNameTextView = saleView.findViewById(R.id.home_child_client_name);
                        orderDateTextView = saleView.findViewById(R.id.home_child_order_date);
                        saleDateTextView = saleView.findViewById(R.id.home_child_sale_date);

                        idTextView.setText(String.valueOf(sale.getSale_id()));
                        Client client = clientMap.get(sale.getClient_id());
                        clientNameTextView.setText(client.getName() + " " + client.getLastname());
                        orderDateTextView.setText(sale.getOrder_date());
                        saleDateTextView.setText(sale.getSale_date());
                        linearLayout.addView(saleView);
                    }
                }
                break;
            case 3: //Παραγγελίες που έγιναν 08/05/2023
                List<Order> ordersQuery = MainActivity.myAppDatabase.myDao().getQuery3();
                linearLayout.removeAllViews();
                View headerView3 = inflater.inflate(R.layout.order_list_item, null);
                TextView idTextView3 = headerView3.findViewById(R.id.order_child_id);
                TextView clientNameTextView3 = headerView3.findViewById(R.id.order_child_client_name);
                TextView priceTextView3 = headerView3.findViewById(R.id.order_child_total_price);
                TextView dateTextView3 = headerView3.findViewById(R.id.order_child_date);
                idTextView3.setText("ID");
                clientNameTextView3.setText("Πελάτης");
                priceTextView3.setText("Αξία");
                dateTextView3.setText("Ημ/νια Πώλησεις");
                linearLayout.addView(headerView3);
                for(Order order : ordersQuery){
                    View orderView = inflater.inflate(R.layout.order_list_item, null);
                    idTextView3 = orderView.findViewById(R.id.order_child_id);
                    clientNameTextView3 = orderView.findViewById(R.id.order_child_client_name);
                    priceTextView3 = orderView.findViewById(R.id.order_child_total_price);
                    dateTextView3 = orderView.findViewById(R.id.order_child_date);

                    idTextView3.setText(String.valueOf(order.getId()));
                    Client client = clientMap.get(order.getClientId());
                    clientNameTextView3.setText(client.getName() + " " + client.getLastname());
                    String formattedPrice = String.format("%.2f", order.getTotalPrice());
                    priceTextView3.setText(formattedPrice + "€");
                    dateTextView3.setText(String.valueOf(order.getOrderDate()));
                    linearLayout.addView(orderView);
                }
                break;
            case 4: //Παραγγελίες άνω των 2000
                List<Order> ordersQuery4 = MainActivity.myAppDatabase.myDao().getQuery4();
                linearLayout.removeAllViews();
                View headerView4 = inflater.inflate(R.layout.order_list_item, null);
                TextView idTextView4 = headerView4.findViewById(R.id.order_child_id);
                TextView clientNameTextView4 = headerView4.findViewById(R.id.order_child_client_name);
                TextView priceTextView4 = headerView4.findViewById(R.id.order_child_total_price);
                TextView dateTextView4 = headerView4.findViewById(R.id.order_child_date);
                idTextView4.setText("ID");
                clientNameTextView4.setText("Πελάτης");
                priceTextView4.setText("Αξία");
                dateTextView4.setText("Ημ/νια Πώλησεις");
                linearLayout.addView(headerView4);
                for(Order order : ordersQuery4){
                    View orderView = inflater.inflate(R.layout.order_list_item, null);
                    idTextView4 = orderView.findViewById(R.id.order_child_id);
                    clientNameTextView4 = orderView.findViewById(R.id.order_child_client_name);
                    priceTextView4 = orderView.findViewById(R.id.order_child_total_price);
                    dateTextView4 = orderView.findViewById(R.id.order_child_date);

                    idTextView4.setText(String.valueOf(order.getId()));
                    Client client = clientMap.get(order.getClientId());
                    clientNameTextView4.setText(client.getName() + " " + client.getLastname());
                    String formattedPrice = String.format("%.2f", order.getTotalPrice());
                    priceTextView4.setText(formattedPrice + "€");
                    dateTextView4.setText(String.valueOf(order.getOrderDate()));
                    linearLayout.addView(orderView);
                }
                break;
            case 5: //Προϊόντα με απόθεμα άνω των 20
                List<Product> productsQuery5 = MainActivity.myAppDatabase.myDao().getQuery5();
                linearLayout.removeAllViews();
                View headerView5  = inflater.inflate(R.layout.product_item, null);
                TextView idTextView5 = headerView5.findViewById(R.id.product_child_id);
                TextView nameTextView5 = headerView5.findViewById(R.id.product_child_name);
                TextView categoryTextView5 = headerView5.findViewById(R.id.product_child_category);
                TextView stockTextView5 = headerView5.findViewById(R.id.product_child_stock);
                TextView priceTextView5 = headerView5.findViewById(R.id.product_child_price);

                idTextView5.setText("ID");
                nameTextView5.setText("Όνομα Προϊόντος");
                categoryTextView5.setText("Κατηγορία");
                stockTextView5.setText("Stock");
                priceTextView5.setText("Αξία");
                linearLayout.addView(headerView5);
                for(Product product : productsQuery5){
                    View productView = inflater.inflate(R.layout.product_item, null);
                    idTextView5 = productView.findViewById(R.id.product_child_id);
                    nameTextView5 = productView.findViewById(R.id.product_child_name);
                    categoryTextView5 = productView.findViewById(R.id.product_child_category);
                    stockTextView5 = productView.findViewById(R.id.product_child_stock);
                    priceTextView5 = productView.findViewById(R.id.product_child_price);

                    idTextView5.setText(String.valueOf(product.getId()));
                    nameTextView5.setText(product.getName());
                    categoryTextView5.setText(product.getCategory());
                    stockTextView5.setText(String.valueOf(product.getStock()));
                    priceTextView5.setText(product.getPrice() + "€");
                    linearLayout.addView(productView);
                }
                break;
            case 6: //Προϊόντα στην κατηγορία Laptop
                List<Product> productsQuery6 = MainActivity.myAppDatabase.myDao().getQuery6();
                linearLayout.removeAllViews();
                View headerView6  = inflater.inflate(R.layout.product_item, null);
                TextView idTextView6 = headerView6.findViewById(R.id.product_child_id);
                TextView nameTextView6 = headerView6.findViewById(R.id.product_child_name);
                TextView categoryTextView6 = headerView6.findViewById(R.id.product_child_category);
                TextView stockTextView6 = headerView6.findViewById(R.id.product_child_stock);
                TextView priceTextView6 = headerView6.findViewById(R.id.product_child_price);

                idTextView6.setText("ID");
                nameTextView6.setText("Όνομα Προϊόντος");
                categoryTextView6.setText("Κατηγορία");
                stockTextView6.setText("Stock");
                priceTextView6.setText("Αξία");
                linearLayout.addView(headerView6);
                for(Product product : productsQuery6){
                    View productView = inflater.inflate(R.layout.product_item, null);
                    idTextView6 = productView.findViewById(R.id.product_child_id);
                    nameTextView6 = productView.findViewById(R.id.product_child_name);
                    categoryTextView6 = productView.findViewById(R.id.product_child_category);
                    stockTextView6 = productView.findViewById(R.id.product_child_stock);
                    priceTextView6 = productView.findViewById(R.id.product_child_price);

                    idTextView6.setText(String.valueOf(product.getId()));
                    nameTextView6.setText(product.getName());
                    categoryTextView6.setText(product.getCategory());
                    stockTextView6.setText(String.valueOf(product.getStock()));
                    priceTextView6.setText(product.getPrice() + "€");
                    linearLayout.addView(productView);
                }
                break;
            case 7: //Πελάτες με παραγγελίες άνω των 2500
                List<Client> clientsQuery7 = MainActivity.myAppDatabase.myDao().getQuery7();
                linearLayout.removeAllViews();
                View headerView7  = inflater.inflate(R.layout.client_item, null);
                TextView idTextView7 = headerView7.findViewById(R.id.client_child_id);
                TextView nameTextView7 = headerView7.findViewById(R.id.client_child_name);
                TextView lastNameTextView7 = headerView7.findViewById(R.id.client_child_lastname);
                TextView phoneTextView7 = headerView7.findViewById(R.id.client_child_phone_number);
                TextView registrationTextView7 = headerView7.findViewById(R.id.client_child_registration_date);

                idTextView7.setText("ID");
                nameTextView7.setText("Όνομα");
                lastNameTextView7.setText("Επίθετο");
                phoneTextView7.setText("Κινητό");
                registrationTextView7.setText("Ημ/νία Εγγραφής");
                linearLayout.addView(headerView7);
                for (Client client : clientsQuery7) {
                    View clientView = inflater.inflate(R.layout.client_item, null);
                    idTextView7 = clientView.findViewById(R.id.client_child_id);
                    nameTextView7 = clientView.findViewById(R.id.client_child_name);
                    lastNameTextView7 = clientView.findViewById(R.id.client_child_lastname);
                    phoneTextView7 = clientView.findViewById(R.id.client_child_phone_number);
                    registrationTextView7 = clientView.findViewById(R.id.client_child_registration_date);

                    idTextView7.setText(String.valueOf(client.getId()));
                    nameTextView7.setText(client.getName());
                    lastNameTextView7.setText(client.getLastname());
                    phoneTextView7.setText(String.valueOf(client.getPhone_number()));
                    registrationTextView7.setText(client.getRegisteration_date());
                    linearLayout.addView(clientView);
                }
                break;
            case 8:
                List<Order> orderQuery8 = MainActivity.myAppDatabase.myDao().getOrders();
                List<ProductWithQuantity> query8Products;
                linearLayout.removeAllViews();
                View headerView8  = inflater.inflate(R.layout.client_item, null);
                TextView idTextView8 = headerView8.findViewById(R.id.client_child_id);
                TextView nameTextView8 = headerView8.findViewById(R.id.client_child_name);
                TextView lastNameTextView8 = headerView8.findViewById(R.id.client_child_lastname);
                TextView phoneTextView8 = headerView8.findViewById(R.id.client_child_phone_number);
                TextView registrationTextView8 = headerView8.findViewById(R.id.client_child_registration_date);

                idTextView8.setText("ID");
                nameTextView8.setText("Όνομα");
                lastNameTextView8.setText("Επίθετο");
                phoneTextView8.setText("Κινητό");
                registrationTextView8.setText("Ημ/νία Εγγραφής");
                linearLayout.addView(headerView8);
                Set<Integer> clientsWithDesktop = new HashSet<>();
                for(Order order : orderQuery8){
                    query8Products = order.getProducts();
                    Client client = clientMap.get(order.getClientId());
                    assert client != null;
                    if(!clientsWithDesktop.contains(client.getId())){
                        for(ProductWithQuantity productWithQuantity: query8Products){
                            Product product = productWithQuantity.getProduct();
                            if(product.getCategory().equals("Desktop")) {
                            View clientView = inflater.inflate(R.layout.client_item, null);
                            idTextView8 = clientView.findViewById(R.id.client_child_id);
                            nameTextView8 = clientView.findViewById(R.id.client_child_name);
                            lastNameTextView8 = clientView.findViewById(R.id.client_child_lastname);
                            phoneTextView8 = clientView.findViewById(R.id.client_child_phone_number);
                            registrationTextView8 = clientView.findViewById(R.id.client_child_registration_date);

                            idTextView8.setText(String.valueOf(client.getId()));
                            nameTextView8.setText(client.getName());
                            lastNameTextView8.setText(client.getLastname());
                            phoneTextView8.setText(String.valueOf(client.getPhone_number()));
                            registrationTextView8.setText(client.getRegisteration_date());
                            linearLayout.addView(clientView);
                            clientsWithDesktop.add(client.getId());
                            }
                        }
                    }
                }
                break;
        }

    }


}
