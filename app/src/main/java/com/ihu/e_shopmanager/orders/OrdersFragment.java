package com.ihu.e_shopmanager.orders;

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

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.clients.SearchClient;
import com.ihu.e_shopmanager.orders.DeleteOrder;
import com.ihu.e_shopmanager.orders.InsertOrder;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.orders.SearchOrder;
import com.ihu.e_shopmanager.products.Product;

import java.util.HashMap;
import java.util.List;

public class OrdersFragment extends Fragment implements View.OnClickListener {

    Button addOrder, editOrder, trackOrder, finishOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.orders_fragment, container, false);
        addOrder = view.findViewById(R.id.order_add_button);
        addOrder.setOnClickListener(this);
        editOrder = view.findViewById(R.id.order_edit_button);
        editOrder.setOnClickListener(this);
        trackOrder = view.findViewById(R.id.order_search_button);
        trackOrder.setOnClickListener(this);
        finishOrder = view.findViewById(R.id.order_finish_button);
        finishOrder.setOnClickListener(this);

        List<Order> orders = MainActivity.myAppDatabase.myDao().getOrders();
        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();

        HashMap<String, Product> productHashMap = new HashMap<>();
        HashMap<Integer, Client> clientMap = new HashMap<>();

        for(Product product : products){
            productHashMap.put(product.getName(), product);
        }

        for (Client client : clients)
            clientMap.put(client.getId(), client);

        View headerView  = inflater.inflate(R.layout.order_list_item, null);
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
            View productView = inflater.inflate(R.layout.order_list_item, null);
            idTextView = productView.findViewById(R.id.order_child_id);
            clientNameTextView = productView.findViewById(R.id.order_child_client_name);
            priceTextView = productView.findViewById(R.id.order_child_total_price);
            dateTextView = productView.findViewById(R.id.order_child_date);

            idTextView.setText(String.valueOf(order.getId()));
            Client client = clientMap.get(order.getClientId());
            clientNameTextView.setText(client.getName() + " " + client.getLastname());
            String formattedPrice = String.format("%.2f", order.getTotalPrice());
            priceTextView.setText(formattedPrice + "€");
            dateTextView.setText(String.valueOf(order.getOrderDate()));

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
