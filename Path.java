import java.util.ArrayList;

public class Path {
    public ArrayList<City> path;
    public float totalCost;
    public int totalTime;

    public Path(ArrayList<City> path, float totalCost, int totalTime) {
        this.path = path;
        this.totalCost = totalCost;
        this.totalTime = totalTime;
    }
}