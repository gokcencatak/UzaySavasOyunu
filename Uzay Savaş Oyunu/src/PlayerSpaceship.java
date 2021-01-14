import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;

public class PlayerSpaceship {
	private boolean left, right, up, down, firing, recovering;
	private BufferedImage normalImage, image, normalImageTransparent;
	private int x, y, r, dx, dy, speed, lives, score, power;
	private long firingTimer, firingDelay, recoveryTimer;

	public PlayerSpaceship () {
		// Baþlangýç Pozisyonu ve Büyüklüðü
		x = GameWindow.width / 2;
		y = GameWindow.height - 100;
		r = 32;

		dx = 0;
		dy = 0;
		speed = 5;
		lives = 3;

		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 120;

		recovering = false;
		recoveryTimer = System.nanoTime();

		score = 0;

		if (normalImage == null) normalImage = new Utilities().loadImg("/images/hero.png");

		if (normalImageTransparent == null) normalImageTransparent = new Utilities().loadImg("/images/hero-transparent.png");
		image = normalImage;
	}

	// Pozisyon ve Durum Güncelleme Fonksiyonu
	public void update () {
		if (left) dx = -speed;
		if (right) dx = speed;
		if (up) dy = -speed;  
		if (down) dy = speed;

		x += dx;
		y += dy;

		// Oyuncu Hareketlerini Ekran Sýnýrlarý ile Kýsýtla
		if (x < r) x = r;
		if (y < r) y = r;
		if (x > GameWindow.width - r) x = GameWindow.width - r;
		if (y > GameWindow.height - r) y = GameWindow.height - r;

		dx = 0;
		dy = 0;

		// Kurþun Atma Sýklýðýný Belirle
		if (firing) {
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			if (elapsed > firingDelay) {
				firingTimer = System.nanoTime();
				GameWindow.bullets.add(new RocketFire(270, (x + 10), y));
			}
		}

		// Düþmanla çarpýþma durumunda 2 sn iyileþme süresi
		if (recovering) {
			long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
			if (elapsed > 2000) {
				recovering = false;
				recoveryTimer = 0;
			}
		}
	}
	
	// Ekranda Gösterme Fonksiyonu
	public void draw (Graphics2D g) {
		if (recovering) 
			image = normalImageTransparent;
		else 
			image = normalImage;
		g.drawImage(image, x - r, y - r, null);
	}

	// Setter Fonksiyonlarý
	public void setLife (int life) {lives = life;}
	public void setLeft (boolean direction) {left = direction;}
	public void setRigth (boolean direction) {right = direction;}
	public void setUp (boolean direction) {up = direction;}
	public void setDown (boolean direction) {down = direction;}
	public void setFiring (boolean fire) {firing = fire;}
	public void setScore (int sc) {score += sc;}
	
	// Getter Fonksiyonlarý
	public boolean isDead () {return lives <= 0;}
	public boolean isRecovering () {return recovering;}
	public int getLives () {return lives;}
	public int getPower () {return power;}
	public int getR () {return r;}
	public int getScore () {return score;}
	public int getX () {return x;}
	public int getY () {return y;}

	// Can Kaybetme Fonksiyonu
	public void loseLife () {
		lives--;
		recovering = true;
		recoveryTimer = System.nanoTime();
	}
}