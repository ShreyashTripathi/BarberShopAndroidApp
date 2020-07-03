package com.example.barbershop.ui.search;

import androidx.lifecycle.ViewModel;

import com.example.barbershop.R;
import com.example.barbershop.models.BarberService;
import com.example.barbershop.models.Shops;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {



    public SearchViewModel() {

    }

    public ArrayList<Shops> getShopList()
    {
        ArrayList<Shops> shopsArrayList = new ArrayList<>();
        shopsArrayList.add(setShopData("Platinum Barber Shop"));
        shopsArrayList.add(setShopData("Pappu Salon"));
        shopsArrayList.add(setShopData("Kallu Salon"));
        shopsArrayList.add(setShopData("Punjabi Style Salon"));
        shopsArrayList.add(setShopData("Bihar Salon wale"));

        return shopsArrayList;
    }

    private Shops setShopData(String shopName) {
        // set the shop object data from cloud db

        Shops shop = new Shops();
        ArrayList<BarberService> services_list = new ArrayList<>();
        services_list.add(new BarberService("Cutting","100", R.drawable.location_2));
        services_list.add(new BarberService("Hair Color","300",R.drawable.location_2));
        services_list.add(new BarberService("Hair Straighten","700",R.drawable.location_2));
        services_list.add(new BarberService("Manicure","1000",R.drawable.location_2));
        services_list.add(new BarberService("Pedicure","900",R.drawable.location_2));

        shop = new Shops(shopName,"Krishna Nagar",0.0,0.0,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list,"+917905334848",2,"Unisex Salon", R.drawable.shop1);
        return shop;
    }

}