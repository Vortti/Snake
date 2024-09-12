import javax.swing.*;  
import java.awt.*;
import java.util.Random;   
import java.awt.event.*;
import java.util.ArrayList;

public class Snake {

    public static void main(String[] args) {
        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Snake");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);  
        frame.pack(); 
        frame.setVisible(true);
        snakeGame.requestFocus();
    }
}

class SnakeGame extends JPanel implements ActionListener, KeyListener {
    
    private class Tile {
        int x;
        int y;
        
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;
    
    Timer gameLoop;  
    int velocityX;
    int velocityY;   

    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();

        placeFood();

        velocityX = 0;
        velocityY = 1;

        gameLoop = new Timer(100, this);  
        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Siatka
        for(int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Food
        g.setColor(Color.red);
        g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        // Snake head + color
        g.setColor(Color.green);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // Snake body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        // Przesuwanie ciała węża
        if (snakeBody.size() > 0) {
            for (int i = snakeBody.size() - 1; i > 0; i--) {
                snakeBody.set(i, snakeBody.get(i - 1));  // Przesuń segment na pozycję poprzedniego
            }
            snakeBody.set(0, new Tile(snakeHead.x, snakeHead.y));  // Przesuń pierwszy segment za głową
        }

        // Głowa Snake'a
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Sprawdzanie kolizji z własnym ciałem
        for (Tile part : snakeBody) {
            if (collision(snakeHead, part)) {
                gameOver();  // Wywołaj gameOver, gdy wąż uderzy w swoje ciało
                return;
            }
        }

        // Sprawdzanie, czy wąż nie wyjeżdża poza planszę
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver();  // Zakończ grę, jeśli wąż wyjeżdża poza planszę
            return;
        }

        // Jedzenie jedzonka
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(snakeHead.x, snakeHead.y));  // Dodwanie nowego segmentu do ciała węża
            placeFood();
        }
    }

    public void gameOver() {
        gameLoop.stop();
        int choice = JOptionPane.showConfirmDialog(this, "Wanna try again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    public void resetGame() {
        snakeHead = new Tile(5, 5);  // Resetowanie pozycji głowy węża
        snakeBody.clear();           // Czyszczenie ciała węża
        placeFood();                 // Nowe jedzenie
        velocityX = 0;               // Resetowanie prędkości
        velocityY = 1;
    
        gameLoop.start();            // Rozpoczęcie ponownie pętlę gry
        repaint();                   // Odświeżenia okna
        requestFocus();              // Skierowanie focus na panel gry
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;  
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nieużywane
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Nieużywane
    }
}
