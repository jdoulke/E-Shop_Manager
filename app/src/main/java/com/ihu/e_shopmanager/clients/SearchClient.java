package com.ihu.e_shopmanager.clients;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;


public class SearchClient extends Fragment {

    EditText client_search_id, client_search_phone;
    TextView client_search_name_view, client_search_phone_view, client_search_date_view, client_search_id_view;
    Button button;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int orientation = getResources().getConfiguration().orientation;

        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.clients_search_fragment, container, false);
        else
            view = inflater.inflate(R.layout.clients_search_landscape_fragment, container, false);



        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πελάτες");

        client_search_id = view.findViewById(R.id.client_search_id);
        client_search_phone = view.findViewById(R.id.client_search_phone);
        client_search_date_view = view.findViewById(R.id.client_search_date_view);
        client_search_phone_view = view.findViewById(R.id.client_search_phone_view);
        client_search_name_view = view.findViewById(R.id.client_search_name_view);
        client_search_id_view = view.findViewById(R.id.client_search_id_view);
        button = view.findViewById(R.id.client_fragment_search_button);
        button.setOnClickListener((View.OnClickListener) v -> {
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);


            String searchId = client_search_id.getText().toString();
            String searchPhone = client_search_phone.getText().toString();


            if (!searchId.isEmpty()) {
                Client client = MainActivity.myAppDatabase.myDao().getClientFromId(Integer.parseInt(searchId));
                if(client != null) {
                    displayClientDetails(client, view);
                    return;
                }
            }

            if (!searchPhone.isEmpty()) {
                Client client = MainActivity.myAppDatabase.myDao().getClientFromPhone(Long.parseLong(searchPhone));
                if(client != null) {
                    displayClientDetails(client, view);
                    return;
                }
            }

            displayClientNotFound(view);
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void displayClientDetails(Client client, View view) {
        String name = client.getName();
        String lastname = client.getLastname();
        long phone = client.getPhone_number();
        String date = client.getRegisteration_date();
        int id = client.getId();

        client_search_id.setText("");
        client_search_phone.setText("");
        client_search_date_view.setText("Ημ/νία Εγγραφής: " + date);
        client_search_phone_view.setText("Κινητό: " + phone);
        client_search_name_view.setText("Πελάτης: " + name + " " + lastname);
        client_search_id_view.setText("Αναγνωριστικό: " + id);

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getActivity(), "Ο πελάτης βρέθηκε.", Toast.LENGTH_LONG).show();
    }

    private void displayClientNotFound(View view) {
        client_search_date_view.setText("Ημ/νία Εγγραφής: ");
        client_search_phone_view.setText("Κινητό: " );
        client_search_name_view.setText("Πελάτης: ");
        client_search_id_view.setText("Αναγνωριστικό: ");
        client_search_id.setText("");
        client_search_phone.setText("");

        InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getActivity(),"Δε βρέθηκε πελάτης.",Toast.LENGTH_LONG).show();
    }

}


