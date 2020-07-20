package global_class;

import android.app.Application;
import android.util.Pair;

public class MyGlobalClass extends Application {
    private String gender;
    private Pair<Double, Double> user_location_coordinates;

    public MyGlobalClass() {
    }

    public MyGlobalClass(String gender, Pair<Double,Double> coordinates) {
        this.gender = gender;
        this.user_location_coordinates = coordinates;
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
}
