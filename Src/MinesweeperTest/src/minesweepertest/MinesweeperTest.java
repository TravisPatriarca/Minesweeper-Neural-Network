/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minesweepertest;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*; 
import java.lang.Math;

public class MinesweeperTest {
    private static void run() {
        new minesweeper();
    }

    public static void main(String[] args) {
        final int gridSize = 10;
        SwingUtilities.invokeLater(() -> run());
    }

}
