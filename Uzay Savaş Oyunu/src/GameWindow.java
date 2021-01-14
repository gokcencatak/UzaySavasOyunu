import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class GameWindow extends JPanel implements Runnable, KeyListener {
	public final static int width = 800;
	public final static int height = 600;

	private boolean running, levelStart = false;
	private BufferedImage image, playerImage, bgImage, bg1, bg2, bg3;
	private Graphics2D g;
	
	// FPS, Seviye Arasý Bekleme, Seviye Sýnýrý
	private int fps = 30, levelDelay = 2000, levelLimit = 10;
	private int levelNumber;
	private long levelStartTimer, levelStartTimerDifference;
	private Thread thread;

	public static PlayerSpaceship player;
	public static ArrayList <RocketFire> bullets;
	public static ArrayList <EnemySpaceship> enemies;
	public static ArrayList <Life> lives;
	public static ArrayList <Explosion> explosions;
	public static ArrayList <GameText> texts;

	private File flFile = null;
	private FileReader frRead = null;
	private BufferedReader brRead = null;
	private PrintWriter outputFile;
	private String scoreFile = "puan.txt";

	public GameWindow () {
		super();
		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
	}
	public void addNotify () {
		super.addNotify();

		// Oyun Thread'ý
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}

		addKeyListener(this);
	}

	// Runnable Fonksiyonlarý
	public void run () {
		running = true;

		if (playerImage == null) playerImage = new Utilities().loadImg("/images/hero.png");

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Arkaplan Resimleri
		if (bg1 == null) bg1 = new Utilities().loadImg("/images/stage1.jpg");
		if (bg2 == null) bg2 = new Utilities().loadImg("/images/stage2.jpg");
		if (bg3 == null) bg3 = new Utilities().loadImg("/images/stage3.jpg");

		// Oyuncu / Kurþun / Düþman / Patlama Tanýmlamalarý
		player = new PlayerSpaceship();
		bullets = new ArrayList<RocketFire>();
		enemies = new ArrayList<EnemySpaceship>();
		lives = new ArrayList<Life>();
		explosions = new ArrayList<Explosion>();
		texts = new ArrayList<GameText>();

		levelStartTimer = 0;
		levelStartTimerDifference = 0;
		levelStart = true;
		levelNumber = 0;

		long startTime ;
		long URDTimeMillis;
		long waitTime = 0;
		long totalTime = 0;
		long targetTime = 1000 / fps;

		int frameCount = 0;
		int maxFrameCount = 30;

		//		
		while (running) {
			startTime = System.nanoTime();

			updateGameScreen();
			gameRender();
			gameDraw();

			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - URDTimeMillis;
			try {
				Thread.sleep(waitTime);
			} catch (Exception ex) {
				totalTime += System.nanoTime() - startTime;
				frameCount++;
				if (frameCount == maxFrameCount) {
					frameCount = 0;
					totalTime = 0;
				}
			}
		}

		// Oyun Sonu Mesajý
//		Game over message
		g.setColor(new Color(0, 100, 255));
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 20));

		String s = "Oyun Bitti";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (width - length) / 3, height / 3);

		String score = "Puanýn: " + player.getScore();
		g.drawString(score, (width - length) / 3, height / 3 + 50);

		String maxScore = "";

		// Puan Dosyasý içeriði: seviye(boþluk)puan
		String arr[] = readScoreFile(scoreFile).split(" ");
		try {
			if (player.getScore() > Integer.parseInt(arr[1])) {
				// Yeni Puaný Puan Dosyasýna Yaz
				writeFile(scoreFile, levelNumber + " " + player.getScore());

				maxScore = "Rekor \nLevel: " + levelNumber + " | Puan: " + player.getScore();
				String congrats = "Tebrikler!, Yeni rekor.";
				g.drawString(congrats.toUpperCase(), (width - length) / 3, height / 3 + 200);
			} else {
				maxScore = "En Yüksek Seviye: " + arr[0] + " | Puan: " + arr[1];
			}
		}
		catch (Exception e) {
			System.out.println("Dosya açýlamadý");
		}
		g.setColor(Color.WHITE);
		g.drawString(maxScore, (width - length) / 3, height / 3 + 100);

		g.drawString("Çýkmak için Esc Tuþuna Basýn", (width - length) / 3, height / 3 + 250);
		g.drawString("Ayþe Gökçen Çatak", (width - length) / 2 + 200, (height - length) / 2 + 340);

		gameDraw();
	}
	
	// Düþmanlarý Oluþtur
	private void createEnemies () {
		enemies.clear();
		
		if (levelNumber >= 1 && levelNumber < 3) {
			for (int i = 0; i < levelNumber; i++) 
				enemies.add(new EnemySpaceship(1, 1));
			for (int i = 0; i < 3; i++) 
				enemies.add(new EnemySpaceship(1, levelNumber));
		}
		if (levelNumber >= 3 && levelNumber <= 5) {
			for (int i = 0; i < 5; i++) 
				enemies.add(new EnemySpaceship(2, 1));
			for (int i = 0; i < 5; i++) 
				enemies.add(new EnemySpaceship(2, (levelNumber - 2)));
		}
		if (levelNumber >= 5 && levelNumber <= 10) {
			for (int i = 0; i < 10; i++) 
				enemies.add(new EnemySpaceship(3, 1));
			for (int i = 0; i < 10; i++) 
				enemies.add(new EnemySpaceship(3, (levelNumber - 4)));
		}
		else if (levelNumber > levelLimit)
			running = false;
	}

	// Oyun Ekranýný Göster
	private void updateGameScreen() {
		if (levelStartTimer == 0 && enemies.size() == 0) {
			levelNumber++;
			levelStart = false;
			levelStartTimer = System.nanoTime();
		} else {
			levelStartTimerDifference = (System.nanoTime() - levelStartTimer) / 1000000;
			if (levelStartTimerDifference > levelDelay) {
				levelStart = true;
				levelStartTimer = 0;
				levelStartTimerDifference = 0;
			}
		}

		// Düþman Gemilerini Ekranda Göster
		if (levelStart && enemies.size() == 0) 
			createEnemies();

		player.update();

		// Kurþunlarý Göster
		for (int i = 0; i < bullets.size(); i++) {
			boolean remove = bullets.get(i).update();
			if (remove) {
				bullets.remove(i);
				i--;
			}
		}
		
		// Patlamalarý Göster
		for (int i = 0; i < explosions.size(); i++) {
			boolean remove = explosions.get(i).update();
			if (remove) {
				explosions.remove(i);
				i--;
			}
		}
		
		// Gücü Güncelle
		for (int i = 0; i < lives.size(); i++) {
			boolean remove = lives.get(i).update();
			if (remove) {
				lives.remove(i);
				i--;
			}
		}
		
		// Puaný ve Seviyeyi Yazdýr
		for (int i = 0; i < texts.size(); i++) {
			boolean remove = texts.get(i).update();
			if (remove) {
				texts.remove(i);
				i--;
			}
		}
		// Düþmanlarý Hareket Ettir
		for (int i = 0; i < enemies.size(); i++) 
			enemies.get(i).update();

		for (int i = 0; i < bullets.size(); i++) {
			RocketFire b = bullets.get(i);
			double bx = b.getX();
			double by = b.getY();
			double br = b.getR();
			for (int j = 0; j < enemies.size(); j++) {
				EnemySpaceship e = enemies.get(j);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();

				double dx = bx - ex;
				double dy = by - ey;

				double dist = Math.sqrt(dx * dx + dy * dy);

				// Kurþun Düþmana Çarptýðýnda Ekrandan Kaldýr.
				if (dist < br + er) {
					e.hit();
					bullets.remove(i);
					i--;
					break;
				}
			}
		}

		// Oyuncu Oyuna Devam Ediyor Mu?
		if (player.isDead()) {
			running = false;
		}

		// Düþman Gemisi Vurulduysa Ekranda Patlama Görüntüsü Göster
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).isDead()) {
				EnemySpaceship _enemy = enemies.get(i);

				// Can Ekleme
				double rand = Math.random();
				if (rand < 0.30) 
					lives.add(new Life(_enemy.getX(), _enemy.getY()));

				player.setScore(_enemy.getType() + _enemy.getRank());
				enemies.remove(i);
				i--;

				_enemy.explode();
				explosions.add(new Explosion(_enemy.getX(), _enemy.getY(),
											 _enemy.getR(), _enemy.getR() + 30));
			}
		}

		// Çarpýþma Kontrolü
		if (!player.isRecovering()) {
			int px = player.getX();
			int py = player.getY();
			int pr = player.getR();
			for (int i = 0; i < enemies.size(); i++) {
				EnemySpaceship e = enemies.get(i);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();

				double dx = px - ex;
				double dy = py - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);

				if (dist < pr + er) 
					player.loseLife();
			}
		}

		// Kullanýcý ve Hediye Canlarýn Çarpýþmasýný Kontrol Et
		int px = player.getX();
		int py = player.getY();
		int pr = player.getR();
		for (int i = 0; i < lives.size(); i++) {
			Life l = lives.get(i);
			double x = l.getX();
			double y = l.getY();
			double r = l.getR();
			double dx = px - x;
			double dy = py - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			if (dist < pr + r) {
				player.setLife(player.getLives() + 1);
				texts.add(new GameText(player.getX(), player.getY(), 2000, "+1 Can"));

				lives.remove(i);
				i--;
			}
		}
	}
	
	private void gameRender() {
		// Oyun arkaplaný her 2 seviyede yeni arkaplan göster
		g.drawImage(image, 0, 0, null);
		
		if (levelNumber >= 1 && levelNumber < 3) 
			bgImage = bg1;
		if (levelNumber >= 3 && levelNumber < 5) 
			bgImage = bg2;
		if (levelNumber >= 5 && levelNumber < 8) 
			bgImage = bg3;
		if (levelNumber >= 8 && levelNumber <= 10) 
			bgImage = bg1;

		g.drawImage(bgImage, 0, 0, null);

		// Oyuncu Gemisini Ekranda Göster
		player.draw(g);

		// Kurþunlarý Ekranda Göster
		for (int i = 0; i < bullets.size(); i++) 
			bullets.get(i).draw(g);
		
		// Düþmanlarý Ekranda Göster
		for (int i = 0; i < enemies.size(); i++) 
			enemies.get(i).draw(g);
		
		// Hediye Canlarý Ekranda Göster
		for (int i = 0; i < lives.size(); i++) 
			lives.get(i).draw(g);
		
		// Patlamalarý Ekranda Göster
		for (int i = 0; i < explosions.size(); i++) 
			explosions.get(i).draw(g);
		
		// Puan ve Seviyeyi Ekranda Göster
		for (int i = 0; i < texts.size(); i++) 
			texts.get(i).draw(g);

		// Seviyeyi Ekranda Göster
		if (levelStartTimer != 0 && levelNumber <= levelLimit) {
			g.setFont(new Font("Century Gothic", Font.PLAIN, 20));
			String s =  "Seviye - " + levelNumber + " Bol Þans!";
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			g.setColor(Color.WHITE);
			g.drawString(s, width / 2 - length, height / 2);
		}

		// Oyuncu Canlarýný Ekranda Göster
		for (int i = 0; i < player.getLives(); i++) 
			g.drawImage(playerImage, 25 + (35 * i), 20, 30, 30, null);

		// Puaný Ekranda Göster
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.BOLD, 16));
		g.drawString("Puan: " + player.getScore(), width - 150, 30);
		
		// Seviyeyi Ekranda Göster
		g.setFont(new Font("Century Gothic", Font.BOLD, 14));
		g.drawString("Seviye: " + levelNumber, width - 250, 30);
	}
	
	private void gameDraw() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	// Klavye Kontrolleri
	public void keyPressed (KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == 27) System.exit(0);
		if (keyCode == KeyEvent.VK_LEFT) player.setLeft(true);
		if (keyCode == KeyEvent.VK_RIGHT) player.setRigth(true);
		if (keyCode == KeyEvent.VK_UP) player.setUp(true);
		if (keyCode == KeyEvent.VK_DOWN) player.setDown(true);
		if (keyCode == KeyEvent.VK_Z || keyCode == 32) player.setFiring(true);
	}
	public void keyReleased (KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == 27) System.exit(0);
		if (keyCode == KeyEvent.VK_LEFT) player.setLeft(false);
		if (keyCode == KeyEvent.VK_RIGHT) player.setRigth(false);
		if (keyCode == KeyEvent.VK_UP) player.setUp(false);
		if (keyCode == KeyEvent.VK_DOWN) player.setDown(false);
		if (keyCode == KeyEvent.VK_Z || keyCode == 32) player.setFiring(false);
	}
	public void keyTyped (KeyEvent e) {}
	
	private String readScoreFile (String srcFile) {
		String line = null;
		try {
			flFile = new File(srcFile);
			frRead = new FileReader(flFile);
			brRead = new BufferedReader(frRead);

			line = brRead.readLine();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (null != frRead) {
					frRead.close();
					return line;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	public void writeFile (String srcFile, String content) {
		try {
			outputFile = new PrintWriter(new FileWriter(srcFile), true);
			outputFile.print(content);
			outputFile.close();
		} catch (IOException er) {
			System.err.println("Dosya yazýlamadý.");
		}
	}
}