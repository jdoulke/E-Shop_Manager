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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertClient extends Fragment {


    EditText client_id, client_name, client_lastname, client_phone_number;
    Button button;
    public InsertClient() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.clients_insert_fragment, container, false);
        client_id = view.findViewById(R.id.client_id);
        client_name = view.findViewById(R.id.client_name);
        client_lastname = view.findViewById(R.id.client_lastname);
        client_phone_number = view.findViewById(R.id.client_phone_number);
        button = view.findViewById(R.id.client_fragment_add_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = 0;
            try {
                id = Integer.parseInt(client_id.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            String name = client_name.getText().toString();
            String lastname = client_lastname.getText().toString();
            long phone_number = 0;
            try {
                phone_number = Long.parseLong(client_phone_number.getText().toString());
            } catch (NumberFormatException ex) {
                System.out.println("Could not parse " + ex);
            }
            try {
                Client client = new Client();
                client.setId(id);
                client.setName(name);
                client.setLastname(lastname);
                client.setPhone_number(phone_number);
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = dateFormat.format(currentDate);
                client.setRegisteration_date(formattedDate);
                MainActivity.myAppDatabase.myDao().insertClient(client);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getActivity(),"Ο πελάτης προστέθηκε",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String message = e.getMessage();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
            client_name.setText("");
            client_id.setText("");
            client_lastname.setText("");
            client_phone_number.setText("");
        });
        return view;
    }

}
