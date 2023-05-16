package com.ihu.e_shopmanager.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;

import java.util.HashMap;
import java.util.List;

public class OrdersFragment extends Fragment implements View.OnClickListener {

    Button addOrder, editOrder, trackOrder, finishOrder;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.orders_fragment, container, false);
        else
            view = inflater.inflate(R.layout.orders_landscape_fragment, container, false);


        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Παραγγελίες");

        addOrder = view.findViewById(R.id.order_add_button);
        addOrder.setOnClickListener(this);
        editOrder = view.findViewById(R.id.order_edit_button);
        editOrder.setOnClickListener(this);
        trackOrder = view.findViewById(R.id.order_search_button);
        trackOrder.setOnClickListener(this);
        finishOrder = view.findViewById(R.id.order_finish_button);
        finishOrder.setOnClickListener(this);

        List<Order> orders = MainActivity.myAppDatabase.myDao().getOrders();
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();


        HashMap<Integer, Client> clientMap = new HashMap<>();


        for (Client client : clients)
            clientMap.put(client.getId(), client);

        @SuppressLint("InflateParams") View headerView  = inflater.inflate(R.layout.order_list_item, null);
        TextView idTextView = headerView.findViewById(R.id.order_child_id);
        TextView clientNameTextView = headerView.findViewById(R.id.order_child_client_name);
        TextView priceTextView = headerView.findViewById(R.id.order_child_total_price);
        TextView dateTextView = headerView.findViewById(R.id.order_child_date);
        LinearLayout mLinearLayout = view.findViewById(R.id.order_linearlayout);

        idTextView.setText("ID");
        clientNameTextView.setText("Πελάτης");
        priceTextView.setText("Αξία");
        dateTextView.setText("Ημ/νια Παραγγελίας");
        mLinearLayout.addView(headerView);
        for (Order order : orders) {
            @SuppressLint("InflateParams") View productView = inflater.inflate(R.layout.order_list_item, null);
            idTextView = productView.findViewById(R.id.order_child_id);
            clientNameTextView = productView.findViewById(R.id.order_child_client_name);
            priceTextView = productView.findViewById(R.id.order_child_total_price);
            dateTextView = productView.findViewById(R.id.order_child_date);

            idTextView.setText(String.valueOf(order.getId()));
            Client client = clientMap.get(order.getClientId());
            assert client != null;
            clientNameTextView.setText(client.getName() + " " + client.getLastname());
            @SuppressLint("DefaultLocale") String formattedPrice = String.format("%.2f", order.getTotalPrice());
            priceTextView.setText(formattedPrice + "€");
            dateTextView.setText(String.valueOf(order.getOrderDate()));

            mLinearLayout.addView(productView);
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
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
