import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;
import java.util.random;
import java.io.IOException;
import java.io.ClassNotFoundException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Math;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class Main {
    static char[][] board = new char[4][4];
    static HashSet<String> set;
    static TreeSet<Word> ans = new TreeSet<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String boardFile = "currentBoard.txt";

        BufferedReader br = new BufferedReader(new FileReader(boardFile));
        for (int i = 0; i < 4; i++) {
            String line = br.readLine();
            for (int j = 0; j < 8; j += 2) {
                board[i][j / 2] = line.charAt(j);
            }
        }
        br.close();

        String file = "wordset_hash.ser";

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

        set = (HashSet<String>) in.readObject();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int n = 1; n <= 16; n++) {
                    ans.addAll(getWordsLengthN(n, i, j));
                }
            }
        }

        PriorityQueue<Word> pq = new PriorityQueue<>(ans);
        for (Word word : ans) {
            System.out.println(" - " + word.word);
        }
        System.out.println("Found words: " + ans.size());
        System.out.println("Positions: ");
        for (Point p : pq.peek().letterPositions) {
            if (p != null) System.out.println(" - " + p);
        }

        for (int word = 0; word < ans.size(); word++) {
            BufferedImage image = ImageIO.read(new File("croppedBoard.png"));
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < pq.peek().letterPositions.length - 1; i++) {
                if (pq.peek().letterPositions[i] == null || pq.peek().letterPositions[i + 1] == null) break;
                Point p1 = getPointForLetter(pq.peek().letterPositions[i].y, pq.peek().letterPositions[i].x);
                //System.out.print(p1);
                Point p2 = getPointForLetter(pq.peek().letterPositions[i + 1].y, pq.peek().letterPositions[i + 1].x);
                //System.out.print(p2);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            Point start = getPointForLetter(pq.peek().letterPositions[0].y, pq.peek().letterPositions[0].x);
            int radius = 20;
            g2d.drawOval(start.x - radius, start.y - radius, radius * 2, radius * 2);

            ImageIO.write(image, "png", new File("Words/Word" + (word) + ".png"));
            g2d.dispose();


            pq.poll();
        }

        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon("Words/Word0.png"));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);

        final int[] index = {0};

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    index[0]++;
                    File nextFile = new File("Words/Word" + index[0] + ".png");
                    if (nextFile.exists()) {
                        label.setIcon(new ImageIcon(nextFile.getAbsolutePath()));
                        frame.pack();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    System.exit(0);
                }
            }
        });



        //ImageIO.write(image, "png", new File("/Users/tanishdasari/Words" + ((int) (Math.random() * 100)) + ".png"));
        ImageIO.read(new File("markedBoard.png")).getScaledInstance(820, 820, BufferedImage.SCALE_SMOOTH); 
    }

    public static Point getPointForLetter(int x, int y) {
        return new Point(x * 28 + (x * 184) + 184/2, y * 28 + (y * 184) + 184/2);        
    }

    public static TreeSet<Word> getWordsLengthN(int n, int x, int y) {
        TreeSet<Word> words = new TreeSet<>();
        boolean[][] visited = new boolean[4][4];
        visited[x][y] = true;

        Word currentWord = new Word(String.valueOf(board[x][y]), new Point[16]);
        currentWord.letterPositions[0] = new Point(x, y);

        floodFill(x, y, n, visited, currentWord, words);
        return words;
    }

    public static boolean isValid(int x, int y) {
        return x >= 0 && x < 4 && y >= 0 && y < 4;
    }
    
    public static void floodFill(int x, int y, int n, boolean[][] visited, Word currentWord, TreeSet<Word> words) {
        if (currentWord.word.length() == n) {
            if (set.contains(currentWord.word)) words.add(new Word(currentWord.word, currentWord.letterPositions.clone()));
            return;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                // if (dx != 0 && dy != 0) continue; stops diagonals

                int newX = x + dx;
                int newY = y + dy;

                if (isValid(newX, newY) && !visited[newX][newY]) {
                    visited[newX][newY] = true;

                    int pos = currentWord.word.length();
                    currentWord.word += board[newX][newY];
                    currentWord.letterPositions[pos] = new Point(newX, newY);

                    floodFill(newX, newY, n, visited, currentWord, words);

                    visited[newX][newY] = false;
                    currentWord.word = currentWord.word.substring(0, pos);
                }
            }
        }
    }

    static class Word implements Comparable<Word> {
        String word;
        Point[] letterPositions;

        Word(String word, Point[] letterPositions) {
            this.word = word;
            this.letterPositions = letterPositions;
        }

        @Override
        public int compareTo(Word other) {
            return this.word.length() != other.word.length() ? Integer.compare(other.word.length(), this.word.length()) : this.word.compareTo(other.word);
        }

        @Override
        public String toString() {
            return word;
        }
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
}
