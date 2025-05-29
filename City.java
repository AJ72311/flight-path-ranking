import java.util.LinkedList;

public class City {
    public String name;
    public LinkedList<Destination> destinations;

    public City(String name) {
        this.name = name;
        destinations = new LinkedList<>();
    }
}
