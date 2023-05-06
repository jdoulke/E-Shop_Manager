package com.ihu.e_shopmanager.orders;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchOrder extends Fragment {

    private Set<String> orders = new HashSet<>();

    private HashMap<String, Product> productHashMap = new HashMap<>();
    private HashMap<Integer, Client> clientIDMap = new HashMap<>();
    private HashMap<Long, Client> clientPhoneMap = new HashMap<>();
    private HashMap<Integer, Order> orderMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.order_search_fragment, container, false);

        EditText order_search_order_id = view.findViewById(R.id.order_search_order_id);
        EditText order_search_client_id = view.findViewById(R.id.order_search_client_id);
        EditText order_search_client_phone = view.findViewById(R.id.order_search_cleint_phone);

        TextView order_search_client_view = view.findViewById(R.id.order_search_client_view);
        TextView order_search_price_view = view.findViewById(R.id.order_search_price_view);

        LinearLayout order_search_linearlayout = view.findViewById(R.id.order_search_linearlayout);

        Spinner orderSpinner = view.findViewById(R.id.order_search_spinner);

        Button order_search_button = view.findViewById(R.id.order_search_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);

        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        List<Order> orders = MainActivity.myAppDatabase.myDao().getOrders();

        List<Order> clientOrder = new ArrayList<>();




        for(Product product : products)
            productHashMap.put(product.getName(), product);
        for (Client client : clients) {
            clientIDMap.put(client.getId(), client);
            clientPhoneMap.put(client.getPhone_number(), client);
        }
        for(Order order : orders)
            orderMap.put(order.getId(), order);

        order_search_linearlayout.addView(headerViews(inflater));

        registerEditTextListeners(view);

        order_search_button.setOnClickListener(v -> {

            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            if(!order_search_order_id.getText().toString().isEmpty()){

                int id = parseInt(order_search_order_id.getText().toString());
                Order order = orderMap.get(id);

                if(order == null){
                    Toast.makeText(getActivity(),"Δε βρέθηκε παραγγελία",Toast.LENGTH_LONG).show();
                    return;
                }
                adapter.clear();
                adapter.add("Παραγγελία " + order.getId());
                orderSpinner.setAdapter(adapter);
                order_search_linearlayout.removeAllViews();
                order_search_linearlayout.addView(headerViews(inflater));

                List<ProductWithQuantity> productWithQuantities = order.getProducts();
                for(ProductWithQuantity productWithQuantity : productWithQuantities) {
                    View productView = inflater.inflate(R.layout.order_search_item, null);
                    Product product = productHashMap.get(productWithQuantity.getProduct().getName());
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
                    order_search_linearlayout.addView(productView);
                }
                String formattedPrice = String.format("%.2f", order.getTotalPrice());
                order_search_price_view.setText("Σύνολο: " + formattedPrice + "€");

            }else if(!order_search_client_id.getText().toString().isEmpty()){

            }else if(!order_search_client_phone.getText().toString().isEmpty()){

            }

        });

        return view;
    }

    private void registerEditTextListeners(View view){

        EditText order_search_order_id = view.findViewById(R.id.order_search_order_id);
        EditText order_search_client_id = view.findViewById(R.id.order_search_client_id);
        EditText order_search_client_phone = view.findViewById(R.id.order_search_cleint_phone);

        TextView client_view = view.findViewById(R.id.order_search_client_view);

        order_search_order_id.addTextChangedListener(new TextWatcher() {
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
                if (s.toString().isEmpty()) {
                    client_view.setText("Πελάτης: ");
                } else {
                    int id = parseInt(s.toString());
                    Order order = orderMap.get(id);

                    if(order == null){
                        client_view.setText("Πελάτης: ");
                        return;
                    }
                    Client client = clientIDMap.get(order.getClientId());
                    client_view.setText("Πελάτης: " + client.getName() + " " + client.getLastname());
                }
            }
        });
        order_search_client_id.addTextChangedListener(new TextWatcher() {
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
                if (s.toString().isEmpty()) {
                    client_view.setText("Πελάτης: ");
                } else {
                    int id = parseInt(s.toString());
                    Client client = clientIDMap.get(id);

                    if(client == null){
                        client_view.setText("Πελάτης: ");
                        return;
                    }
                    client_view.setText("Πελάτης: " + client.getName() + " " + client.getLastname());
                }
            }
        });

        order_search_client_phone.addTextChangedListener(new TextWatcher() {
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
                if (s.toString().isEmpty()) {
                    client_view.setText("Πελάτης: ");
                } else {
                    long phone_number = parseLong(s.toString());
                    Client client = clientPhoneMap.get(phone_number);

                    if(client == null){
                        client_view.setText("Πελάτης: ");
                        return;
                    }
                    client_view.setText("Πελάτης: " + client.getName() + " " + client.getLastname());
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
    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ex) {
            System.out.println("Could not parse " + ex);
            return 0;
        }
    }

    private static String formatDate(String s) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date selectedDate = dateFormat.parse(s);
            return dateFormat.format(selectedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
