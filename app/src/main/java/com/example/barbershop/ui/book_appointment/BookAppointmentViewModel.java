package com.example.barbershop.ui.book_appointment;

import androidx.lifecycle.ViewModel;

import com.example.barbershop.R;
import com.example.barbershop.models.HairStylist;

import java.util.ArrayList;

public class BookAppointmentViewModel extends ViewModel {

    public BookAppointmentViewModel(){

    }

    public ArrayList<HairStylist> getHairStylistList()
    {
        ArrayList<HairStylist> hairStylists = new ArrayList<>();
        hairStylists.add(new HairStylist(R.drawable.model2,2.5f,"7905334848","Sam Dropbox1"));
        hairStylists.add(new HairStylist(R.drawable.model2,1.5f,"7905334848","Sam Dropbox2"));
        hairStylists.add(new HairStylist(R.drawable.model2,2.0f,"7905334848","Sam Dropbox3"));
        hairStylists.add(new HairStylist(R.drawable.model2,2.3f,"7905334848","Sam Dropbox4"));
        hairStylists.add(new HairStylist(R.drawable.model2,3.0f,"7905334848","Sam Dropbox5"));
        hairStylists.add(new HairStylist(R.drawable.model2,5.0f,"7905334848","Sam Dropbox6"));
        hairStylists.add(new HairStylist(R.drawable.model2,4.0f,"7905334848","Sam Dropbox7"));
        return hairStylists;
    }

    public ArrayList<String> getAvailableTimings()
    {
        ArrayList<String> timingsAvailable = new ArrayList<>();
        timingsAvailable.add("10:00 am to 11:00 am ");
        timingsAvailable.add("11:00 am to 12:00 am ");
        timingsAvailable.add("12:00 pm to 1:00 pm ");
        timingsAvailable.add("1:00 pm to 2:00 pm ");
        timingsAvailable.add("2:00 pm to 3:00 pm ");
        timingsAvailable.add("3:00 pm to 4:00 pm ");
        timingsAvailable.add("4:00 pm to 5:00 pm ");

        return timingsAvailable;
    }
}
