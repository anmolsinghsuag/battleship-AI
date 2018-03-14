import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private boolean learning;
    private double spread;

    private int[][] board;

    public Board(int[][] board) {
        this.board = board;
    }


    public Board(boolean learning) {
        this.learning=learning;
        this.board = getRandomBoard();
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    //Functions for Random Board allotment
    public int[][] getRandomBoard() {
        //Todo: Get matrix of common human shots and create an arrangement that is most likely to outdo human guesses
        int board[][] = new int[10][10];

        //Filling Water
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = 0;
            }
        }

        int shipValue = 1;

        for (int i = 0; i < Constants.SHIPS.length; i++) {
            String ship = Constants.SHIPS[i];
            int shipLength = Constants.SHIP_SIZE[i];
            int shipCount = Constants.SHIP_COUNT[i];

            for (int j = 0; j < shipCount; j++) {
                placeShip(board, shipValue, shipLength);
                shipValue++;
            }
        }

        double spread = calculateSpread(board);
        double targetSpread=0.0;
        if(this.learning){
            targetSpread=getPicnicSpread();
            this.spread = targetSpread;
        }
        else{
            targetSpread=Constants.pirateSpread;
            this.spread = targetSpread;
        }
        if(spread<targetSpread){
            return getRandomBoard();
        }
        else{
            return board;
        }


    }

    public void placeShip(int[][] board, int shipValue, int shipLength) {
        Random rand = new Random();
        while (true) {
            int col = rand.nextInt(10);
            int row = rand.nextInt(10);
            int direction = rand.nextInt(4);
            if (isValid(board, shipLength, col, row, direction)) {
                switch (direction) {
                    case 0:
                        for (int i = col; i < col + shipLength; i++) {
                            board[row][i] = shipValue;
                        }
                        break;
                    case 1:
                        for (int i = col; i > col - shipLength; i--) {
                            board[row][i] = shipValue;
                        }
                        break;
                    case 2:
                        for (int i = row; i < row + shipLength; i++) {
                            board[i][col] = shipValue;
                        }
                        break;
                    case 3:
                        for (int i = row; i > row - shipLength; i--) {
                            board[i][col] = shipValue;
                        }
                        break;
                }
                break;
            }
        }
    }


    public boolean isValid(int[][] board, int shipLength, int col, int row, int dir) {

        switch (dir) {
            case 0:
                for (int i = col; i < col + shipLength; i++) {
                    if (i > 9 || board[row][i] != 0) {
                        return false;
                    }
                }
                break;
            case 1:
                for (int i = col; i > col - shipLength; i--) {
                    if (i < 0 || board[row][i] != 0) {
                        return false;
                    }
                }
                break;
            case 2:
                for (int i = row; i < row + shipLength; i++) {
                    if (i > 9 || board[i][col] != 0) {
                        return false;
                    }
                }
                break;
            case 3:
                for (int i = row; i > row - shipLength; i--) {
                    if (i < 0 || board[i][col] != 0) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    //Minimum Spanning Spread from centres of all Placed ships
    public double calculateSpread(int[][] grid){
        double spread =100.0;
        List<Coordinate> centres = new ArrayList<>();
        for(int s=1;s<8;s++){
            int shipFound =0;
            int centre = Constants.SHIP_SIZES[s-1]/2;
            int stop=0;
            for(int i=0;i<Constants.MAX_ROW;i++){
                for(int j=0;j<Constants.MAX_COL;j++){
                    if(grid[i][j]==s){
                        shipFound++;
                        if(shipFound>=centre){
                            centres.add(new Coordinate(i,j));
                            stop=1;
                            break;
                        }
                    }
                }
                if(stop==1){
                    break;
                }
            }
        }

        for(int i=0;i<7;i++){
            double sp=0.0;
            for(int j=0;j<7;j++){
                if(i==j){
                    continue;
                }
                double distance = Math.abs(centres.get(i).getX()-centres.get(j).getX())+ Math.abs(centres.get(i).getY()-centres.get(j).getY());
                sp+=distance;
            }
            if(sp<spread){
                spread=sp;
            }

        }
        spread=spread/7;
        return spread;
    }

    public double getPicnicSpread(){
        if(Constants.picnicResults.size()<25){
            Random rand = new Random();
            return rand.nextInt(6);
        }
        else{
            double totalSpread =0.0;
            for(int i=0;i<Constants.picnicResults.size();i++){
                if(Constants.picnicResults.get(i)==1){
                    totalSpread+=Constants.picnicSpreads.get(i);
                }
            }
            return totalSpread/Constants.picnicResults.size();
        }
    }

    public double getSpread() {
       return this.spread;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }
}
