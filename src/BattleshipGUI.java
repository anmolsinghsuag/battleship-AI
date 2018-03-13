import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.CanvasPeer;

public class BattleshipGUI {
    private int width;
    private JFrame frame=new JFrame();
    private JButton[][] player1;
    private JPanel player1Panel;
    private JPanel player2Panel;
    private HeatMap player1Prob;
    private HeatMap player2Prob;
    private JButton[][] player2;
    private JPanel mainPanel = new JPanel();
    private JPanel infoPanel;
    private JButton player1Label;
    private JButton player2Label;
    private JLabel player1Move;
    private JLabel player2Move;
    private JButton pause;
    private JButton speed;
    private JLabel player1wins;
    private JLabel player2wins;
    private boolean paused;
    private long waitTime;
    private ImageIcon shipIcon;
    private ImageIcon cross;
    private ImageIcon start;





    public BattleshipGUI(){
        player1 = new JButton[10][10];
        player2 = new JButton[10][10];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.white);
        this.paused=false;
        this.waitTime=1000;
        this.shipIcon=new  ImageIcon(getClass().getResource("steering.png"));
        this.cross=new  ImageIcon(getClass().getResource("cancel.png"));
        this.start = new ImageIcon(getClass().getResource("power-button.png"));

        initInfoPanel();
        initBoards();
        initProbBoards();
        init();

    }

    public void initBoards(){
        TitledBorder border1 = new TitledBorder("AI Bot 1");
        border1.setTitleJustification(TitledBorder.CENTER);
        border1.setTitlePosition(TitledBorder.TOP);
        TitledBorder border2 = new TitledBorder("AI Bot 2");
        border2.setTitleJustification(TitledBorder.CENTER);
        border2.setTitlePosition(TitledBorder.TOP);
        this.player1Panel = new JPanel();
        this.player1Panel.setBorder(border1);
        this.player1Panel.setLayout(new GridLayout(10, 10));
        this.player1Panel.setSize(new Dimension(300, 300));
        this.player1Panel.setMinimumSize(new Dimension(300, 300));
        this.player1Panel.setMaximumSize(new Dimension(300, 300));
        this.player2Panel = new JPanel();
        this.player2Panel.setBorder(border2);
        this.player2Panel.setLayout(new GridLayout(10, 10));
        this.player2Panel.setSize(new Dimension(300, 300));
        this.player2Panel.setMinimumSize(new Dimension(300, 300));
        this.player2Panel.setMaximumSize(new Dimension(300, 300));
        this.player1Panel.setBackground(Color.white);
        this.player2Panel.setBackground(Color.white);

        for (int i=0; i<Constants.MAX_ROW; i++) {
            for (int j=0; j<Constants.MAX_COL; j++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(30, 30));
                btn.setSize(new Dimension(30, 30));
                btn.setBorder(new LineBorder(Color.WHITE,1));
                btn.setBorderPainted(true);
                btn.setBackground(new Color(64,163,245));
                this.player1Panel.add(btn);
                btn.setOpaque(true);
                this.player1[i][j]=btn;

                JButton btn2 = new JButton();
                btn2.setPreferredSize(new Dimension(30, 30));
                btn2.setSize(new Dimension(30, 30));
                btn2.setBorder(new LineBorder(Color.WHITE,1));
                btn2.setBorderPainted(true);
                btn2.setBackground(new Color(64,163,245));
                this.player2Panel.add(btn2);
                btn2.setOpaque(true);
                this.player2[i][j]=btn2;
            }
        }

    }

    public void initProbBoards(){
        TitledBorder border1 = new TitledBorder("Player 1 Probability Map");
        border1.setTitleJustification(TitledBorder.CENTER);
        border1.setTitlePosition(TitledBorder.TOP);
        TitledBorder border2 = new TitledBorder("Player 2 Probability Map");
        border2.setTitleJustification(TitledBorder.CENTER);
        border2.setTitlePosition(TitledBorder.TOP);
        float data[][] = new float[10][10];
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                data[i][j]= 0;
            }
        }
        Color [] colors=new Color[]{new Color(254,232,200),new Color(253,187,132),new Color(251,176,33),new Color(246,136,56),new Color(238,62,50)};
        this.player1Prob = new HeatMap(data,false,colors);
        this.player1Prob.setBorder(border1);
        this.player2Prob = new HeatMap(data,false,colors);
        this.player2Prob.setBorder(border2);
    }

    public void initInfoPanel(){
        this.infoPanel = new JPanel();
        this.infoPanel.setBackground(Color.WHITE);
        this.infoPanel.setLayout(new GridLayout(2, 4));
        this.player1Label = new JButton("Player 1");
        this.player1Label.setOpaque(true);
        this.infoPanel.add(this.player1Label);
        this.player1Move = new JLabel("",SwingConstants.CENTER);
        this.infoPanel.add(this.player1Move);
        this.player1wins = new JLabel("Wins: 0",SwingConstants.CENTER);
        this.infoPanel.add(this.player1wins);
        this.pause = new JButton("Pause");
        this.pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePaused();
            }
        } );
        this.infoPanel.add(this.pause);
        togglePaused();


        this.player2Move = new JLabel("",SwingConstants.CENTER);
        this.player2Label = new JButton("Player 2");
        this.player2Label.setOpaque(true);
        this.infoPanel.add(this.player2Label);
        this.infoPanel.add(this.player2Move);
        this.player2wins = new JLabel("Wins: 0",SwingConstants.CENTER);
        this.infoPanel.add(this.player2wins);
        this.speed = new JButton("Speed Up");
        this.speed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleSpeed();
            }
        } );

        this.speed.setBackground(Color.lightGray);
        this.infoPanel.add(this.speed);
        this.infoPanel.setPreferredSize(new Dimension(600, 150));
    }

    public void init(){
        addComponent(this.player1Panel,0,0,2,2,GridBagConstraints.CENTER,GridBagConstraints.BOTH,1.0);
        addComponent(this.player1Prob,2,0,2,2,GridBagConstraints.CENTER,GridBagConstraints.BOTH,1.0);
        addComponent(this.infoPanel,0,2,4,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,0.5);
        addComponent(this.player2Panel,0,3,2,2,GridBagConstraints.CENTER,GridBagConstraints.BOTH,1.0);
        addComponent(this.player2Prob,2,3,2,2,GridBagConstraints.CENTER,GridBagConstraints.BOTH,1.0);
        frame.setContentPane(mainPanel);
        frame.setSize(600,750);
        frame.setMinimumSize(new Dimension(300,300));
        frame.setVisible(true);
    }

    public void addComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill,double weighty) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, weighty,
                anchor, fill, new Insets(0,0,0,0), 0, 0);
        this.mainPanel.add(component, gbc);
    }

    public void update(int[][]player1board,int[][] player1Opp,int[][] player2Board,int[][] player2Opp,float[][]prob1,float[][]prob2){
        for(int i=0;i<Constants.MAX_ROW;i++){
            for(int j=0;j<Constants.MAX_COL;j++){
                this.player1[i][j].setIcon(null);
                this.player2[i][j].setIcon(null);
                if(player1board[i][j]==0){
                    this.player1[i][j].setBackground(new Color(64,163,245));
                }
                if(player1board[i][j]>0){
                    this.player1[i][j].setBackground(new Color(230,230,237));
                }
                if(player2Opp[i][j]==0){
                    this.player1[i][j].setBackground(new Color(255,96,96)); //red
                    this.player1[i][j].setIcon(this.cross);

                }
                if(player2Opp[i][j]>0){
                    this.player1[i][j].setBackground(new Color(32,183,150));
                    this.player1[i][j].setIcon(this.shipIcon);
                }
                if(player2Opp[i][j]==-2){
                    this.player1[i][j].setBackground(new Color(255,193,7));
                }

                if(player2Board[i][j]==0){
                    this.player2[i][j].setBackground(new Color(64,163,245));
                }
                if(player2Board[i][j]>0){
                    this.player2[i][j].setBackground(new Color(230,230,237));
                }
                if(player1Opp[i][j]==0){
                   this.player2[i][j].setBackground(new Color(255,96,96)); //red
                    this.player2[i][j].setIcon(this.cross);
                }
                if(player1Opp[i][j]>0){
                   this.player2[i][j].setBackground(new Color(32,183,150));
                    this.player2[i][j].setIcon(this.shipIcon);
                }
                if(player1Opp[i][j]==-2){
                    this.player2[i][j].setBackground(new Color(255,193,7));
                }
            }
        }
        this.player1Prob.updateData(prob1,false);
        this.player2Prob.updateData(prob2,false);
        frame.repaint();

    }

    public void updateInfo(int player,String[] s){
        if(player==1){
            this.player2Label.setBackground(Color.lightGray);
            this.player1Label.setBackground(Color.green);
            this.player1Move.setText(s[0]);
            this.player2Move.setText(s[1]);
        }
        else{
            this.player1Label.setBackground(Color.lightGray);
            this.player2Label.setBackground(Color.green);
            this.player2Move.setText(s[0]);
            this.player1Move.setText(s[1]);

        }
    }
    public void togglePaused(){
        if(this.paused){
            this.paused=false;
            this.pause.setText("Pause");
            this.pause.setBackground(Color.lightGray);
        }
        else{
            this.paused=true;
            this.pause.setText("Start");
            this.pause.setBackground(Color.green);

        }
        this.pause.setOpaque(true);
    }

    public void toggleSpeed(){
        if(this.waitTime==50){
            this.waitTime=1000;
            this.speed.setText("Speed Up");
            this.speed.setBackground(Color.lightGray);
        }
        else{
            this.waitTime=50;
            this.speed.setText("Speed Down");
            this.speed.setBackground(Color.green);
        }
        this.speed.setOpaque(true);
    }

    public void updateStats(int p1,int p2){
        this.player1wins.setText("Wins : "+p1);
        this.player2wins.setText("Wins : "+p2);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}