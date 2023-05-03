package com.ihu.e_shopmanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.clients.DeleteClient;
import com.ihu.e_shopmanager.clients.InsertClient;
import com.ihu.e_shopmanager.clients.SearchClient;
import com.ihu.e_shopmanager.clients.UpdateClient;

import java.util.List;

public class ClientsFragment extends Fragment implements View.OnClickListener {

    Button addClient, removeClient, editClient, searchClient;
    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.clients_fragment, container, false);
        addClient = view.findViewById(R.id.client_add_button);
        addClient.setOnClickListener(this);
        removeClient = view.findViewById(R.id.client_remove_button);
        removeClient.setOnClickListener(this);
        editClient = view.findViewById(R.id.client_edit_button);
        editClient.setOnClickListener(this);
        searchClient = view.findViewById(R.id.client_search_button);
        searchClient.setOnClickListener(this);

        mScrollView = view.findViewById(R.id.clients_scrollview);
        mLinearLayout = view.findViewById(R.id.clients_linearlayout);

        // Get clients from database
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();

        View headerView  = inflater.inflate(R.layout.client_item, null);
        TextView idTextView = headerView.findViewById(R.id.client_child_id);
        TextView nameTextView = headerView.findViewById(R.id.client_child_name);
        TextView lastNameTextView = headerView.findViewById(R.id.client_child_lastname);
        TextView phoneTextView = headerView.findViewById(R.id.client_child_phone_number);
        TextView registrationTextView = headerView.findViewById(R.id.client_child_registration_date);

        idTextView.setText("ID");
        nameTextView.setText("Όνομα");
        lastNameTextView.setText("Επίθετο");
        phoneTextView.setText("Κινητό");
        registrationTextView.setText("Ημ/νία Εγγραφής");
        mLinearLayout.addView(headerView);

        // Inflate client_item.xml for each client and add them to LinearLayout
        for (Client client : clients) {
            View clientView = inflater.inflate(R.layout.client_item, null);
            idTextView = clientView.findViewById(R.id.client_child_id);
            nameTextView = clientView.findViewById(R.id.client_child_name);
            lastNameTextView = clientView.findViewById(R.id.client_child_lastname);
            phoneTextView = clientView.findViewById(R.id.client_child_phone_number);
            registrationTextView = clientView.findViewById(R.id.client_child_registration_date);

            idTextView.setText(String.valueOf(client.getId()));
            nameTextView.setText(client.getName());
            lastNameTextView.setText(client.getLastname());
            phoneTextView.setText(String.valueOf(client.getPhone_number()));
            registrationTextView.setText(client.getRegisteration_date());

            mLinearLayout.addView(clientView);
        }



        return view;

    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(40);
        if (v.getId() == R.id.client_add_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new InsertClient()).addToBackStack(null).commit();
        else if (v.getId() == R.id.client_remove_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new DeleteClient()).addToBackStack(null).commit();
        else if (v.getId() == R.id.client_edit_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new UpdateClient()).addToBackStack(null).commit();
        else if (v.getId() == R.id.client_search_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchClient()).addToBackStack(null).commit();

    }

}
