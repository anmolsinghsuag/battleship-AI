public class Main {

    public static void main(String[] args) throws InterruptedException {
        //Runs the battleship simulator for 100 matches
        Battleship battleship = new Battleship(100);
        battleship.play();
    }
}
