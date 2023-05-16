package com.ihu.e_shopmanager.products;

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
import com.ihu.e_shopmanager.clients.Client;

public class DeleteProduct extends Fragment {


    EditText editText;
    Button button;

    public DeleteProduct() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.products_delete_fragment, container, false);


        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Προϊόντα");

        editText = view.findViewById(R.id.product_delete_id);
        button = view.findViewById(R.id.product_remove_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            int id = 0;
            try {
                id = Integer.parseInt(editText.getText().toString());
            } catch (NumberFormatException ex) {
                Toast.makeText(getActivity(),"Σφάλμα στην εισαγωγή του ID.",Toast.LENGTH_LONG).show();
                return;
            }
            Product product = new Product();
            product.setId(id);
            MainActivity.myAppDatabase.myDao().deleteProduct(product);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Toast.makeText(getActivity(),"To προϊόν αφαιρέθηκε.",Toast.LENGTH_LONG).show();
            editText.setText("");
        });
        return view;
    }


}
