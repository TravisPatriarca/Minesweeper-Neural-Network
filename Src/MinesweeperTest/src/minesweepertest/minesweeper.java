/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minesweepertest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.*; 
import java.lang.*;
import java.io.*;
import javax.imageio.*;
import java.lang.String;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;

public class minesweeper {
    private static final int btnSize = 20;
    private static final int gridSize = 50;
    private static final int cellCount = gridSize*gridSize;
    private static final double bombCount = Math.floor((double)cellCount*0.15);
    private static final Icon t0 = new ImageIcon("images/0.png");
    private static final Icon t1 = new ImageIcon("images/1.png");
    private static final Icon t2 = new ImageIcon("images/2.png");
    private static final Icon t3 = new ImageIcon("images/3.png");
    private static final Icon t4 = new ImageIcon("images/4.png");
    private static final Icon t5 = new ImageIcon("images/5.png");
    private static final Icon t6 = new ImageIcon("images/6.png");
    private static final Icon t7 = new ImageIcon("images/7.png");
    private static final Icon t8 = new ImageIcon("images/8.png");
    private static final Icon tFlag = new ImageIcon("images/flag.png");
    private static final Icon tUnopened = new ImageIcon("images/unopened.png");
    private static final Icon tMine = new ImageIcon("images/mine.png");
    private static Cell[][] cells = new Cell[gridSize][gridSize];
    private static final boolean TRAIN_MODE = false;
    private static String cellID[] = {
        /*"000000000001",
        "000000000010",
        "000000000100",
        "000000001000",
        "000000010000",
        "000000100000",
        "000001000000",
        "000010000000",
        "000100000000",
        "001000000000",
        "010000000000",
        "100000000000"};*/

        "0,0,0,0,0,0,0,0,0,0,0,1,",
        "0,0,0,0,0,0,0,0,0,0,1,0,",
        "0,0,0,0,0,0,0,0,0,1,0,0,",
        "0,0,0,0,0,0,0,0,1,0,0,0,",
        "0,0,0,0,0,0,0,1,0,0,0,0,",
        "0,0,0,0,0,0,1,0,0,0,0,0,",
        "0,0,0,0,0,1,0,0,0,0,0,0,",
        "0,0,0,0,1,0,0,0,0,0,0,0,",
        "0,0,0,1,0,0,0,0,0,0,0,0,",
        "0,0,1,0,0,0,0,0,0,0,0,0,",
        "0,1,0,0,0,0,0,0,0,0,0,0,",
        "1,0,0,0,0,0,0,0,0,0,0,0,"};
    private static boolean reset = false;
    private static JFrame frame = new JFrame("Minesweeper");

    private class Cell extends JButton {
        public int row;
        public int col;
        public int val;
        public boolean revealed = false;
        public boolean flagged = false;

        public Cell() {
            row = -1;
            col = -1;
            val = 9;
        }

        public Cell(int x, int y) {
            setText("");
            col = x;
            row = y;
            addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {
                if (!revealed) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (flagged)
                            setIcon(tUnopened);
                        else
                            setIcon(tFlag);

                        flagged = !flagged;
                    }
                    else
                        if (SwingUtilities.isLeftMouseButton(e))
                        {
                            if (val == 0)
                                floodCell(col, row);
                            else
                                revealCell();  
                        }
                        else
                            encodeCell(col,row);
                    
                    
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
            });
        }

        public void revealCell() {
            switch (val) {
                case 0:
                    this.setIcon(t0);
                    break;
                case 1:
                    this.setIcon(t1);
                    break;
                case 2:
                    this.setIcon(t2);
                    break;
                case 3:
                    this.setIcon(t3);
                    break;
                case 4:
                    this.setIcon(t4);
                    break;
                case 5:
                    this.setIcon(t5);
                    break;
                case 6:
                    this.setIcon(t6);
                    break;
                case 7:
                    this.setIcon(t7);
                    break;
                case 8:
                    this.setIcon(t8);
                    break;
                case 9:
                    this.setIcon(tUnopened);
                    break;
                case 10:
                    this.setIcon(tFlag);
                    break;
                case 11:
                    this.setIcon(tMine);
                    resetGame();
                    startGame();
                    break;
            }

            this.revealed = true;
        }

        public void encodeCell(int cx, int cy) {
            String code = "";
            String endCode = "";
            int val = -1;
            double size = 5.0;
            int rad = (int)Math.floor(size/2);
            //System.out.println(rad);
            for (int y = cy-rad; y <= cy+rad; y++)
            {
                for (int x = cx-rad; x <= cx+rad; x++)
                {
                    if (x == cx && y == cy) {
                        if (cells[cx][cy].val == 11)
                            endCode = "1";
                        else
                            endCode = "0";
                    }
                    else
                    {
                        if ((x >= 0 && x < gridSize) && (y >= 0 && y < gridSize)) {
                            if (cells[x][y].revealed == false) {
                                if (cells[x][y].flagged)
                                    val = 9;
                                else
                                    val = 10;
                            }
                            else
                                val = cells[x][y].val;
                        }
                        else
                            val = 11;
                        
                        /*if (val != 11)
                            System.out.println("cell["+x+"]["+y+"] - " + cells[x][y].val + " | " + cellID[val]);
                        else
                            System.out.println("cell["+x+"]["+y+"] - OutBounds | " + cellID[val]);*/

                        code += cellID[val];
                    }
                }
            }
            
            //System.out.println("cell["+cx+"]["+cy+"] - " + cells[cx][cy].val + " | " + endCode);
            if (TRAIN_MODE == true)
                code += endCode;

            // convert String to char[] array
            char[] chars = code.toCharArray();
            double[] numbers = new double[chars.length];


            // iterate over char[] array using enhanced for loop
            if (TRAIN_MODE == true)
            {          
            //System.out.println(chars.length);
                if ( !code.equals("0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1") &&
                     !code.equals("0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0")) {
                    FileOutputStream out = null;

                    try (PrintWriter p = new PrintWriter(new FileOutputStream("data/data2.csv", true))) {
                        p.println(code);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            else
            {
                for (int i=0; i<chars.length; i++) {
                   if (chars[i]==',')
                        chars[i] = ' ';
                }

                String myString = String.valueOf(chars);
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

                System.out.println("Copied to clipboard\n" + code);
                //for (int i=0; i<chars.length; i++) {
                //   numbers[i] = Double.parseDouble(String.valueOf(chars[i]));
                //}
                
                //neuralNetwork.setInput(numbers);
                //neuralNetwork.calculate();
                //double[] networkOutput = neuralNetwork.getOutput();
                //System.out.println(" Output: " + networkOutput ); 
            }
        }

        public void floodCell(int cx, int cy) {
            cells[cx][cy].revealCell();

            double size = 3.0;
            int rad = (int)Math.floor(size/2);
            //System.out.println(rad);
            for (int y = cy-rad; y <= cy+rad; y++)
            {
                for (int x = cx-rad; x <= cx+rad; x++)
                {
                    if ((x >= 0 && x < gridSize) && (y >= 0 && y < gridSize)) {
                        if (cells[x][y].revealed == false) {
                            if (cells[x][y].val == 0)
                                floodCell(x,y);
                            else
                                cells[x][y].revealCell();
                        }
                    }
                }
            }
        }

        public int getBombCount() {
            int bombNum = 0;
            double size = 3.0;
            int rad = (int)Math.floor(size/2);
            for (int y = this.row-rad; y<= this.row+rad; y++)
            {
                for (int x = this.col-rad; x <= this.col+rad; x++)
                {
                    if ((x >= 0 && x < gridSize) && (y >= 0 && y < gridSize))
                        if (cells[x][y].val == 11)
                            bombNum++;
                    
                }
            }

            return bombNum;
        }
    }

    public Cell addCell(int x, int y) {
        Cell btn = new Cell(x, y);
        btn.setPreferredSize(new Dimension(btnSize, btnSize));
        btn.setFocusable(false);
        btn.setIcon(tUnopened);
        btn.setBackground(new Color(255, 155, 15));
        btn.setForeground(new Color(105, 64, 6));
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(163, 101, 11)));
        return btn;
    }

    public void resetGame() {
        frame.dispose();
        frame = new JFrame("Minesweeper");
    }

    public void startGame() {
        JPanel panel = new JPanel(new GridLayout(gridSize, gridSize));
        
        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        for (int y=0; y<gridSize; y++)
        {
            for (int x=0; x<gridSize; x++)
            {
                cells[x][y] = addCell(x, y);
                panel.add(cells[x][y]);
            }
        }
        
        int bombsLeft = (int)bombCount;
        Random rand = new Random(); 
        do {
            int rx = rand.nextInt(gridSize);
            int ry = rand.nextInt(gridSize);
            if (cells[rx][ry].val != 11)
            {
                cells[rx][ry].val = 11;
                bombsLeft--;
            }
            System.out.println("Bomb placed: [" + rx + "][" + ry + "] - " + bombsLeft);
        } while (bombsLeft > 0);

        int bombNum;
        for (int y=0; y<gridSize; y++)
        {
            for (int x=0; x<gridSize; x++)
            {
                if (cells[x][y].val != 11) {
                    
                    cells[x][y].val = cells[x][y].getBombCount();
                    cells[x][y].setIcon(tUnopened);
                }
                else
                {
                    if (TRAIN_MODE==true)
                        cells[x][y].setIcon(tMine);
                    else
                        cells[x][y].setIcon(tUnopened);
                }
                
                
                //cells[x][y].revealCell();
            }
        }
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public minesweeper() {
        int inputSize = 2; 
        int outputSize = 1; 
        DataSet ds = new DataSet(inputSize, outputSize);
        
        startGame();
    }
}
