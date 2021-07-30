package main;

import javax.swing.JPanel;

import input.Keyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

public class Game extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	
	private int snakex = 100, snakey = 100;
	
	private int score = 0;
	
	private Random random;

	private Keyboard key;
	
	public Thread thread;
	private boolean running = false;
	
	private Snake snake;
	private int snakeSpeed = 2;
	
	private Ball balls[];
	private int numberOfBalls = 15;

	public Game() {
		initGui();
		snake = new Snake(snakex, snakey, 75);
		balls = new Ball[numberOfBalls];
		key = new Keyboard();
		addKeyListener(key);
		
		random = new Random();
		
		for(int i = 0; i < balls.length; i++) {
			int ballx = random.nextInt(WIDTH - 15 * 2);
			int bally = random.nextInt(HEIGHT - 15 * 2);
			
			int balldx = random.nextInt(4) + 1;
			int balldy = random.nextInt(4) + 1;
			balls[i] = new Ball(ballx, bally, balldx, balldy);
			balls[i].setColor(Color.red);
		}
		
		setFocusable(true);
		requestFocus();
	}
	
	private void initGui() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;

		//*****************************TURN ON ANTIALIASING******************************************
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//*******************************************************************************************


		graphics.fillRect(0, 0, WIDTH, HEIGHT);

		graphics.setColor(Color.green);
		snake.draw(snakex, snakey, graphics);
		for(int i = 0; i < balls.length; i++) {
			balls[i].draw(balls[i].x, balls[i].y, graphics);
		}
		
		graphics.dispose();
	}
	
	public synchronized void start() {
		if(thread != null) return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	public synchronized void stop() {
		if(thread == null) return;

		running = false;
		try {
			thread.join();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		
		while(running) {
			long now = System.nanoTime();
			delta += (now-lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				delta--;
			}
			repaint();
		}

	}
	
	public void update() {
		if(key.down) snakey += snakeSpeed;
		if(key.up) snakey -= snakeSpeed;
		if(key.right) snakex += snakeSpeed;
		if(key.left) snakex -= snakeSpeed;
		key.update();
		
		checkForCollisions();
		
		moveBall();
	}
	
	private void moveBall() {
		for(int i = 0; i < balls.length; i++) {
			balls[i].x += balls[i].dx;
			balls[i].y += balls[i].dy;
		}
	}
	
	
	private void checkForCollisions() {
		
		//SNAKE WITH BOUNDARY
		if(snakex > WIDTH - 10 || snakey > HEIGHT - 10 || snakex < -10 || snakey < -10) {
			running = false;
		}
		
		//SNAKE WITH BALL
		for(int i = 0; i < balls.length; i++) {
			if(snake.intersects(balls[i])) {
				snakeSpeed++;
				score++;
				balls[i].x = random.nextInt(WIDTH - balls[i].radius * 2);
				balls[i].y = random.nextInt(HEIGHT - balls[i].radius * 2);

				System.out.println("Collided with ball " + score);
			}
			
		}
		
		//BALL WITH BOUNDARY
		for(int i = 0; i < balls.length; i++) {
			if(balls[i].x > WIDTH - 10 || balls[i].x < -10) {
				balls[i].dx = -balls[i].dx;
			}
			if(balls[i].y > HEIGHT - 10 || balls[i].y < -10) {
				balls[i].dy = -balls[i].dy;
			}
		}
	}
	
}













