package com.ihu.e_shopmanager.products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;

import java.util.List;

public class ProductsFragment extends Fragment implements View.OnClickListener{

    Button addProduct, removeProduct, editProduct, searchProduct;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.products_fragment, container, false);
        else
            view = inflater.inflate(R.layout.products_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Προϊόντα");

        addProduct = view.findViewById(R.id.product_add_button);
        addProduct.setOnClickListener(this);
        removeProduct = view.findViewById(R.id.product_remove_button);
        removeProduct.setOnClickListener(this);
        editProduct = view.findViewById(R.id.product_edit_button);
        editProduct.setOnClickListener(this);
        searchProduct = view.findViewById(R.id.product_search_button);
        searchProduct.setOnClickListener(this);

        LinearLayout mLinearLayout = view.findViewById(R.id.products_linearlayout);


        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();

        @SuppressLint("InflateParams") View headerView  = inflater.inflate(R.layout.product_item, null);
        TextView idTextView = headerView.findViewById(R.id.product_child_id);
        TextView nameTextView = headerView.findViewById(R.id.product_child_name);
        TextView categoryTextView = headerView.findViewById(R.id.product_child_category);
        TextView stockTextView = headerView.findViewById(R.id.product_child_stock);
        TextView priceTextView = headerView.findViewById(R.id.product_child_price);

        idTextView.setText("ID");
        nameTextView.setText("Όνομα Προϊόντος");
        categoryTextView.setText("Κατηγορία");
        stockTextView.setText("Stock");
        priceTextView.setText("Αξία");
        mLinearLayout.addView(headerView);

        for (Product product : products) {
            @SuppressLint("InflateParams") View productView = inflater.inflate(R.layout.product_item, null);
            idTextView = productView.findViewById(R.id.product_child_id);
            nameTextView = productView.findViewById(R.id.product_child_name);
            categoryTextView = productView.findViewById(R.id.product_child_category);
            stockTextView = productView.findViewById(R.id.product_child_stock);
            priceTextView = productView.findViewById(R.id.product_child_price);

            idTextView.setText(String.valueOf(product.getId()));
            nameTextView.setText(product.getName());
            categoryTextView.setText(product.getCategory());
            stockTextView.setText(String.valueOf(product.getStock()));
            priceTextView.setText(product.getPrice() + "€");

            mLinearLayout.addView(productView);
        }



        return view;

    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(40);
        if (v.getId() == R.id.product_add_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new InsertProduct()).addToBackStack(null).commit();
        else if (v.getId() == R.id.product_remove_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new DeleteProduct()).addToBackStack(null).commit();
        else if (v.getId() == R.id.product_edit_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new UpdateProduct()).addToBackStack(null).commit();
        else if (v.getId() == R.id.product_search_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchProduct()).addToBackStack(null).commit();

    }


}
