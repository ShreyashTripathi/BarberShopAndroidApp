package com.example.barbershop.ui.book_appointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.adapters.HairStylistAdapter;
import com.example.barbershop.models.BookingData;
import com.example.barbershop.models.HairStylist;
import com.example.barbershop.models.User;
import com.example.barbershop.ui.barber_shop.BarberShopActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class BookAppointment extends AppCompatActivity {

    private CalendarView cv;
    private TextView shop_name_tv,service_booked_tv;
    private RecyclerView available_barber_rv;
    private BookAppointmentViewModel viewModel;
    private BookingData bookingData;
    private String shop_document_id;
    private String shop_name,hairStylistName, emailOrPhone;

    LinearLayout available_timings;
    private String TAG = "BookAppointment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initialize();

        viewModel = new ViewModelProvider(this).get(BookAppointmentViewModel.class);
        Calendar calendar = Calendar.getInstance();
        cv.setDate(calendar.getTimeInMillis(),true,true);

        Calendar temp = Calendar.getInstance();
        temp.add(Calendar.DATE,7);
        cv.setMaxDate(temp.getTimeInMillis());

        Calendar temp2 = Calendar.getInstance();
        temp.add(Calendar.DATE,-7);
        cv.setMinDate(temp2.getTimeInMillis());

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(BookAppointment.this, "Selected date: " + dayOfMonth + " : " + month + " : " + year, Toast.LENGTH_SHORT).show();
                String date = dayOfMonth + " / " + month + " / " + year;
                setDateData(date);
            }

        });

        Intent intent = getIntent();
        shop_name = intent.getStringExtra("shop_name");
        shop_document_id = intent.getStringExtra("shop_document_id");
        shop_name_tv.setText(shop_name);

        String service_booked = intent.getStringExtra("service_booked");
        service_booked_tv.setText(service_booked);

        MutableLiveData<ArrayList<HairStylist>> available_barbers = viewModel.getHairStylistList(shop_name);
        available_barbers.observe(BookAppointment.this, new Observer<ArrayList<HairStylist>>() {
            @Override
            public void onChanged(ArrayList<HairStylist> hairStylists) {
                HairStylistAdapter hairStylistAdapter = new HairStylistAdapter(hairStylists, BookAppointment.this, new HairStylistAdapter.OnGetHairStylist() {
                    @Override
                    public void getHairStylist(HairStylist hairStylist) {
                        setHairStylistData(hairStylist);
                    }
                });
                available_barber_rv.setAdapter(hairStylistAdapter);

                Log.println(Log.INFO,TAG,"HairStylist for a shop set!");
            }
        });


        ArrayList<String> available_slots = viewModel.getAvailableTimings();

        for(final String timing : available_slots)
        {
            LayoutInflater vi = LayoutInflater.from(BookAppointment.this);
            View v = vi.inflate(R.layout.time_slots_card, null,false);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView time_tv = v.findViewById(R.id.time_slot);
            time_tv.setText(timing);

            CardView time_cv = v.findViewById(R.id.time_slot_card_view);
            time_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BookAppointment.this, "Time Chosen!", Toast.LENGTH_SHORT).show();
                    setTimeData(timing);
                }
            });

            available_timings.addView(v);
        }

        String sharedPrefFile = "login";
        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String login_type = mPreferences.getString("login_type", "LoggedOut");
        String user_email = mPreferences.getString("user_email","");
        String user_phone = mPreferences.getString("user_phone","");


        if(login_type.equals("OTP"))
        {
            emailOrPhone = user_phone;
        }
        else
        {
            emailOrPhone = user_email;
        }
        MutableLiveData<User> userData =  viewModel.getUserData(emailOrPhone);
        userData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUserData(user);
            }
        });

    }

    private void setUserData(User user) {
        bookingData.setUser_name(user.getName());
        bookingData.setUser_phone(user.getPhone());
        bookingData.setUser_img(user.getUser_profile_pic());
    }

    private void setHairStylistData(HairStylist hairStylist) {
        bookingData.setHairstylist_id(hairStylist.getName()+"_"+hairStylist.getShop_name());
        hairStylistName = hairStylist.getName();
    }

    private void setTimeData(String timing) {
        bookingData.setTimeSlot(timing);
    }

    private void setDateData(String date) {
        bookingData.setDate(date);
    }

    private void initialize() {
        cv = findViewById(R.id.book_appointment_calender);
        shop_name_tv = findViewById(R.id.book_appointment_shop_name);
        service_booked_tv = findViewById(R.id.service_booked);
        available_barber_rv = findViewById(R.id.available_barber_rv);
        LinearLayoutManager llm = new LinearLayoutManager(BookAppointment.this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        available_barber_rv.setLayoutManager(llm);
        bookingData = new BookingData();

        available_timings = findViewById(R.id.available_slots);
    }


    public void bookAppointment(View view) {
        Toast.makeText(this, "Appointment Booked!", Toast.LENGTH_SHORT).show();
        viewModel.setBookingData(bookingData,shop_document_id,shop_name,hairStylistName,emailOrPhone);
        Intent intent = new Intent(BookAppointment.this, BarberShopActivity.class);
        intent.putExtra("shop_name",shop_name);
        startActivity(intent);
        finish();
        //finishActivity(R.string.BOOK_APPOINTMENT);
    }
}