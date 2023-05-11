package com.ihu.e_shopmanager.orders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
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

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;
import com.ihu.e_shopmanager.products.ProductWithQuantity;
import com.ihu.e_shopmanager.sales.Sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompletionOfOrder extends Fragment {

    private HashMap<Integer, Order> orderMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.order_complilation_fragment, container, false);
        else
            view = inflater.inflate(R.layout.order_complilation_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Παραγγελίες");

        List<Order> orders = MainActivity.myAppDatabase.myDao().getOrders();


        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }

        registerListeners(view);


        return view;
    }


    private void registerListeners(View view) {

        EditText order_finish_order_id = view.findViewById(R.id.order_finish_order_id);

        TextView order_client_view = view.findViewById(R.id.order_finish_client_view);
        TextView dateView = view.findViewById(R.id.order_finish_date_view);
        TextView order_id_view = view.findViewById(R.id.order_finish_id_view);
        TextView order_total_price_view = view.findViewById(R.id.order_finish_price_view);

        Button finishButton = view.findViewById(R.id.order_finish_button);

        order_finish_order_id.addTextChangedListener(new TextWatcher() {
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
                    order_client_view.setText(" Πελάτης: ");
                    dateView.setText(" Ημερομηνία Παραγγελίας: ");
                    order_id_view.setText(" Παραγγελία: ");
                    order_total_price_view.setText(" Σύνολο: 0€");
                } else {
                    int orderID = parseInt(s.toString());
                    Order order = orderMap.get(orderID);

                    if (order == null) {
                        order_client_view.setText(" Πελάτης: ");
                        dateView.setText(" Ημερομηνία Παραγγελίας: ");
                        order_id_view.setText(" Παραγγελία: ");
                        order_total_price_view.setText(" Σύνολο: 0€");
                        return;
                    }

                    Client client = MainActivity.myAppDatabase.myDao().getClientFromOrder(orderID);
                    if (client == null) {
                        order_client_view.setText(" Πελάτης: ");
                        dateView.setText(" Ημερομηνία Παραγγελίας: ");
                        order_id_view.setText(" Παραγγελία: ");
                        order_total_price_view.setText(" Σύνολο: 0€");
                        return;
                    }

                    order_client_view.setText(" Πελάτης: " + client.getName() + " " + client.getLastname());
                    dateView.setText(" Ημερομηνία Παραγγελίας: " + order.getOrderDate());
                    order_id_view.setText(" Παραγγελία: " + orderID);
                    String formattedPrice = String.format("%.2f", order.getTotalPrice());
                    order_total_price_view.setText(" Σύνολο: " + formattedPrice + "€");

                }

            }
        });

        finishButton.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            int id = parseInt(order_finish_order_id.getText().toString());
            Order order = orderMap.get(id);
            if (order != null) {
                try {
                    Sale sale = new Sale();
                    Date currentDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = dateFormat.format(currentDate);
                    sale.setSale_date(formattedDate);
                    sale.setProductsList(order.getProducts());
                    sale.setOrder_date(order.getOrderDate());
                    sale.setClient_id(order.getClientId());
                    sale.setValue(order.getTotalPrice());
                    sale.setSale_id(order.getId());
                    CollectionReference salesReference = MainActivity.firestoreDatabase.collection("Sales");
                    List<ProductWithQuantity> products = new ArrayList<>();
                    List<Sale> sales = new ArrayList<>();

                    MainActivity.firestoreDatabase.collection("Sales").
                            document("" + order.getId()).
                            set(sale).addOnCompleteListener(task -> {
                                Toast.makeText(getActivity(), "Η παραγγελία ολοκληρώθηκε επιτυχώς", Toast.LENGTH_LONG).show();
                                MainActivity.myAppDatabase.myDao().deleteOrder(order);
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Η παραγγελία δεν ολοκληρώθηκε", Toast.LENGTH_LONG).show());
                } catch (Exception e) {
                    String message = e.getMessage();
                    System.out.println("ERROR: " + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(getActivity(), "Δε βρέθηκε παραγγελία ", Toast.LENGTH_LONG).show();

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            order_client_view.setText(" Πελάτης: ");
            dateView.setText(" Ημερομηνία Παραγγελίας: ");
            order_id_view.setText(" Παραγγελία: ");
            order_total_price_view.setText(" Σύνολο: 0€");
        });


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
