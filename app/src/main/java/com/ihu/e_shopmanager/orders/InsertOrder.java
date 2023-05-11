package com.ihu.e_shopmanager.orders;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.MainActivity;
import com.ihu.e_shopmanager.R;
import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;
import com.ihu.e_shopmanager.products.ProductWithQuantity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InsertOrder extends Fragment {

    private Set<String> productCategories = new HashSet<>();
    private Set<String> productFromCategory = new HashSet<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            view = inflater.inflate(R.layout.order_insert_fragment, container, false);
        else
            view = inflater.inflate(R.layout.order_insert_landscape_fragment, container, false);

        TextView toolbarText = requireActivity().findViewById(R.id.toolbar_string);
        toolbarText.setText("Παραγγελίες");

        Spinner productCategorySpinner = view.findViewById(R.id.order_category_spinner);
        Spinner productSpinner = view.findViewById(R.id.order_product_spinner);

        Button addProductButton = view.findViewById(R.id.order_add_product_button);
        Button addOrderButton = view.findViewById(R.id.order_insert_button);

        EditText order_id = view.findViewById(R.id.order_insert_id);
        EditText client_id = view.findViewById(R.id.order_insert_client_id);
        EditText quantity = view.findViewById(R.id.product_quantity_edittext);

        TextView totalPriceView = view.findViewById(R.id.order_total_price_view);
        TextView order_client_view = view.findViewById(R.id.order_client_view);

        LinearLayout orderLinearLayout = view.findViewById(R.id.order_linearlayout);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);

        List<Product> products = MainActivity.myAppDatabase.myDao().getProducts();
        List<Client> clients = MainActivity.myAppDatabase.myDao().getClients();
        List<ProductWithQuantity> productWithQuantities = new ArrayList<>();


        HashMap<String, Product> productHashMap = new HashMap<>();
        HashMap<Integer, Client> clientMap = new HashMap<>();

        for(Product product : products){
            String category = product.getCategory();
            if(!productCategories.contains(category))
                productCategories.add(category);
            productHashMap.put(product.getName(), product);
        }

        for (Client client : clients)
            clientMap.put(client.getId(), client);

        adapter.addAll(productCategories);
        productCategorySpinner.setAdapter(adapter);

        orderLinearLayout.addView(headerViews(inflater));

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
                    order_client_view.setText("Πελάτης: ");
                } else {
                    int id = parseInt(s.toString());
                    Client client = clientMap.get(id);

                    if(client == null){
                        order_client_view.setText("Πελάτης: ");
                        return;
                    }
                    order_client_view.setText("Πελάτης: " + client.getName() + " " + client.getLastname());
                }
            }
        });
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
            Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            String selectedProduct = productSpinner.getSelectedItem().toString();
            Product product = productHashMap.get(selectedProduct);
            if(product.getStock() > parseInt(quantity.getText().toString())) {
                View productView = inflater.inflate(R.layout.order_item, null);
                TextView idView = productView.findViewById(R.id.order_child_id);
                TextView nameView = productView.findViewById(R.id.order_child_name);
                TextView categoryView = productView.findViewById(R.id.order_child_category);
                TextView quantityView = productView.findViewById(R.id.order_child_quantity);
                TextView priceView = productView.findViewById(R.id.order_child_price);
                idView.setText(String.valueOf(product.getId()));
                nameView.setText(product.getName());
                categoryView.setText(product.getCategory());
                if (quantity.getText().toString().isEmpty())
                    quantityView.setText("1");
                else
                    quantityView.setText(quantity.getText().toString());
                priceView.setText(product.getPrice() + "€");

                orderLinearLayout.addView(productView);
                ProductWithQuantity productWithQuantity = new ProductWithQuantity();
                productWithQuantity.setProduct(product);
                productWithQuantity.setQuantity(parseInt(quantity.getText().toString()));
                productWithQuantities.add(productWithQuantity);
                Toast.makeText(getActivity(), "To προϊόν προστέθηκε.", Toast.LENGTH_LONG).show();
                float totalPrice = 0;
                for (ProductWithQuantity product1 : productWithQuantities) {
                    totalPrice += product1.getQuantity() * product1.getProduct().getPrice();
                }
                String formattedPrice = String.format("%.2f", totalPrice);
                totalPriceView.setText("Σύνολο: " + formattedPrice + "€");
                product.setStock(product.getStock() - parseInt(quantity.getText().toString()));
                MainActivity.myAppDatabase.myDao().updateProduct(product);
            }else {
                Toast.makeText(getActivity(), "Δεν υπάρχουν αποθέματα για αυτό το προϊόν.", Toast.LENGTH_LONG).show();
            }
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            quantity.setText("");
        });

        addOrderButton.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            Order order = new Order();
            int orderId = parseInt(order_id.getText().toString());
            int clientId = parseInt(client_id.getText().toString());
            float totalPrice = 0;
            for (ProductWithQuantity product1 : productWithQuantities){
                totalPrice += product1.getQuantity() * product1.getProduct().getPrice();
            }
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(currentDate);
            try {
                order.setOrderDate(formattedDate);
                order.setClientId(clientId);
                order.setId(orderId);
                order.setTotalPrice(totalPrice);
                order.setProducts(productWithQuantities);
                MainActivity.myAppDatabase.myDao().insertOrder(order);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getActivity(),"Η παραγγελία δημιουργήθηκε.",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String message = e.getMessage();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
            order_id.setText("");
            client_id.setText("");
            quantity.setText("");
            order_client_view.setText("Πελάτης: ");
            totalPriceView.setText("Σύνολο: 0€");
            orderLinearLayout.removeAllViews();
            orderLinearLayout.addView(headerViews(inflater));
        });

        return view;
    }

    private View headerViews(LayoutInflater inflater) {

        View headerView = inflater.inflate(R.layout.order_item, null);
        TextView idTextView = headerView.findViewById(R.id.order_child_id);
        TextView nameTextView = headerView.findViewById(R.id.order_child_name);
        TextView categoryTextView = headerView.findViewById(R.id.order_child_category);
        TextView quantityTextView = headerView.findViewById(R.id.order_child_quantity);
        TextView priceTextView = headerView.findViewById(R.id.order_child_price);
        idTextView.setText("ID");
        nameTextView.setText("Όνομα Προϊόντος");
        categoryTextView.setText("Κατηγορία");
        quantityTextView.setText("Τμχ");
        priceTextView.setText("Αξία");

        return headerView;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            System.out.println("Could not parse " + ex);
            return 0;
        }
    }


}
