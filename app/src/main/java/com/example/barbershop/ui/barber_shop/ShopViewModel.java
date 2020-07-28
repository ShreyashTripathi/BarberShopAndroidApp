package com.example.barbershop.ui.barber_shop;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.barbershop.models.Shops;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ShopViewModel extends ViewModel {

    private static final String TAG = "shop_view_model";
    private static MutableLiveData<Shops> _shop;
    public ShopViewModel(){
        _shop = new MutableLiveData<>();
    }



    public MutableLiveData<Shops> getShopData(final String shop_name) {

        Log.println(Log.INFO,TAG,"getShopData() running!");
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        String SHOPS_COLLECTION_PATH = "Shops";
        final CollectionReference shops = db.collection(SHOPS_COLLECTION_PATH);

        shops.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Shops shop = document.toObject(Shops.class);
                                if(shop.getShopName().equals(shop_name)){
                                    _shop.setValue(shop);
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return _shop;
    }

    public void removeShopFromFavourite(final String shopDocumentId, final String emailOrPhone)
    {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String USERS_COLLECTION_PATH = "Users";
        CollectionReference user = firestore.collection(USERS_COLLECTION_PATH);

        user.document(emailOrPhone).update("fav_shops", FieldValue.arrayRemove(shopDocumentId))
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                {
                    Log.println(Log.ERROR,TAG,""+task.getException());
                }
                else
                {
                    Log.println(Log.INFO,TAG,"Shop removed from fav!");
                }
            }
        });
        /*
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        User user = document.toObject(User.class);
                        if(user.getEmail().equals(emailOrPhone) || user.getPhone().equals(emailOrPhone))
                        {
                            ArrayList<String> favShops;
                            if(user.getFav_shops() != null)
                                favShops = new ArrayList<>(user.getFav_shops());
                            else
                                favShops = new ArrayList<>();
                            favShops.remove(shopName);
                            user.setFav_shops(favShops);
                            updateFavShops(user,emailOrPhone);
                            break;
                        }
                    }
                }
                else {
                    Log.println(Log.ERROR,TAG,""+task.getException());
                }
            }
        });*/
    }
    public void setShopAsFavourite(final String shopDocumentId, final String emailOrPhone)
    {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String USERS_COLLECTION_PATH = "Users";
        CollectionReference user = firestore.collection(USERS_COLLECTION_PATH);

        user.document(emailOrPhone).update("fav_shops", FieldValue.arrayUnion(shopDocumentId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful())
                        {
                            Log.println(Log.ERROR,TAG,""+task.getException());
                        }
                        else
                        {
                            Log.println(Log.INFO,TAG,"Shop removed from fav!");
                        }
                    }
                });


       /* user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        User user = document.toObject(User.class);
                        if(user.getEmail().equals(emailOrPhone) || user.getPhone().equals(emailOrPhone))
                        {
                            ArrayList<String> favShops;
                            if(user.getFav_shops() != null)
                                 favShops = new ArrayList<>(user.getFav_shops());
                            else
                                favShops = new ArrayList<>();
                            favShops.add(shopName);
                            user.setFav_shops(favShops);
                            updateFavShops(user,emailOrPhone);
                            break;
                        }
                    }
                }
                else {
                    Log.println(Log.ERROR,TAG,""+task.getException());
                }
            }
        });*/
    }

    /*private void updateFavShops(User user, String emailOrPhone) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String USERS_COLLECTION_PATH = "Users";
        CollectionReference user_ = firestore.collection(USERS_COLLECTION_PATH);

        user_.document(emailOrPhone).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.println(Log.INFO,TAG,"User data overwritten...Shop added as fav!");
                }
                else
                {
                    Log.println(Log.ERROR,TAG,""+task.getException());
                }
            }
        });
    }*/

}
