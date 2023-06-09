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



public class DeleteClient extends Fragment {

    EditText editText;
    Button button;

    public DeleteClient() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.clients_delete_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Πελάτες");

        editText = view.findViewById(R.id.client_delete_id);
        button = view.findViewById(R.id.client_fragment_remove_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = -1;
            try {
                if(!editText.getText().toString().equals(""))
                    id = Integer.parseInt(editText.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του ID. ",Toast.LENGTH_LONG).show();
                return;
            }
            if(id != -1) {
                Client client = new Client();
                client.setId(id);
                MainActivity.myAppDatabase.myDao().deleteClient(client);
                Toast.makeText(getActivity(),"Ο πελάτης αφαιρέθηκε.",Toast.LENGTH_LONG).show();
            }
            InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            editText.setText("");
        });
        return view;
    }

}
