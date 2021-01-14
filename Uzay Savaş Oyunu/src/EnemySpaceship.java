import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;

public class EnemySpaceship {
	private boolean ready, dead, hit;
	private BufferedImage bd1, bd2, bd3, image;
	private double x, y, dx, dy, rad, speed;
	private int r, health, type, rank;
	private long hitTimer;

	public EnemySpaceship (int type, int rank) {
		this.type = type;
		this.rank = rank;

		// Düþman Gemisi Tipi ve Deðeri Tanýmlamalarý
		// Düþman Tipi ve Rank Deðerine Göre Güç ve Hýz Tanýmlamasý
		if (type == 1) {
			if (bd1 == null) bd1 = new Utilities().loadImg("/images/alien1.png");
			if (rank == 1) {
				speed = 3;
				r = 15;
				health = 1;
			}
			if (rank == 2) {
				speed = 3;
				r = 20;
				health = 2;
			}
			if (rank == 3) {
				speed = 1.5;
				r = 25;
				health = 3;
			}
			image = bd1;
		}
		if (type == 2) {
			if (bd2 == null) bd2 = new Utilities().loadImg("/images/alien2.png");
			if (rank == 1) {
				speed = 3;
				r = 15;
				health = 2;
			}
			if (rank == 2) {
				speed = 3;
				r = 25;
				health = 3;
			}
			if (rank == 3) {
				speed = 2.5;
				r = 30;
				health = 3;
			}
			image = bd2;
		}
		if (type == 3) {
			if (bd3 == null) bd3 = new Utilities().loadImg("/images/alien3.png");
			if (rank == 1) {
				speed = 1.5;
				r = 15;
				health = 5;
			}
			if (rank == 2) {
				speed = 1.5;
				r = 25;
				health = 5;
			}
			if (rank == 3) {
				speed = 1.5;
				r = 30;
				health = 5;
			}
			image = bd3;
		}

		x = Math.random() * GameWindow.width / 2 + GameWindow.width / 4;
		y = -r;

		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);

		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;

		ready = false;
		dead = false;
		hit = false;
		hitTimer = 0;
	}
	// Getter Fonksiyonlarý
	public double getX () {return x;}
	public double getY () {return y;}
	public int getR () {return r;}
	public boolean isDead () {return dead;}
	public int getType () {return type;}
	public int getRank () {return rank;}

	// Gemi Patlayýnca Seviyeye Göre Yenilerini Oluþtur Yada Yok Et
	public void explode () {
		if (rank > 1) {
			int amount = 0;
			if (type == 1) amount = 2;
			if (type == 2) amount = 2;
			if (type == 3) amount = 2;

			for (int i = 0; i < amount; i++) {
				EnemySpaceship e = new EnemySpaceship(getType(), getRank() - 1);
				e.x = this.x;
				e.y = this.y;
				double angle = 0;

				if (!ready) angle = Math.random() * 140 + 20;
				else angle = Math.random() * 360;

				e.rad = Math.toRadians(angle);
				GameWindow.enemies.add(e);
			}
		}
	}
	
	// Vurulma Durumunda Canýný Düþürme Fonksiyonu
	public void hit () {
		health--;
		if (health <= 0) dead = true;
		hit = true;
		hitTimer = System.nanoTime();
	}

	// Hareket Güncelleme Fonksiyonu
	public void update () {
		x += dx;
		y += dy;

		if (!ready) 
			if (x > r && x < GameWindow.width - r && y > r && y < GameWindow.height - r) 
				ready = true;

		// Ekran Kenarlarýna Çarpýnca Geri Döndür
		if (x < r && dx < 0) dx = -dx;
		if (y < r && dy < 0) dy = -dy;
		if (x > GameWindow.width - r && dx > 0) dx = -dx;
		if (y > GameWindow.height - r && dy > 0) dy = -dy;

		if (hit) {
			long elapsed = (System.nanoTime() - hitTimer) / 1000000;
			if (elapsed > 50) {
				hit = false;
				hitTimer = 0;
			}
		}
	}
	
	//Ekranda Gösterme Fonksiyonu
	public void draw (Graphics2D g) {
		g.drawImage(image, (int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 1.7), null);
	}
}