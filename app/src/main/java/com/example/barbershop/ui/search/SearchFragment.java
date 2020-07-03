package com.example.barbershop.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.SalonsAdapter;
import com.example.barbershop.models.Shops;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private RecyclerView salons_rv;
    private SearchView searchView;
    SalonsAdapter s_adapter;

    private ArrayList<Shops> shopsArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        salons_rv = root.findViewById(R.id.salon_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        salons_rv.setLayoutManager(llm);
        shopsArrayList = searchViewModel.getShopList();
        s_adapter = new SalonsAdapter(requireActivity(),searchViewModel.getShopList());
        salons_rv.setAdapter(s_adapter);

        searchView = root.findViewById(R.id.search_view_salon);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query_) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                query = query.toLowerCase();

                final ArrayList<Shops> filteredModelList = new ArrayList<>();
                for (Shops model : shopsArrayList) {
                    final String text = model.getShopName().toLowerCase();
                    if (text.contains(query)) {
                        filteredModelList.add(model);
                    }
                }
                s_adapter.animateTo(filteredModelList);
                salons_rv.scrollToPosition(0);
                return true;
            }
        });


        return root;
    }


}