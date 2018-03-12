public class Battleship {
    private AIPlayer player1;
    private AIPlayer player2;
    private int wins1=0;
    private int wins2=0;
    private int gamesPlayed;
    private int totalMatches;
    private char[] locations = {'A','B','C','D','E','F','G','H','I','J'};
    BattleshipGUI gui=new BattleshipGUI();



    public Battleship(int totalMatches) {
        this.player1 = new AIPlayer();
        this.player2 = new AIPlayer();
        this.totalMatches=totalMatches;
        this.gamesPlayed=0;
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
            return 1;
        }
        if(p2Sunk==Constants.TOTAL_SHIPS){
            return 2;
        }
        return 0;
    }

    public void play() throws InterruptedException {

        float[][] prob1;
        float[][] prob2;
        String r[];
        while(true){
            Thread.sleep(10);
            if(gui.isPaused()){
                continue;
            }
            long waitTime=gui.getWaitTime();

            if(player1.isEngaged()){
                prob1=this.player1.engagedProbMatrix;
            }
            else{
                prob1=this.player1.probMatrix;
            }
            if(player2.isEngaged()){
                prob2=this.player2.engagedProbMatrix;
            }
            else{
                prob2=this.player2.probMatrix;
            }
            Thread.sleep(waitTime);
            r=takeTurn(1);
            gui.update(this.player1.board.getBoard(),this.player1.opponentBoard,this.player2.board.getBoard(),this.player2.opponentBoard,prob1,prob2);
            gui.updateInfo(1,r);
            Thread.sleep(waitTime);
            r=takeTurn(2);
            gui.update(this.player1.board.getBoard(),this.player1.opponentBoard,this.player2.board.getBoard(),this.player2.opponentBoard,prob1,prob2);
            gui.updateInfo(2,r);
            if(isComplete()!=0){
                if(isComplete()==1){
                    this.wins1++;
                }
                else{
                    this.wins2++;
                }
                gui.updateStats(this.wins1,this.wins2);
                System.out.println("Player "+isComplete()+" wins!");
                String s[]= {"Player "+isComplete()+" wins!",""};
                gui.updateInfo(isComplete(),s);
                break;
            }
        }
        this.gamesPlayed++;
        if(this.gamesPlayed<this.totalMatches){
            nextGame();
        }

    }

    public String[] takeTurn(int player){
        String[] r= new String[2];
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
        System.out.println("Player "+player+" attacks : " +(coordinate.getX()+1)+" "+this.locations[coordinate.getY()]);
        r[0]="Player "+player+" attacks : " +(coordinate.getX()+1)+" "+this.locations[coordinate.getY()];
        Result result = p2.getResult(coordinate);
        if(result.hit){
            if(result.shipNumber!=-1){
                System.out.println(Constants.SHIP_NAMES[result.shipNumber-1]+" is sunk!");
                r[1]= Constants.SHIP_NAMES[result.shipNumber-1]+" is sunk!";
            }
            else{
                System.out.println("It's a hit!");
                r[1]= "It's a hit!";
            }
        }
        else{
            System.out.println("It's a miss");
            r[1]= "It's a miss";
        }
        p1.markResult(result);
        return r;

    }
    public void nextGame() throws InterruptedException {
        this.player1=new AIPlayer();
        this.player2=new AIPlayer();
        play();
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
