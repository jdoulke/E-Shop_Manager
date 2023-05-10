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

import java.util.List;

public class SearchProduct extends Fragment {

    EditText product_search_id;
    TextView product_search_name_view, product_search_stock_view, product_search_price_view, product_search_id_view, product_search_category_view;
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.products_search_fragment, container, false);


        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Προϊόντα");

        product_search_id = view.findViewById(R.id.product_search_id);
        product_search_id_view = view.findViewById(R.id.product_search_id_view);
        product_search_stock_view = view.findViewById(R.id.product_search_stock_view);
        product_search_name_view = view.findViewById(R.id.product_search_name_view);
        product_search_price_view = view.findViewById(R.id.product_search_price_view);
        product_search_category_view = view.findViewById(R.id.product_search_category_view);
        button = view.findViewById(R.id.product_search_button);
        button.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);

            List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();

            String searchId = product_search_id.getText().toString();


            for (Product i : products) {
                int id = i.getId();

                if (!searchId.isEmpty() && searchId.equals(String.valueOf(id))) {
                    displayProductDetails(i, view);
                    return;
                }

            }

            displayProductNotFound(view);
        });
        return view;
    }

    private void displayProductDetails(Product product, View view) {
        String name = product.getName();
        String category = product.getCategory();
        int stock = product.getStock();
        float price = product.getPrice();
        int id = product.getId();

        product_search_id.setText("");
        product_search_category_view.setText("Κατηγορία: " + category);
        product_search_stock_view.setText("Αποθέματα: " + stock);
        product_search_price_view.setText("Αξία: " + price);
        product_search_name_view.setText("Προϊόν: " + name);
        product_search_id_view.setText("Αναγνωριστικό: " + id);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getActivity(), "To προϊόν βρέθηκε", Toast.LENGTH_LONG).show();
    }

    private void displayProductNotFound(View view) {
        product_search_id.setText("");
        product_search_category_view.setText("Κατηγορία: ");
        product_search_stock_view.setText("Αποθέματα: ");
        product_search_price_view.setText("Αξία: ");
        product_search_name_view.setText("Προϊόν: ");
        product_search_id_view.setText("Αναγνωριστικό: ");

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getActivity(),"Δε βρέθηκε προϊόν",Toast.LENGTH_LONG).show();
    }

}
