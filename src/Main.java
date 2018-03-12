import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
      AIPlayer player1 = new AIPlayer();
      AIPlayer player2 = new AIPlayer();
      Battleship battleship = new Battleship(player1,player2);
      System.out.println("Game Begins");
      System.out.println("Player 1 Board");
      for(int i=0;i<Constants.MAX_ROW;i++){
          for (int j=0;j<Constants.MAX_COL;j++){
              System.out.print(player1.board.getBoard()[i][j]+" ");
          }
          System.out.println("");
      }
        System.out.println("Player 2 Board");
        for(int i=0;i<Constants.MAX_ROW;i++){
            for (int j=0;j<Constants.MAX_COL;j++){
                System.out.print(player2.board.getBoard()[i][j]+" ");
            }
            System.out.println("");
        }
      battleship.play();


    }
}
