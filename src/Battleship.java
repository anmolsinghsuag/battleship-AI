public class Battleship {
    private AIPlayer player1;
    private AIPlayer player2;
    private int moves;


    public Battleship(AIPlayer player1, AIPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.moves = 0;
    }

    public int isComplete(){
        int p1Sunk = 0;
        int p2Sunk=0;
        for(int i=0;i<Constants.TOTAL_SHIPS;i++){
            if(this.player1.getSunkShips()[i]){
                p1Sunk++;
            }
            if(this.player2.getSunkShips()[i]){
                p2Sunk++;
            }
        }
        if(p1Sunk==Constants.TOTAL_SHIPS){
            return 2;
        }
        if(p2Sunk==Constants.TOTAL_SHIPS){
            return 1;
        }
        return 0;
    }

    public void play(){
        while(true){
            if(isComplete()!=0){
                System.out.println("Player "+isComplete()+" wins!");
                break;
            }
            takeTurn(1);
            takeTurn(2);
            this.moves++;
        }
    }

    public void takeTurn(int player){
        System.out.println("\n");
        AIPlayer p1;
        AIPlayer p2;
        if(player==1){
            p1=this.player1;
            p2=this.player2;
        }
        else{
            p1=this.player2;
            p2=this.player1;
        }
        Coordinate coordinate = p1.playMove();
        System.out.println("Player "+player+" attacks : " +coordinate.getX()+" "+coordinate.getY());
        Result result = p2.getResult(coordinate);
        if(result.hit){
            if(result.shipNumber!=-1){
                System.out.println(Constants.SHIP_NAMES[result.shipNumber-1]+" is sunk!");
            }
            else{
                System.out.println("It's a hit!");
            }
        }
        else{
            System.out.println("It's a miss");
        }
        p1.markResult(result);

    }

    public AIPlayer getPlayer1() {
        return player1;
    }

    public void setPlayer1(AIPlayer player1) {
        this.player1 = player1;
    }

    public AIPlayer getPlayer2() {
        return player2;
    }

    public void setPlayer2(AIPlayer player2) {
        this.player2 = player2;
    }
}
