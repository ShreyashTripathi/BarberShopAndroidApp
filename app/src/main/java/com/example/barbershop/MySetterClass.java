package com.example.barbershop;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.barbershop.models.BarberService;
import com.example.barbershop.models.Shops;
import com.example.barbershop.models.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MySetterClass {

    private static final String TAG = "MySetterClass";
    private static String FEMALE_STYLIST_URL = "https://image.freepik.com/free-photo/front-view-young-female-hairdresser-white-t-shirt-black-cape-holding-spray-hairbrush-black-sterile-mask-black-gloves_140725-15397.jpg";
    private static String MALE_STYLIST_URL = "https://image.freepik.com/free-photo/young-man-isolated-white-with-hairdresser-barber-dress-pointing-side_1368-62539.jpg";

    public static void setShopData() {
        // set the shop object data from cloud
        ArrayList<BarberService> services_list1 = new ArrayList<>();
        services_list1.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list1.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list1.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list1.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list1.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop1 = new Shops("SelenaGomez Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list1,"+917905334848",2,"Female Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop1);

        ArrayList<BarberService> services_list2 = new ArrayList<>();
        services_list2.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list2.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list2.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list2.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list2.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop2 = new Shops("Camila Barber Shop","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list2,"+917905334848",2,"Female Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop2);

        ArrayList<BarberService> services_list3 = new ArrayList<>();
        services_list3.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list3.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list3.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list3.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list3.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop3 = new Shops("KatyPerry Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list3,"+917905334848",2,"Female Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop3);

        ArrayList<BarberService> services_list4 = new ArrayList<>();
        services_list4.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list4.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list4.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list4.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list4.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop4 = new Shops("Eminem Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list4,"+917905334848",2,"Male Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop4);

        ArrayList<BarberService> services_list5 = new ArrayList<>();
        services_list5.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list5.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list5.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list5.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list5.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop5 = new Shops("DIVINE Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list5,"+917905334848",2,"Male Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop5);

        ArrayList<BarberService> services_list6 = new ArrayList<>();
        services_list6.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list6.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list6.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list6.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list6.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop6 = new Shops("Bazzi Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list6,"+917905334848",2,"Male Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop6);

        ArrayList<BarberService> services_list7 = new ArrayList<>();
        services_list7.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list7.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list7.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list7.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list7.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop7 = new Shops("Justin Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list7,"+917905334848",2,"Unisex Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop7);

        ArrayList<BarberService> services_list8 = new ArrayList<>();
        services_list8.add(new BarberService("Cutting","100", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list8.add(new BarberService("Hair Color","300","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list8.add(new BarberService("Hair Straighten","700","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list8.add(new BarberService("Manicure","1000","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        services_list8.add(new BarberService("Pedicure","900","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTxA5oeX2jSh9tMQlkJ_v9JMlFnlNBLRbUdkA&usqp=CAU"));
        Shops shop8 = new Shops("BTS Shop Model","Kanpur",26.4163461,80.3763674,"Good population in human society is the basic principle for peace,prosperity and spiritual progress in life. The varriisrama religion's principles were so designed that the good population would prevail in society.",3.5f,services_list8,"+917905334848",2,"Unisex Salon", "https://image.freepik.com/free-photo/brunette-woman-getting-her-hair-cut_23-2148108792.jpg");
        addShop(shop8);


    }
    private static void addShop(final Shops shop) {
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        String SHOPS_COLLECTION_PATH = "Shops";
        final CollectionReference shops = db.collection(SHOPS_COLLECTION_PATH);

        Log.println(Log.INFO,TAG,"addShop function running....");
        final String SHOP_NAME_FIELD = "shopName";
        shops.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean flag = false;
                    for(DocumentSnapshot snapshot :task.getResult().getDocuments())
                    {
                        if(snapshot.get(SHOP_NAME_FIELD).equals(shop.getShopName()))
                        {
                            Log.println(Log.INFO,TAG,"Shop already exists of same name!");
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                    {
                        shops.document(shop.getShopName()+"_"+shop.getPhone()).set(shop)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Log.println(Log.INFO,TAG,"Shop Data added....");
                                        }
                                        else
                                        {
                                            Log.println(Log.ERROR,TAG,"Error: " + task.getException());
                                        }
                                    }
                                });

                        /*shops.add(shop).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.println(Log.INFO,TAG,"Shop Data added....");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(SignUpActivity.this, "User not added, error: " + e, Toast.LENGTH_LONG).show();
                                Log.println(Log.ERROR,TAG,"Error: " + e);
                            }
                        });*/
                    }

                }
                else{
                    Log.println(Log.ERROR,TAG,"Error:145: " + task.getException());
                }
            }
        });
    }

    public static void setWorkerData()
    {
        Worker worker1 = new Worker(MALE_STYLIST_URL,2.5f,"+917905334848","Eminem","DIVINE Shop Model","Male", "Male Salon");
        addWorker(worker1);

        Worker worker2 = new Worker(MALE_STYLIST_URL,2.5f,"+917905334848","DIVINE","Eminem Shop Model","Male", "Male Salon");
        addWorker(worker2);

        Worker worker3 = new Worker(MALE_STYLIST_URL,2.5f,"+917905334848","Bazzi","Bazzi Shop Model","Male", "Male Salon");
        addWorker(worker3);

        Worker worker4 = new Worker(FEMALE_STYLIST_URL,2.5f,"+917905334848","Camila","SelenaGomez Shop Model","Female", "Female Salon");
        addWorker(worker4);

        Worker worker5 = new Worker(FEMALE_STYLIST_URL,2.5f,"+917905334848","Selena Gomez","Camila Barber Shop","Female", "Female Salon");
        addWorker(worker5);

        Worker worker6 = new Worker(FEMALE_STYLIST_URL,2.5f,"+917905334848","Katy Perry","KatyPerry Shop Model","Female", "Female Salon");
        addWorker(worker6);

        Worker worker7 = new Worker(MALE_STYLIST_URL,2.5f,"+917905334848","BTS","Justin Shop Model","Male", "Unisex Salon");
        addWorker(worker7);

        Worker worker8 = new Worker(FEMALE_STYLIST_URL,2.5f,"+917905334848","Justin","BTS Shop Model","Female", "Unisex Salon");
        addWorker(worker8);

    }

    public static void addWorker(final Worker worker)
    {
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        String WORKERS_COLLECTION_PATH = "Workers";
        final CollectionReference workers = db.collection(WORKERS_COLLECTION_PATH);
        Log.println(Log.INFO,TAG,"addWorkers function running....");
        final String WORKER_NAME_FIELD = "name";
        final String WORKER_SHOP_FIELD  = "shop_name";

        workers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    boolean flag = false;
                    for(DocumentSnapshot snapshot :task.getResult().getDocuments())
                    {
                        if(snapshot.get(WORKER_NAME_FIELD).equals(worker.getName()) &&  snapshot.get(WORKER_SHOP_FIELD).equals(worker.getShop_name()))
                        {
                            Log.println(Log.INFO,TAG,"Worker of same name already exists in same shop!");
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                    {
                        workers.document(worker.getName()+"_"+ worker.getShop_name())
                                .set(worker)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Log.println(Log.INFO,TAG,"Worker Data added....");
                                        }
                                        else
                                        {
                                            Log.println(Log.ERROR,TAG,"Error: " + task.getException());
                                        }
                                    }
                                });

                    }

                }
                else{
                    Log.println(Log.ERROR,TAG,"Error:200: " + task.getException());
                }
            }
        });

    }



}
