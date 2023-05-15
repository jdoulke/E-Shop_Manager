package com.ihu.e_shopmanager.clients;

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

        int orientation = getResources().getConfiguration().orientation;

        View view;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.clients_insert_fragment, container, false);
        else
            view = inflater.inflate(R.layout.clients_insert_landscape_fragment, container, false);


        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πελάτες");

        client_id = view.findViewById(R.id.client_id);
        client_name = view.findViewById(R.id.client_name);
        client_lastname = view.findViewById(R.id.client_lastname);
        client_phone_number = view.findViewById(R.id.client_phone_number);
        button = view.findViewById(R.id.client_fragment_add_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = -1;
            try {
                id = Integer.parseInt(client_id.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του ID. ",Toast.LENGTH_LONG).show();
                return;
            }
            String name = client_name.getText().toString();
            String lastname = client_lastname.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του ονόματος. ",Toast.LENGTH_LONG).show();
                return;
            }
            if(lastname.isEmpty()){
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του επιθέτου. ",Toast.LENGTH_LONG).show();
                return;
            }
            long phone_number = 0;
            try {
                phone_number = Long.parseLong(client_phone_number.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του τηλεφώνου. ",Toast.LENGTH_LONG).show();
                return;
            }
            try {
                if(id != -1 && !name.isEmpty() && !lastname.isEmpty() && phone_number != 0) {
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
                    Toast.makeText(getActivity(),"Ο πελάτης προστέθηκε.",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του πελάτη. ",Toast.LENGTH_LONG).show();
                }
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (Exception e) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του πελάτη. ",Toast.LENGTH_LONG).show();
            }
            client_name.setText("");
            client_id.setText("");
            client_lastname.setText("");
            client_phone_number.setText("");
        });
        return view;
    }

}
