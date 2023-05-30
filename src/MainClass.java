

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import java.util.concurrent.TimeUnit;
import java.applet.*;
import acm.graphics.*;
import acm.program.GraphicsProgram;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class MainClass extends GraphicsProgram implements ActionListener {

    public GOval food;

    private ArrayList<GRect> snakeBody;

    private int snakeX, snakeY, snakeWidth, snakeHeight;

    public Timer timer = new Timer(100, this);

    private boolean isPlaying, isGameOver;
    private int score, previousScore;
    private GLabel scoreLabel;
    private GLabel instructions;
    boolean blockKey = false;
    boolean goingUp = false;
    boolean goingDown = false;
    boolean goingLeft = true;
    boolean goingRight = false;

    GRect background = new GRect(450, 300);
    @Override
    public void keyReleased(KeyEvent e) {
        if (blockKey) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    goingUp = true;
                    goingDown = false;
                    goingLeft = false;
                    goingRight = false;
                    break;

                case KeyEvent.VK_DOWN:
                    goingUp = false;
                    goingDown = true;
                    goingLeft = false;
                    goingRight = false;
                    break;

                case KeyEvent.VK_LEFT:
                    goingUp = false;
                    goingDown = false;
                    goingLeft = true;
                    goingRight = false;
                    break;

                case KeyEvent.VK_RIGHT:
                    goingUp = false;
                    goingDown = false;
                    goingLeft = false;
                    goingRight = true;
                    break;
            }
            blockKey = false;

        }
    }

    public void background() {

        background.setLocation(100, 100);
        background.setFillColor(Color.black);
        background.setFilled(true);
        add(background);


    }

    public void run() {
        snakeBody = new ArrayList<>();
        createGCanvas();
        addKeyListeners();
        GRect square = new GRect(getGCanvas().getWidth(), getGCanvas().getHeight());
        square.setFillColor(Color.magenta);
        square.setFilled(true);
        add(square);
        background();
        randomFood();
        setUpInfo();
        timer.start();
        drawSnake();
        addKeyListeners();


    }

    public void randomFood() {
        Random rand = new Random();
        food = new Ball(50, 50, 15, 15);
        food.setFillColor(Color.red);
        food.setFilled(true);
        add(food);
        int randX = 100 + rand.nextInt(30)*15;
        int randY = 100 + rand.nextInt(20)*15;
        food.setLocation(randX, randY);
    }

    public void drawSnake() {
        snakeWidth = 15;
        snakeHeight = 15;
        for (int i = 0; i < 10; i++) {
            SnakePart part = new SnakePart(250 + i * 15, 340, snakeWidth, snakeHeight);
            part.setColor(Color.green);
            part.setFilled(true);
            add(part);
            snakeBody.add(part);

        }
    }

    public void setUpInfo() {
        GLabel startLabel = new GLabel("You can click anywhere to start your game", getWidth() / 2,
                (getHeight() / 2));
        startLabel.move(-startLabel.getWidth() / 2, -startLabel.getHeight());
        startLabel.setColor(Color.WHITE);
        add(startLabel);
        GLabel rulesLabel = new GLabel("Use the arrow keys to move around", getWidth() / 2,
                (getHeight() / 2) + 75);
        rulesLabel.move(-rulesLabel.getWidth() / 2, -rulesLabel.getHeight());
        rulesLabel.setColor(Color.WHITE);
        add(rulesLabel);


        // putting score label into display
        scoreLabel = new GLabel("Score : " + score, getWidth()/2-70, 450);
        scoreLabel.scale(3);
        scoreLabel.move(-scoreLabel.getWidth() / 2, -scoreLabel.getHeight());
        scoreLabel.setColor(Color.black);
        add(scoreLabel);
        waitForClick();
        isPlaying = true;
        remove(startLabel);
        remove(rulesLabel);
    }


    public void keyPressed(KeyEvent keyPressed) {
        System.out.println("keypressed");
        blockKey = true;
    }


    private void redrawSnake() {
        for (int i = snakeBody.size() - 1; i > 0; i--) {
            snakeBody.get(i).setLocation(snakeBody.get(i - 1).getX(), (snakeBody.get(i - 1).getY()));
        }
        for (int i = 0; i < snakeBody.size(); i++) {
            add(snakeBody.get(i));
        }
    }

    private void growSnake() {

        GRect rect = new GRect(snakeBody.get(snakeBody.size() - 1).getX() - snakeWidth,
                snakeBody.get(snakeBody.size() - 1).getY() - snakeHeight, snakeWidth, snakeHeight);
        rect.setFilled(true);
        rect.setColor(Color.green);
        snakeBody.add(rect);
    }

    private void moveUp() {
        redrawSnake();
        snakeBody.get(0).setLocation(snakeBody.get(0).getX(), snakeBody.get(0).getY() - 15);
    }

    private void moveDown() {
        redrawSnake();
        snakeBody.get(0).setLocation(snakeBody.get(0).getX(), snakeBody.get(0).getY() + 15);

    }

    private void moveLeft() {
        redrawSnake();
        snakeBody.get(0).setLocation(snakeBody.get(0).getX() - 15, snakeBody.get(0).getY());
    }

    private void moveRight() {
        redrawSnake();
        snakeBody.get(0).setLocation(snakeBody.get(0).getX() + 15, snakeBody.get(0).getY());
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
       if(!isGameOver)
        {
            scoreLabel.setLabel("Score : " + score);
        if (goingUp) {
            moveUp();


        } else if (goingDown) {
            moveDown();


        } else if (goingRight) {
            moveRight();


        } else if (goingLeft) {
            moveLeft();


        }
        if (intersectsWithFood()) {
            score += 1;
            growSnake();
            remove(food);
            randomFood();
        }
        if (intersectsWithSnake()||outOfBound()) {
            isGameOver = true;
        }
    }
     else {

        timer.stop();
        GLabel gameOverLabel = new GLabel("Game Over!", getWidth() / 2, getHeight() / 2);
        gameOverLabel.move(-gameOverLabel.getWidth() / 2, -gameOverLabel.getHeight());
        gameOverLabel.setColor(Color.white);
        add(gameOverLabel);

    }


    }

    private boolean intersectsWithFood() {

        if (food.getBounds().intersects(snakeBody.get(0).getBounds())) {
            return true;
        } else {
            return false;
        }
    }
        private boolean intersectsWithSnake() {
            GRect head = null;
            if ((!goingLeft && !goingDown && !goingUp && !goingRight) || goingRight) {
                head = new GRect(snakeBody.get(0).getX() + 15, snakeBody.get(0).getY() + 5, 5, 5);
            } else if (goingLeft) {
                head = new GRect(snakeBody.get(0).getX() - 5, snakeBody.get(0).getY() + 5, 5, 5);
            } else if (goingDown) {
                head = new GRect(snakeBody.get(0).getX() + 5, snakeBody.get(0).getY() + 15, 5, 5);
            } else if (goingUp) {
                head = new GRect(snakeBody.get(0).getX() + 5, snakeBody.get(0).getY() - 5, 5, 5);
            }
            for (int i = 1; i < snakeBody.size(); i++) {
                if (head.getBounds().intersects(snakeBody.get(i).getBounds())) {
                    return true;
                }
            }
            return false;

        }

        public boolean outOfBound(){
       if(snakeBody.get(0).getX()>535||snakeBody.get(0).getX()<100||snakeBody.get(0).getY()>385||snakeBody.get(0).getY()<100){
            return true;
            }

        return false;
        }

}
