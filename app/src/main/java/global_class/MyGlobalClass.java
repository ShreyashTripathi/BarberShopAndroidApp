package global_class;

import android.app.Application;
import android.util.Pair;

public class MyGlobalClass extends Application {
    private String gender;
    private Pair<Double, Double> user_location_coordinates;
    private String user_email;
    private String user_loc_string;

    public MyGlobalClass() {
    }

    public MyGlobalClass(String gender, Pair<Double, Double> coordinates, String user_email) {
        this.gender = gender;
        this.user_location_coordinates = coordinates;
        this.user_email = user_email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Pair<Double, Double> getUser_location_coordinates() {
        return user_location_coordinates;
    }

    public void setUser_location_coordinates(Pair<Double, Double> user_location_coordinates) {
        this.user_location_coordinates = user_location_coordinates;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_loc_string() {
        return user_loc_string;
    }

    public void setUser_loc_string(String user_loc_string) {
        this.user_loc_string = user_loc_string;
    }
}
