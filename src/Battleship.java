public class Battleship {
    private AIPlayer player1;
    private AIPlayer player2;
    private int wins1 = 0;
    private int wins2 = 0;
    private int gamesPlayed;
    private int totalMatches;
    private char[] locations = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    BattleshipGUI gui = new BattleshipGUI();


    public Battleship(int totalMatches) {
        this.player1 = new AIPlayer("Pirate",false);
        this.player2 = new AIPlayer("Picnic",true);
        this.totalMatches = totalMatches;
        this.gamesPlayed = 0;
    }

    //Checks if the match is finished
    public int isComplete() {
        int p1Sunk = 0;
        int p2Sunk = 0;
        for (int i = 0; i < Constants.TOTAL_SHIPS; i++) {
            if (this.player1.getSunkShips()[i]) {
                p1Sunk++;
            }
            if (this.player2.getSunkShips()[i]) {
                p2Sunk++;
            }
        }
        if (p1Sunk == Constants.TOTAL_SHIPS) {
            return 1;
        }
        if (p2Sunk == Constants.TOTAL_SHIPS) {
            return 2;
        }
        return 0;
    }

    //Alternates turns between the 2 bots until complete
    public void play() throws InterruptedException {

        float[][] prob1;
        float[][] prob2;
        String r[];
        while (true) {
            Thread.sleep(1);
            if (gui.isPaused()) {
                continue;
            }
            long waitTime = gui.getWaitTime();

            if (player1.isEngaged()) {
                prob1 = this.player1.engagedProbMatrix;
            } else {
                prob1 = this.player1.probMatrix;
            }
            if (player2.isEngaged()) {
                prob2 = this.player2.engagedProbMatrix;
            } else {
                prob2 = this.player2.probMatrix;
            }
            Thread.sleep(waitTime);
            r = takeTurn(1);
            gui.update(this.player1.board.getBoard(), this.player1.opponentBoard, this.player2.board.getBoard(), this.player2.opponentBoard, prob1, prob2);
            gui.updateInfo(1, r);
            Thread.sleep(waitTime);
            r = takeTurn(2);
            gui.update(this.player1.board.getBoard(), this.player1.opponentBoard, this.player2.board.getBoard(), this.player2.opponentBoard, prob1, prob2);
            gui.updateInfo(2, r);
            if (isComplete() != 0) {
                Constants.picnicSpreads.add(player2.board.getSpread());
                Constants.pirateSpreads.add(player1.board.getSpread());

                String name;
                if (isComplete() == 1) {
                    this.wins1++;
                    name = this.player1.getName();
                    Constants.picnicResults.add(0);
                } else {
                    this.wins2++;
                    name = this.player2.getName();
                    Constants.picnicResults.add(1);
                }
                Constants.picnicScaleFactor.add(player2.getScaleFactor());
                gui.updateStats(this.wins1, this.wins2);
                System.out.println(name + " wins!");
                String s[] = {name + " wins!", ""};
                gui.updateInfo(isComplete(), s);
                break;
            }
        }
        this.gamesPlayed++;
        if (this.gamesPlayed < this.totalMatches) {
            nextGame();
        }

    }

    //Takes a turn for a player
    public String[] takeTurn(int player) {
        String[] r = new String[2];
        System.out.println("\n");
        AIPlayer p1;
        AIPlayer p2;
        String name;
        if (player == 1) {
            p1 = this.player1;
            p2 = this.player2;
            name = this.player1.getName();
        } else {
            p1 = this.player2;
            p2 = this.player1;
            name = this.player2.getName();
        }
        Coordinate coordinate = p1.playMove();
        System.out.println(name + " attacks : " + (coordinate.getX() + 1) + " " + this.locations[coordinate.getY()]);
        r[0] = name + " attacks : " + (coordinate.getX() + 1) + " " + this.locations[coordinate.getY()];
        Result result = p2.getResult(coordinate);
        if (result.hit) {
            if (result.shipNumber != -1) {
                System.out.println(Constants.SHIP_NAMES[result.shipNumber - 1] + " is sunk!");
                r[1] = Constants.SHIP_NAMES[result.shipNumber - 1] + " is sunk!";
            } else {
                System.out.println("It's a hit!");
                r[1] = "It's a hit!";
            }
        } else {
            System.out.println("It's a miss");
            r[1] = "It's a miss";
        }
        p1.markResult(result);
        return r;

    }

    //Starts next game
    public void nextGame() throws InterruptedException {
        this.player1 = new AIPlayer("Pirate",false);
        this.player2 = new AIPlayer("Picnic",true);
        play();
    }

}
