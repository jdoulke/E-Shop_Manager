package com.ihu.e_shopmanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.ihu.e_shopmanager.clients.DeleteClient;
import com.ihu.e_shopmanager.clients.InsertClient;
import com.ihu.e_shopmanager.clients.SearchClient;
import com.ihu.e_shopmanager.clients.UpdateClient;

public class OrdersFragment extends Fragment implements View.OnClickListener {

    Button addOrder, editOrder, trackOrder, finishOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.orders_fragment, container, false);
        addOrder = view.findViewById(R.id.order_add_button);
        addOrder.setOnClickListener(this);
        editOrder = view.findViewById(R.id.order_edit_button);
        editOrder.setOnClickListener(this);
        trackOrder = view.findViewById(R.id.order_search_button);
        trackOrder.setOnClickListener(this);
        finishOrder = view.findViewById(R.id.order_finish_button);
        finishOrder.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(40);
        if (v.getId() == R.id.order_add_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new InsertClient()).addToBackStack(null).commit();
        else if (v.getId() == R.id.order_edit_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new DeleteClient()).addToBackStack(null).commit();
        else if (v.getId() == R.id.order_search_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new UpdateClient()).addToBackStack(null).commit();
        else if (v.getId() == R.id.order_finish_button)
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new SearchClient()).addToBackStack(null).commit();

    }
}
