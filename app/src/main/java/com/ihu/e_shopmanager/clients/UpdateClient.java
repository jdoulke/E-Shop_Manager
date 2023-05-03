package com.ihu.e_shopmanager.clients;

import android.content.Context;
import android.icu.util.Calendar;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.ClientsFragment;
import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.products.Product;


import java.text.SimpleDateFormat;
import java.util.Date;
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
        View view = inflater.inflate(R.layout.clients_update_fragment, container, false);
        client_id = view.findViewById(R.id.client_update_id);
        client_name = view.findViewById(R.id.client_update_name);
        client_lastname = view.findViewById(R.id.client_update_lastname);
        client_phone_number = view.findViewById(R.id.client_update_phone);
        client_date = view.findViewById(R.id.client_update_date);
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
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
                Client client = new Client();
                for (Client i : clients) {
                    int code = i.getId();
                    if (s.toString().equals(String.valueOf(code))) {
                        client = i;
                        break;
                    }
                }

                // Populate the other EditTexts with the customer details
                client_name.setText(client.getName());
                client_lastname.setText(client.getLastname());
                if(client.getPhone_number() != 0)
                    client_phone_number.setText(String.valueOf(client.getPhone_number()));
                else
                    client_phone_number.setText("");
                client_date.setText(client.getRegisteration_date());
            }
        });

        button = view.findViewById(R.id.client_update_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = 0;
            try {
                id = Integer.parseInt(client_id.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            String name, lastname;
            name = client_name.getText().toString();
            lastname = client_lastname.getText().toString();
            long phone_number = 0;
            try {
                phone_number = Long.parseLong(client_phone_number.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            String date = "";
            try {
                String dateString = client_date.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date selectedDate = dateFormat.parse(dateString);
                date = dateFormat.format(selectedDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
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


}
