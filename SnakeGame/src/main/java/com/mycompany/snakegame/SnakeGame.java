package com.mycompany.snakegame;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    
    private static final int UNIT_SIZE = 25;
    private static final int TILES_HORIZONTAL = 24;
    private static final int TILES_VERTICAL = 22;
    private static final int BOARD_WIDTH = TILES_HORIZONTAL * UNIT_SIZE; // 600
    private static final int BOARD_HEIGHT = TILES_VERTICAL * UNIT_SIZE; // 550
    private static final int DELAY = 100;
    private static final int CONTROL_PANEL_HEIGHT = 50;
    private static final int TOTAL_HEIGHT = BOARD_HEIGHT + CONTROL_PANEL_HEIGHT; // 600

    private ArrayList<Point> snake;
    private Point food;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;
    private int score = 0;
    private JButton startButton;
    private JButton restartButton;
    private JLabel scoreLabel;
    private boolean gameStarted = false;

    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(BOARD_WIDTH, TOTAL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setLayout(new BorderLayout());

        JPanel controlPanel = createCenteredControlPanel();
        this.add(controlPanel, BorderLayout.NORTH);

        initializeGame();
    }

    private JPanel createCenteredControlPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.DARK_GRAY);
        outerPanel.setPreferredSize(new Dimension(BOARD_WIDTH, CONTROL_PANEL_HEIGHT));

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        centerPanel.setBackground(Color.DARK_GRAY);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(Color.GREEN);
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(100, 30));
        startButton.addActionListener(this);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setBackground(Color.ORANGE);
        restartButton.setForeground(Color.WHITE);
        restartButton.setPreferredSize(new Dimension(100, 30));
        restartButton.addActionListener(this);
        restartButton.setEnabled(false);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);

        centerPanel.add(startButton);
        centerPanel.add(restartButton);
        centerPanel.add(scoreLabel);

        outerPanel.add(centerPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    public void initializeGame() {
        snake = new ArrayList<>();
 
        int startX = (TILES_HORIZONTAL / 2) * UNIT_SIZE;
        int startY = (TILES_VERTICAL / 2) * UNIT_SIZE + CONTROL_PANEL_HEIGHT;
        snake.add(new Point(startX, startY));
        newFood();
        running = false;
        direction = 'R';
        score = 0;
        scoreLabel.setText("Score: " + score);
        gameStarted = false;
    }

    public void startGame() {
        running = true;
        gameStarted = true;
        timer = new Timer(DELAY, this);
        timer.start();
        startButton.setEnabled(false);
        restartButton.setEnabled(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!gameStarted) {
            // Welcome message
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("SNAKE GAME", 
                (BOARD_WIDTH - metrics.stringWidth("SNAKE GAME")) / 2, 
                TOTAL_HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            metrics = getFontMetrics(g.getFont());
            g.drawString("Press Start Game to begin!", 
                (BOARD_WIDTH - metrics.stringWidth("Press Start Game to begin!")) / 2, 
                TOTAL_HEIGHT / 2);
            
            g.drawString("Use W,A,S,D keys to control the snake", 
                (BOARD_WIDTH - metrics.stringWidth("Use W,A,S,D keys to control the snake")) / 2, 
                TOTAL_HEIGHT / 2 + 30);
            return;
        }

        if (running) {
            g.setColor(Color.DARK_GRAY);
            
            // Vertical lines
            for (int i = 0; i <= TILES_HORIZONTAL; i++) {
                int x = i * UNIT_SIZE;
                g.drawLine(x, CONTROL_PANEL_HEIGHT, x, TOTAL_HEIGHT);
            }
            // Horizontal lines
            for (int i = 0; i <= TILES_VERTICAL; i++) {
                int y = CONTROL_PANEL_HEIGHT + (i * UNIT_SIZE);
                g.drawLine(0, y, BOARD_WIDTH, y);
            }

            // Food
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            // Snake
            for (int i = 0; i < snake.size(); i++) {
                if (i == 0) {
                    // Head
                    g.setColor(Color.GREEN);
                } else {
                    // Body
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(snake.get(i).x, snake.get(i).y, UNIT_SIZE, UNIT_SIZE);
                
                
                g.setColor(Color.BLACK);
                g.drawRect(snake.get(i).x, snake.get(i).y, UNIT_SIZE, UNIT_SIZE);
            }
        } else if (gameStarted) {
            // Game over
            gameOver(g);
        }
    }

    public void newFood() {
        int x = random.nextInt(TILES_HORIZONTAL) * UNIT_SIZE;
        int y = random.nextInt(TILES_VERTICAL) * UNIT_SIZE + CONTROL_PANEL_HEIGHT;
        food = new Point(x, y);
        
        while (snake.contains(food)) {
            x = random.nextInt(TILES_HORIZONTAL) * UNIT_SIZE;
            y = random.nextInt(TILES_VERTICAL) * UNIT_SIZE + CONTROL_PANEL_HEIGHT;
            food = new Point(x, y);
        }
    }

    public void move() {
        Point newHead = new Point(snake.get(0));

        switch (direction) {
            case 'U':
                newHead.y -= UNIT_SIZE;
                break;
            case 'D':
                newHead.y += UNIT_SIZE;
                break;
            case 'L':
                newHead.x -= UNIT_SIZE;
                break;
            case 'R':
                newHead.x += UNIT_SIZE;
                break;
        }

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            score++;
            scoreLabel.setText("Score: " + score);
            newFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    public void checkCollisions() {
        
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }

        // Check if head touches borders
        if (head.x < 0 || head.x >= BOARD_WIDTH || head.y < CONTROL_PANEL_HEIGHT || head.y >= TOTAL_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", 
            (BOARD_WIDTH - metrics1.stringWidth("GAME OVER")) / 2, 
            TOTAL_HEIGHT / 2);

        // Score text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Final Score: " + score, 
            (BOARD_WIDTH - metrics2.stringWidth("Final Score: " + score)) / 2, 
            TOTAL_HEIGHT / 2 + 50);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press Restart to play again", 
            (BOARD_WIDTH - metrics3.stringWidth("Press Restart to play again")) / 2, 
            TOTAL_HEIGHT / 2 + 80);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startGame();
        } else if (e.getSource() == restartButton) {
            timer.stop();
            initializeGame();
            startButton.setEnabled(true);
            restartButton.setEnabled(false);
            repaint();
        } else if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:  // A key for left
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_D:  // D key for right
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_W:  // W key for up
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_S:  // S key for down
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();

        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(BOARD_WIDTH + 16, TOTAL_HEIGHT + 39); 
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
