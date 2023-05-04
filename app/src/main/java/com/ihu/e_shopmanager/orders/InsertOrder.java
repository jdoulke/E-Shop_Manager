package com.ihu.e_shopmanager.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.products.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InsertOrder extends Fragment {

    private Set<String> productCategories = new HashSet<>();
    private Set<String> productFromCategory = new HashSet<>();

    private Button addProductButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_insert_fragment, container, false);

        Spinner productCategorySpinner = view.findViewById(R.id.order_category_spinner);
        Spinner productSpinner = view.findViewById(R.id.order_product_spinner);

        addProductButton = view.findViewById(R.id.order_add_product_button);

        LinearLayout orderLinearLayout = view.findViewById(R.id.order_linearlayout)

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);

        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();

        for(Product product : products){
            String category = product.getCategory();
            if(!productCategories.contains(category))
                productCategories.add(category);
        }
        registerListeners(view);
        adapter.addAll(productCategories);
        productCategorySpinner.setAdapter(adapter);
        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                List<Product> productsInCategory = MainActivity.myAppDatabase.myDao().getProductsInCategory(selectedCategory);
                productFromCategory.clear();
                for(Product product : productsInCategory){
                    String productName = product.getName();
                    if(!productFromCategory.contains(productName))
                        productFromCategory.add(productName);
                }
                productAdapter.clear();
                productAdapter.addAll(productFromCategory);
                productSpinner.setAdapter(productAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        addProductButton.setOnClickListener(v -> {
            String selectedProduct = productSpinner.getSelectedItem().toString();
            TextView productTextView = new TextView(getContext());
            productTextView.setText(selectedProduct);
            orderLinearLayout.addView(productTextView);
            for (Product product : products) {
                View productView = inflater.inflate(R.layout.product_item, null);
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
        });

        return view;
    }

    private void registerListeners(View view) {


    }


}
