package com.ihu.e_shopmanager.clients;

import android.content.Context;
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

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;

import java.util.List;

public class SearchClient extends Fragment {

    EditText client_search_id, client_search_phone;
    TextView client_search_name_view, client_search_phone_view, client_search_date_view, client_search_id_view;
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.clients_search_fragment, container, false);


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
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();

            String searchId = client_search_id.getText().toString();
            String searchPhone = client_search_phone.getText().toString();

            for (Client i : clients) {
                int code = i.getId();
                long phone = i.getPhone_number();

                if (!searchId.isEmpty() && searchId.equals(String.valueOf(code))) {
                    displayClientDetails(i, view);
                    return;
                }

                if (!searchPhone.isEmpty() && searchPhone.equals(String.valueOf(phone))) {
                    displayClientDetails(i, view);
                    return;
                }
            }

            displayClientNotFound(view);
        });
        return view;
    }

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

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getActivity(), "Ο πελάτης βρέθηκε", Toast.LENGTH_LONG).show();
    }

    private void displayClientNotFound(View view) {
        client_search_date_view.setText("Ημ/νία Εγγραφής: ");
        client_search_phone_view.setText("Κινητό: " );
        client_search_name_view.setText("Πελάτης: ");
        client_search_id_view.setText("Αναγνωριστικό: ");
        client_search_id.setText("");
        client_search_phone.setText("");

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getActivity(),"Δε βρέθηκε πελάτης",Toast.LENGTH_LONG).show();
    }

}


