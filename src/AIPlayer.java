import java.util.*;

public class AIPlayer {
    public Board board;
    public int[][] opponentBoard;

    public float[][] probMatrix;
    public float[][] engagedProbMatrix;

    private List<Coordinate> hits;
    private boolean[] sunkShips;
    private List<Integer> sinkPile;
    private Map<Integer, List<Coordinate>> engagedRows;
    private Map<Integer, List<Coordinate>> engagedCols;
    private Map<Integer,List<Coordinate>> personalBoard;
    private boolean engaged;
    private Coordinate lastHit;

    public AIPlayer() {
        this.board = new Board();
        init();
    }

    public void init() {
        //Setting opponent's board with all -1
        this.opponentBoard = new int[10][10];
        this.probMatrix = new float[10][10];
        this.engagedProbMatrix = new float[10][10];

        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                this.opponentBoard[i][j] = -1;
            }
        }

        this.personalBoard = new HashMap<>();
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                if(this.board.getBoard()[i][j]!=0){
                    if(this.personalBoard.containsKey(this.board.getBoard()[i][j])){
                        this.personalBoard.get(this.board.getBoard()[i][j]).add(new Coordinate(i,j));
                    }
                    else{
                        List<Coordinate> l = new ArrayList<>();
                        l.add(new Coordinate(i,j));
                        this.personalBoard.put(this.board.getBoard()[i][j],l);
                    }
                }

            }
        }


        this.sunkShips = new boolean[7];
        this.hits = new ArrayList<Coordinate>();
        this.sinkPile = new ArrayList<Integer>();

        this.engagedCols = new HashMap<>();
        this.engagedRows = new HashMap<>();
        this.engaged = false;
    }

    /*******************************************************************************************************/
    //Funtions to decide the move
    public Coordinate playMove() {
        System.out.println("Engaged ="+this.engaged);
        Coordinate move;
        if (engaged) {
            computeEngagedProbMatrix();
            move= chooseEngagedHit();
        } else {
            computeProbMatrix();
            move= chooseRandomHit();
        }
        this.lastHit=move;
        return move;
    }

    //Choose hit coordinate using Prob Matrix
    public Coordinate chooseRandomHit() {
        double p = Math.random();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                cumulativeProbability += this.probMatrix[i][j];
                if (p <= cumulativeProbability) {
                    return new Coordinate(i, j);
                }
            }
        }
        return new Coordinate(0, 0);
    }

    //Return coordinate based on previous hits
    public Coordinate chooseEngagedHit() {
        int maxRow=-1;
        int maxCol=-1;
        float maxProb = -1;
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                if(this.engagedProbMatrix[i][j]>maxProb){
                    maxProb = this.engagedProbMatrix[i][j];
                    maxRow = i;
                    maxCol = j;
                }
            }
        }
        return new Coordinate(maxRow,maxCol);
    }

    /*******************************************************************************************************/
    //Functions for calculating Engaged and Non-Engaged probability matrix

    //When no ship is engaged, AI hits based on the probability matrix
    public void computeProbMatrix() {
        resetProbMatrix();
        for (int i = 0; i < Constants.TOTAL_SHIPS; i++) {
            if (this.sunkShips[i]) {
                continue;
            }
            int shipLength = Constants.SHIP_SIZES[i];

            for (int m = 0; m < Constants.MAX_ROW; m++) {
                for (int n = 0; n < Constants.MAX_COL; n++) {
                    setProbs(m, n, shipLength);
                }
            }
        }
        for (int i = 0; i < Constants.TOTAL_SHIPS; i++) {
            if (this.sunkShips[i]) {
                scaleProbMatrix(i);
            }
        }
        convertToProbability(this.probMatrix);
    }

    public void computeEngagedProbMatrix() {
        resetEngagedProbMatrix();
        for (int i = 0; i < Constants.TOTAL_SHIPS; i++) {
            if (this.sunkShips[i]) {
                continue;
            }
            int shipLength = Constants.SHIP_SIZES[i];

            for (Integer row : this.engagedRows.keySet()) {
                setEngagedProb(row, shipLength, 1);
            }
            for (Integer col : this.engagedCols.keySet()) {
                setEngagedProb(col, shipLength, 0);
            }
        }
        convertToProbability(this.engagedProbMatrix);

    }


    //Scales down the area next to sunk ships
    public void scaleProbMatrix(int ship) {
        Set<Coordinate> neighbours = new HashSet<>();
        for (int m = 0; m < Constants.MAX_ROW; m++) {
            for (int n = 0; n < Constants.MAX_COL; n++) {
                if (this.opponentBoard[m][n] == ship + 1) {
                    neighbours.addAll(getNeighbours(m, n));
                }
            }
        }
        for (Coordinate c : neighbours) {
            this.probMatrix[c.getX()][c.getY()] *= 0.5;
        }

    }

    //Gets neighbors of a grid element
    public List<Coordinate> getNeighbours(int m, int n) {
        List<Coordinate> neighbours = new ArrayList<>();
        if (m + 1 < Constants.MAX_ROW && n + 1 < Constants.MAX_COL) {
            neighbours.add(new Coordinate(m + 1, n + 1));
        }
        if (m + 1 < Constants.MAX_ROW && n < Constants.MAX_COL) {
            neighbours.add(new Coordinate(m + 1, n));
        }
        if (m + 1 < Constants.MAX_ROW && n - 1 >= 0) {
            neighbours.add(new Coordinate(m + 1, n - 1));
        }
        if (m - 1 >= 0 && n - 1 >= 0) {
            neighbours.add(new Coordinate(m - 1, n - 1));
        }
        if (m - 1 >= 0 && n >= 0) {
            neighbours.add(new Coordinate(m - 1, n));
        }
        if (m - 1 >= 0 && n + 1 < Constants.MAX_COL) {
            neighbours.add(new Coordinate(m - 1, n + 1));
        }
        if (n + 1 < Constants.MAX_COL) {
            neighbours.add(new Coordinate(m, n + 1));
        }
        if (n - 1 >= 0) {
            neighbours.add(new Coordinate(m, n - 1));
        }
        return neighbours;
    }

    //Checks of a ship of length can fit horizontally or vertically from grid starting row,col
    public void setProbs(int row, int col, int length) {
        int total = 0;
        boolean rowConsistent = true;
        boolean colConsistent = true;
        for (int i = col; i < col + length; i++) {
            if (i >= Constants.MAX_COL || this.opponentBoard[row][i] != -1) {
                rowConsistent = false;
            }
        }
        for (int i = row; i < row + length; i++) {
            if (i >= Constants.MAX_COL || this.opponentBoard[i][col] != -1) {
                colConsistent = false;
            }
        }

        if (rowConsistent) {
            for (int i = col; i < col + length; i++) {
                this.probMatrix[row][i] += 1;
                total += 1;
            }
        }
        if (colConsistent && length != 1) {
            for (int i = row; i < row + length; i++) {
                this.probMatrix[i][col] += 1;
                total += 1;
            }
        }

    }

    public void setEngagedProb(int i, int length, int dir) {
        if(dir==1){
            for (int m = 0; m <= Constants.MAX_COL - length; m++) {
                int totalHits = 0;
                boolean valid = true;
                for (int n = m; n < m + length; n++) {
                    if (this.opponentBoard[i][n] >= 0) {
                        valid=false;
                    }
                    if(this.opponentBoard[i][n] == -2){
                        totalHits+=1;
                    }
                }
                if(totalHits>length){
                    valid=false;
                }
                if(valid){
                    for (int n = m; n < m + length; n++) {
                        if(this.opponentBoard[i][n]==-2){
                            continue;
                        }
                        this.engagedProbMatrix[i][n]+=totalHits;
                    }
                }
            }

        }
        else{
            for (int m = 0; m <= Constants.MAX_ROW - length; m++) {
                int totalHits = 0;
                boolean valid = true;
                for (int n = m; n < m + length; n++) {
                    if (this.opponentBoard[n][i] >= 0) {
                        valid=false;
                    }
                    if(this.opponentBoard[n][i] == -2){
                        totalHits+=1;
                    }
                }
                if(totalHits>length){
                    valid=false;
                }
                if(valid){
                    for (int n = m; n < m + length; n++) {
                        if(this.opponentBoard[n][i]==-2){
                            continue;
                        }
                        this.engagedProbMatrix[n][i]+=totalHits;
                    }
                }
            }

        }

    }

    //Resets Prob Matrix to 0
    public void resetProbMatrix() {
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                this.probMatrix[i][j] = 0;
            }
        }
    }

    public void resetEngagedProbMatrix() {
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                this.engagedProbMatrix[i][j] = 0;
            }
        }
    }


    //Converts probMatrix to probabilties
    public void convertToProbability(float[][] matrix) {
        float total = 0;
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                total += matrix[i][j];
            }
        }
        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int j = 0; j < Constants.MAX_COL; j++) {
                matrix[i][j] = matrix[i][j] / total;
            }
        }
    }

    /*******************************************************************************************************/
    //Functions to mark the result of a move

    public void markResult(Result result) {
        boolean hit = result.isHit();
        Coordinate coordinate = this.lastHit;
        int shipNumber = result.getShipNumber();

        if (!hit) {
            this.opponentBoard[coordinate.getX()][coordinate.getY()] = 0;
        } else {

            if(this.engagedRows.containsKey(coordinate.getX())){
                this.engagedRows.get(coordinate.getX()).add(coordinate);
            }
            else{
                List<Coordinate> l = new ArrayList<>();
                l.add(coordinate);
                this.engagedRows.put(coordinate.getX(),l);
            }
            if(this.engagedCols.containsKey(coordinate.getY())){
                this.engagedCols.get(coordinate.getY()).add(coordinate);
            }
            else{
                List<Coordinate> l = new ArrayList<>();
                l.add(coordinate);
                this.engagedCols.put(coordinate.getY(),l);
            }
            this.opponentBoard[coordinate.getX()][coordinate.getY()]=-2;
            this.engaged=true;
            this.hits.add(coordinate);
            if(shipNumber!=-1){
             sinkShipWithCoordinates(result.getCoordinates(),shipNumber);
            }
        }

    }

    public void sinkShip(int shipNumber){
        int length=Constants.SHIP_SIZES[shipNumber-1];
        Coordinate lastHit= this.lastHit;
        boolean inRow=false;
        boolean inCol=false;
        int rowL=this.engagedRows.get(lastHit.getX()).size();
        int colL=this.engagedCols.get(lastHit.getY()).size();
        if(this.engagedRows.get(lastHit.getX()).size()==length){
            inRow = true;
        }
        if(this.engagedCols.get(lastHit.getY()).size()==length){
            inCol = true;
        }
        if(inRow && !inCol){
            sinkShipWithCoordinates(this.engagedRows.get(lastHit.getX()),shipNumber);
        }
        else if(!inRow && inCol){
            sinkShipWithCoordinates(this.engagedCols.get(lastHit.getY()),shipNumber);
        }
        else if(inRow && inCol && length==1){
            sinkShipWithCoordinates(this.engagedCols.get(lastHit.getY()),shipNumber);
        }
        System.out.println("Row Length="+rowL+" Column Length="+colL+" length="+length+" 1: "+inRow+" 2: "+inCol);
        System.out.println("Comes here");


    }

    public void sinkShipWithCoordinates(List<Coordinate> coordinates1, int shipNumber) {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.addAll(coordinates1);
        for (Coordinate coordinate : coordinates) {
            this.opponentBoard[coordinate.getX()][coordinate.getY()] = shipNumber;
            this.engagedRows.get(coordinate.getX()).remove(coordinate);
            this.engagedCols.get(coordinate.getY()).remove(coordinate);
            this.hits.remove(coordinate);
        }

        this.sunkShips[shipNumber - 1] = true;
        //System.out.println("Hit Size:"+this.hits.size());
        if(this.hits.size()==0){
            this.engaged=false;
        }
    }

    /*******************************************************************************************************/
    //Functions to return response of opponent's hit

    public Result getResult(Coordinate coordinate){
        boolean hit=false;
        int shipNumber=-1;
        List<Coordinate> r = new ArrayList<>();
        int hitCell = this.board.getBoard()[coordinate.getX()][coordinate.getY()];
        if(hitCell!=0){
            hit=true;
            this.personalBoard.get(hitCell).remove(coordinate);
            //System.out.println("Length of "+hitCell+" becomes "+this.personalBoard.get(hitCell).size());
            if(this.personalBoard.get(hitCell).size()==0){
                shipNumber=hitCell;
                for (int i = 0; i < Constants.MAX_ROW; i++) {
                    for (int j = 0; j < Constants.MAX_COL; j++) {
                        if(this.board.getBoard()[i][j]==hitCell){
                            r.add(new Coordinate(i,j));
                        }
                    }
                }

            }
        }
        return new Result(hit,shipNumber,r);
    }
    /*******************************************************************************************************/

    //Getter Setters

    public boolean[] getSunkShips() {
        return sunkShips;
    }

    public void setSunkShips(boolean[] sunkShips) {
        this.sunkShips = sunkShips;
    }

    public boolean isEngaged() {
        return engaged;
    }

    public void setEngaged(boolean engaged) {
        this.engaged = engaged;
    }
}
