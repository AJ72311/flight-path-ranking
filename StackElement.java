import java.util.ArrayList;

public class StackElement {
    public City city;
    public ArrayList<City> path;

    public StackElement(City city, ArrayList<City> path) {
        this.city = city;
        this.path = path;
    }
}
