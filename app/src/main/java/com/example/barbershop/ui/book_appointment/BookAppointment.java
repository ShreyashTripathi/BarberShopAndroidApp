package com.example.barbershop.ui.book_appointment;

import android.content.Intent;
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
import com.example.barbershop.models.HairStylist;

import java.util.ArrayList;
import java.util.Calendar;

public class BookAppointment extends AppCompatActivity {

    CalendarView cv;
    Calendar calendar;
    TextView shop_name_tv,service_booked_tv;
    RecyclerView available_barber_rv;
    BookAppointmentViewModel viewModel;

    LinearLayout available_timings;
    private String TAG = "BookAppointment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        initialize();

        viewModel = new ViewModelProvider(this).get(BookAppointmentViewModel.class);
        calendar = Calendar.getInstance();
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
            }
        });

        Intent intent = getIntent();
        String shop_name = intent.getStringExtra("shop_name");
        shop_name_tv.setText(shop_name);

        String service_booked = intent.getStringExtra("service_booked");
        service_booked_tv.setText(service_booked);

        MutableLiveData<ArrayList<HairStylist>> available_barbers = viewModel.getHairStylistList(shop_name);
        available_barbers.observe(BookAppointment.this, new Observer<ArrayList<HairStylist>>() {
            @Override
            public void onChanged(ArrayList<HairStylist> hairStylists) {
                available_barber_rv.setAdapter(new HairStylistAdapter(hairStylists,BookAppointment.this));
                Log.println(Log.INFO,TAG,"HairStylist for a shop set!");
            }
        });


        ArrayList<String> available_slots = viewModel.getAvailableTimings();

        for(String timing : available_slots)
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
                }
            });

            available_timings.addView(v);
        }

    }

    private void initialize() {
        cv = findViewById(R.id.book_appointment_calender);
        shop_name_tv = findViewById(R.id.book_appointment_shop_name);
        service_booked_tv = findViewById(R.id.service_booked);
        available_barber_rv = findViewById(R.id.available_barber_rv);
        LinearLayoutManager llm = new LinearLayoutManager(BookAppointment.this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        available_barber_rv.setLayoutManager(llm);

        available_timings = findViewById(R.id.available_slots);
    }

    public void bookAppointment(View view) {
    }
}