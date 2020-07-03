package com.example.barbershop.ui.barber_shop;

import androidx.lifecycle.ViewModel;

import com.example.barbershop.R;
import com.example.barbershop.models.BarberService;
import com.example.barbershop.models.Shops;

import java.util.ArrayList;

public class ShopViewModel extends ViewModel {

    private ArrayList<Shops> shopsList;
    public ShopViewModel(){

        setShopData();
    }

    private void setShopData() {
       // set the shop object data from cloud db
        shopsList = new ArrayList<>();
        ArrayList<BarberService> services_list = new ArrayList<>();
        services_list.add(new BarberService("Cutting","100",R.drawable.location_2));
        services_list.add(new BarberService("Hair Color","300",R.drawable.location_2));
        services_list.add(new BarberService("Hair Straighten","700",R.drawable.location_2));
        services_list.add(new BarberService("Manicure","1000",R.drawable.location_2));
        services_list.add(new BarberService("Pedicure","900",R.drawable.location_2));

        shopsList.add(new Shops("Platinum Barber Shop","Krishna Nagar",0.0,0.0,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list,"+917905334848",2,"Unisex Salon", R.drawable.shop1));
    }

    public Shops getShopData(String shop_name) {
        Shops shop = new Shops();
        for(Shops s : shopsList)
        {
            if(s.getShopName().equals(shop_name))
            {
                shop = s;
                break;
            }
        }
        return shop;
    }


}
