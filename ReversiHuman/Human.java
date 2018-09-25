import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.math.*;
import java.text.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MyCanvas extends JComponent {
    int width, height;
    int sqrWdth, sqrHght;
    Color gris = new Color(230,230,230);
    Color myWhite = new Color(220, 220, 220);
    
    int state[][] = new int[8][8];
    int turn = 0;
    double t1, t2;
    boolean gameOver = false;
    int theMe;
    int winner;
    
    public MyCanvas(int w, int h, int _me) {
        //System.out.println("MyCanvas");
        width = w;
        height = h;
        
        sqrWdth = (w - 60) / 8;
        sqrHght = (h - 168) / 8;
        
        theMe = _me;
    }
    
    public void gameOver() {
        gameOver = true;
        System.out.println("Game Over");
        repaint();
    }

    public void updateState(int nState[][], int nTurn, double nt1, double nt2, int nwinner) {
        int i, j;
        
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                state[i][j] = nState[i][j];
            }
        }
        
        turn = nTurn;
        winner = nwinner;
        
        //System.out.println("**********Turn: " + turn);
        
        t1 = nt1;
        t2 = nt2;
        
        //System.out.println("should repaint");
        
        repaint();
    }

    void drawAlphabetBar(Graphics g, int h) {
        g.setColor(myWhite);
        g.fillRect (6, h, width-12, 20);

        g.setFont(new Font("Courier", 1, 18));
        g.setColor(Color.black);
        
        int baseline = h + 16;
        
        g.drawString("a", 30 + (sqrWdth / 2), baseline);
        g.drawString("b", 30 + (sqrWdth / 2) + sqrWdth*1, baseline);
        g.drawString("c", 30 + (sqrWdth / 2) + sqrWdth*2, baseline);
        g.drawString("d", 30 + (sqrWdth / 2) + sqrWdth*3, baseline);
        g.drawString("e", 30 + (sqrWdth / 2) + sqrWdth*4, baseline);
        g.drawString("f", 30 + (sqrWdth / 2) + sqrWdth*5, baseline);
        g.drawString("g", 30 + (sqrWdth / 2) + sqrWdth*6, baseline);
        g.drawString("h", 30 + (sqrWdth / 2) + sqrWdth*7, baseline);
    }
    
    void drawNumberBar(Graphics g, int w) {

        g.setColor(myWhite);
        g.fillRect (w, 34, 20, sqrHght*8);

        g.setFont(new Font("Courier", 1, 18));
        g.setColor(Color.black);
        
        int baseline = w + 5;
        g.drawString("8", baseline, 34 + 6 + (sqrHght / 2));
        g.drawString("7", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*1);
        g.drawString("6", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*2);
        g.drawString("5", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*3);
        g.drawString("4", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*4);
        g.drawString("3", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*5);
        g.drawString("2", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*6);
        g.drawString("1", baseline, 34 + 6 + (sqrHght / 2) + sqrHght*7);
    }

    
    public void paint(Graphics g) {
        //System.out.println("here");
        
        Color turquois = new Color(30, 200, 200);
        Color myDarkGray = new Color(100, 100, 100);
        
        g.setColor(turquois);
        g.drawRect (5, 5, width-10, height - 110);
        
        drawAlphabetBar(g, 10);
        drawAlphabetBar(g, height - 130);
        
        drawNumberBar(g, 6);
        drawNumberBar(g, width-26);
        
        Color boardColor = new Color(30,160,30);
        g.setColor(boardColor);
        g.fillRect(30, 34, sqrWdth*8, sqrHght*8);
        
        g.setColor(myWhite);
        for (int i = 0; i < 9; i++) {
            g.drawLine(30, 34+i*sqrHght, width-30, 34+i*sqrHght);
        }

        for (int i = 0; i < 9; i++) {
            g.drawLine(30+i*sqrWdth, 34, 30+i*sqrWdth, 34+8*sqrHght);
        }
        
        int i, j;
        int x, y;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (state[i][j] == 1) {  // black
                    g.setColor(Color.black);
                    x = 30 + 6 + sqrWdth*j;
                    y = 34 + 6 + sqrHght * (7-i);
                    g.fillOval(x, y, sqrWdth-12, sqrHght-12);
                }
                else if (state[i][j] == 2) { // white
                    g.setColor(gris);
                    x = 30 + 6 + sqrWdth*j;
                    y = 34 + 6 + sqrHght * (7-i);
                    g.fillOval(x, y, sqrWdth-12, sqrHght-12);

                    //g.setColor(bkgroundColor);
                    //g.drawOval(x, y, sqrWdth-8, sqrHght-8);

                }
            }
        }
        
        int countBlack = 0, countWhite = 0;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (state[i][j] == 1)
                    countBlack ++;
                else if (state[i][j] == 2)
                    countWhite ++;
            }
        }
        
        int xanchor = 180;
        
        g.setColor(Color.black);
        String blackStr = "" + countBlack;
        
        if (theMe == 1)
            g.drawString("Black (you):", xanchor-45, height - 58);
        else
            g.drawString("Black:", xanchor-45, height - 58);
        if (countBlack < 10)
            g.drawString(blackStr, xanchor + 110, height - 58);
        else
            g.drawString(blackStr, xanchor + 104, height - 58);
        
        int min = (int)(t1 / 60);
        int sec = (int)(t1+0.5) % 60;
        System.out.println("t1 = " + t1 + "; sec = " + sec);
        int mili = (int)(t1 - (min*60 + sec)) * 100;
        String minStr, secStr, miliStr;
        if (min < 10)
            minStr = "0" + min;
        else
            minStr = "" + min;
        if (sec < 10)
            secStr = "0" + sec;
        else
            secStr = "" + sec;
        if (mili < 10)
            miliStr = "0" + mili;
        else
            miliStr = "" + mili;
        
        String t1Str = minStr + ":" + secStr;
        g.drawString(t1Str, xanchor + 180, height - 58);
        
        if (theMe == 2)
            g.drawString("White (you):", xanchor-45, height - 32);
        else
            g.drawString("White:", xanchor-45, height - 32);
        String whiteStr = "" + countWhite;
        if (countWhite < 10)
            g.drawString(whiteStr, xanchor + 110, height - 32);
        else
            g.drawString(whiteStr, xanchor + 104, height - 32);
        
        if (!gameOver) {
            if ((turn == 1) || ((turn == -1) && (theMe == 2)))
                g.fillOval(xanchor-55, height - 67, 6, 6);
            else
                g.fillOval(xanchor-55, height - 41, 6, 6);
        }
        
        min = (int)(t2 / 60);
        sec = (int)(t2+0.5) % 60;
        System.out.println("t2 = " + t2 + "; sec = " + sec);
        mili = (int)(t2 - (min*60 + sec)) * 100;
        if (min < 10)
            minStr = "0" + min;
        else
            minStr = "" + min;
        if (sec < 10)
            secStr = "0" + sec;
        else
            secStr = "" + sec;
        if (mili < 10)
            miliStr = "0" + mili;
        else
            miliStr = "" + mili;
        
        String t2Str = minStr + ":" + secStr;
        g.drawString(t2Str, xanchor + 180, height - 32);
        
        
        g.setFont(new Font("Courier", 1, 16));
        g.setColor(myDarkGray);
        g.drawString("Score", xanchor+91, height - 80);

        g.drawString("Time", xanchor+188, height - 80);
        
        if (gameOver) {
            g.setFont(new Font("Courier", 1, 40));
            g.setColor(Color.red);
        
            g.drawString("Game Over", width / 2 - 109, height / 2 - 16);
        }
        
        if (winner > 0) {
            if (winner == 1) {
                g.setColor(Color.red);
                g.drawRect(115, height - 76, 316, 24);
                g.drawRect(116, height - 75, 314, 22);
            }
            else if (winner == 2) {
                g.setColor(Color.red);
                g.drawRect(115, height - 50, 316, 24);
                g.drawRect(116, height - 49, 314, 22);
            }
        }
    }
}


class Human extends JFrame {
    Color bkgroundColor = new Color(200,160,120);
    MyCanvas canvas;
    boolean gameOver = false;

    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    int winner = -1;

    double t1, t2;
    int theMe;
    int boardState;
    int state[][] = new int[8][8];
    int turn = -1;
    int round;
    int nMouseX = -1;
    int nMouseY = -1;
    
    int validMoves[] = new int[64];
    int numValidMoves;
    
    public Human(int _me, String host) {
        final int width = 540;//620;
        final int height = 648;//728;

        theMe = _me;
    
        setSize(width,height);//400 width and 500 height
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height);
        canvas = new MyCanvas(width, height, theMe);
        getContentPane().add(canvas);
        
        setVisible(true);
        
        setTitle("Mere Human");
    
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                //System.out.println(me.getPoint().x + ", " + me.getPoint().y);
                
                int msX = me.getPoint().x - 31;
                int msY = me.getPoint().y - (34+24);
                
                //System.out.println("Relative: " + msX + ", " + msY);
                
                int ymouse = ((height - 168) - msY) / ((height - 168) / 8);
                int xmouse = msX / ((width - 60) / 8);

                if ((ymouse >= 0) && (ymouse < 8) && (xmouse >= 0) && (xmouse < 8)) {
                    nMouseX = xmouse;
                    nMouseY = ymouse;

                    //System.out.println("Coordinate: " + xmouse + ", " + ymouse);
                }
            }
        });
    
        Random generator = new Random();
        initClient(host);

        int myMove;
        
        while (true) {
            //System.out.println("Read");
            readMessage();
            
            if (gameOver)
                break;
            
            canvas.updateState(state, turn, t1, t2, winner);
            
            if (turn == theMe) {
                //System.out.println("Move");
            
                getValidMoves(round, state);
                
                //myMove = generator.nextInt(numValidMoves);        // select a move randomly
                
                // wait for a click
                nMouseX = -1;
                while (nMouseX == -1) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
                
                String sel = nMouseY + "\n" + nMouseX;
                
                //System.out.println("Selection: " + nMouseY + ", " + nMouseX);
                
                sout.println(sel);
            }
        }
        
        readFinale();
        canvas.updateState(state, -1, t1, t2, winner);
    }
    
    private void getValidMoves(int round, int state[][]) {
        int i, j;
        
        numValidMoves = 0;
        if (round < 4) {
            if (state[3][3] == 0) {
                validMoves[numValidMoves] = 3*8 + 3;
                numValidMoves ++;
            }
            if (state[3][4] == 0) {
                validMoves[numValidMoves] = 3*8 + 4;
                numValidMoves ++;
            }
            if (state[4][3] == 0) {
                validMoves[numValidMoves] = 4*8 + 3;
                numValidMoves ++;
            }
            if (state[4][4] == 0) {
                validMoves[numValidMoves] = 4*8 + 4;
                numValidMoves ++;
            }
            //System.out.println("Valid Moves:");
            //for (i = 0; i < numValidMoves; i++) {
            //    System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            //}
        }
        else {
            //System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                            //System.out.println(i + ", " + j);
                        }
                    }
                }
            }
        }
        
        
        //if (round > 3) {
        //    System.out.println("checking out");
        //    System.exit(1);
        //}
    }
    
    private boolean checkDirection(int state[][], int row, int col, int incx, int incy) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;
        
        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;
        
            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;
        
            sequence[seqLen] = state[r][c];
            seqLen++;
        }
        
        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (theMe == 1) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        return true;
                    break;
                }
            }
        }
        
        return false;
    }
    
    private boolean couldBe(int state[][], int row, int col) {
        int incx, incy;
        
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;
            
                if (checkDirection(state, row, col, incx, incy))
                    return true;
            }
        }
        
        return false;
    }
    
    public void readMessage() {
        int i, j;
        String status;
        try {
            //System.out.println("Ready to read again");
            turn = Integer.parseInt(sin.readLine());
            
            if (turn == -999) {
                gameOver = true;
                canvas.gameOver();
                return;
            }
            
            System.out.println("Turn: " + turn);
            round = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            t2 = Double.parseDouble(sin.readLine());
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    public void readFinale() {
        int i, j;
        String status;
        try {
            winner = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            t2 = Double.parseDouble(sin.readLine());
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }
    
    public void initClient(String host) {
        int portNumber = 3333+theMe;
        
        System.out.println(portNumber);
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            String info = sin.readLine();
            System.out.println(info);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        new Human(Integer.parseInt(args[1]), args[0]);
    }
    
}
