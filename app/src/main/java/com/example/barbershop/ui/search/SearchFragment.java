package com.example.barbershop.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.SalonsAdapter;
import com.example.barbershop.models.Shops;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView salons_rv;
    SalonsAdapter s_adapter;

    private ArrayList<Shops> anim_shopsArrayList;
    private String TAG = "SearchFragment";
    private SearchViewModel searchViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        salons_rv = root.findViewById(R.id.salon_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        salons_rv.setLayoutManager(llm);
        anim_shopsArrayList = new ArrayList<>();
        observeShopList();

        SearchView searchView = root.findViewById(R.id.search_view_salon);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query_) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                Log.println(Log.INFO,TAG,"OnQueryTextChanged() running!");
                query = query.toLowerCase();
                if(query.length() == 0)
                {
                    //Add All Shops
                    Log.println(Log.INFO,TAG,"If part running...sizeList: " + anim_shopsArrayList.size());
                    //s_adapter.setModels(anim_shopsArrayList);
                }
                else {
                    Log.println(Log.INFO,TAG,"Else part running... size: " + anim_shopsArrayList.size());
                    ArrayList<Shops> filteredModelList = new ArrayList<>();
                    for (Shops model : anim_shopsArrayList) {
                        final String text = model.getShopName().toLowerCase();
                        if (text.contains(query)) {
                            filteredModelList.add(model);
                        }
                    }
                    s_adapter.animateTo(filteredModelList);
                    salons_rv.scrollToPosition(0);
                }
                return true;
            }
        });

        return root;
    }

    public void observeShopList() {


        MutableLiveData< ArrayList<Shops> > shops = searchViewModel.getShopList();
        shops.observe(requireActivity(), new Observer<ArrayList<Shops>>() {
            @Override
            public void onChanged(ArrayList<Shops> shops) {
                if(shops.isEmpty())
                    Log.println(Log.ERROR,TAG,"Shops list empty!");
                anim_shopsArrayList = shops;
                s_adapter = new SalonsAdapter(requireActivity(),shops);
                salons_rv.setAdapter(s_adapter);
                Log.println(Log.INFO,TAG,"Shops adapter set!");
            }
        });

    }


}



