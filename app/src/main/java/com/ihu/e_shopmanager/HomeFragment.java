package com.ihu.e_shopmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;
import com.ihu.e_shopmanager.products.ProductWithQuantity;
import com.ihu.e_shopmanager.sales.Sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private List<Sale> sales = new ArrayList<>();
    HashMap<Integer, Client> clientMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Αρχική");

        TextView bestClient, bestProduct, bestDate, turnover, infoDescription;

        bestDate = view.findViewById(R.id.bestDate);
        bestProduct = view.findViewById(R.id.bestProduct);
        infoDescription = view.findViewById(R.id.infoDescription);
        turnover = view.findViewById(R.id.turnover);

        Spinner infoSpinner = view.findViewById(R.id.infoSpinner);

        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        List<ProductWithQuantity> productWithQuantities = new ArrayList<>();
        getSales(view);


        for (Client client : clients)
            clientMap.put(client.getId(), client);


        return view;

    }

    @SuppressLint("SetTextI18n")
    private void getSales(View view) {

        List<ProductWithQuantity> products = new ArrayList<>();
        CollectionReference salesReference = MainActivity.firestoreDatabase.collection("Sales");
        TextView bestSale = view.findViewById(R.id.bestSale);
        TextView bestClient = view.findViewById(R.id.bestClient);
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

            for (Sale sale : sales) {
                int clientId = sale.getClient_id();
                if(clientSalesCounts.containsKey(clientId)) {
                    clientSalesCounts.put(clientId, clientSalesCounts.get(clientId) + 1);
                }else
                    clientSalesCounts.put(clientId, 0);
            }

            int maxSalesCount = 0;
            int clientWithMaxSales = -1;

            for (Map.Entry<Integer, Integer> entry : clientSalesCounts.entrySet()) {
                int clientId = entry.getKey();
                int count = entry.getValue();

                if (count > maxSalesCount) {
                    maxSalesCount = count;
                    clientWithMaxSales = clientId;
                }
            }
            Client client = clientMap.get(clientWithMaxSales);
            if(client != null)
                bestClient.setText("Καλύτερος Πελάτης: " + client.getName() + " " + client.getLastname());
        }).addOnFailureListener(e -> {
            Log.d("FireStore ERROR: ", e.getMessage());
        });
        Query query = salesReference.orderBy("value",  Query.Direction.DESCENDING).limit(1);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
            Sale sale = documentSnapshot.toObject(Sale.class);
            if(sale != null)
                bestSale.setText("Μεγαλύτερη Πώληση: " + sale.getValue() + "€");
        }).addOnFailureListener(e -> Toast.makeText(getActivity(),"query operation failed.",Toast.LENGTH_LONG).show());





    }


}
