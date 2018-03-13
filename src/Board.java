import java.util.Random;

public class Board {

    private int[][] board;

    public Board(int[][] board) {
        this.board = board;
    }


    public Board() {
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

        return board;
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
}
