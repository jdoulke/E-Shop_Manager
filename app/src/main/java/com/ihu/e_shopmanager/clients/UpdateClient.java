package com.ihu.e_shopmanager.clients;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class UpdateClient extends Fragment {

    EditText client_id, client_name, client_lastname, client_phone_number, client_date;
    Button button;

    public UpdateClient() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.clients_update_fragment, container, false);
        else
            view = inflater.inflate(R.layout.clients_update_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πελάτες");

        client_id = view.findViewById(R.id.client_update_id);
        client_name = view.findViewById(R.id.client_update_name);
        client_lastname = view.findViewById(R.id.client_update_lastname);
        client_phone_number = view.findViewById(R.id.client_update_phone);
        client_date = view.findViewById(R.id.client_update_date);

        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        HashMap<Integer, Client> clientMap = new HashMap<>();
        for (Client client : clients)
            clientMap.put(client.getId(), client);

        client_id.addTextChangedListener(new TextWatcher() {
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

                    client_name.setText("");
                    client_lastname.setText("");
                    client_phone_number.setText("");
                    client_date.setText("");

                } else {
                    int id = parseInt(s.toString());
                    Client client = clientMap.get(id);

                    if(client == null){
                        client_name.setText("");
                        client_lastname.setText("");
                        client_phone_number.setText("");
                        client_date.setText("");
                        return;
                    }

                    client_name.setText(client.getName());
                    client_lastname.setText(client.getLastname());
                    if (client.getPhone_number() != 0)
                        client_phone_number.setText(String.valueOf(client.getPhone_number()));
                    else
                        client_phone_number.setText("");
                    client_date.setText(client.getRegisteration_date());
                }
            }
        });

        button = view.findViewById(R.id.client_update_button);
        button.setOnClickListener(v -> {

            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            String name = client_name.getText().toString();
            String lastname = client_lastname.getText().toString();
            int id = parseInt(client_id.getText().toString());
            long phone_number = parseLong(client_phone_number.getText().toString());
            String date = formatDate(client_date.getText().toString());

            try {
                Client client = new Client();
                client.setId(id);
                client.setName(name);
                client.setLastname(lastname);
                client.setPhone_number(phone_number);
                client.setRegisteration_date(date);
                MainActivity.myAppDatabase.myDao().updateClient(client);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getActivity(),"Τα στοιχεία ενημερώθηκαν",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String message = e.getMessage();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
            client_id.setText("");
            client_name.setText("");
            client_lastname.setText("");
            client_phone_number.setText("");
            client_date.setText("");
        });
        return view;
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
