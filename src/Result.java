import java.util.List;

public class Result {

    boolean hit;
    int shipNumber;
    List<Coordinate> coordinates;

    public Result(boolean hit, int shipNumber,List<Coordinate> coordinates) {
        this.hit = hit;
        this.shipNumber = shipNumber;
        this.coordinates=coordinates;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public int getShipNumber() {
        return shipNumber;
    }

    public void setShipNumber(int shipNumber) {
        this.shipNumber = shipNumber;
    }
}
