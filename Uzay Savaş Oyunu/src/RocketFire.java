import java.awt.*;

public class RocketFire {
	private double x, y, rad, speed, dx, dy;
	private int r;
	private Color color;

	public RocketFire (double angle, int x, int y) {
		this.x = x;
		this.y = y;
		r = 5;

		rad = Math.toRadians(angle);
		speed = 10;
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;

		color = Color.YELLOW;
	}

	// Konum Güncelleme Fonksiyonu
	public boolean update () {
		x += dx;
		y += dy;

		// Oyun ekranýnýn dýþýna çýktý mý?
		if (x < -r || x > GameWindow.width + r || y > GameWindow.height + r) 
			return true;
		return false;
	}
	public void draw (Graphics2D g) {
		g.setColor(color);
		g.fillOval((int) (x - r) - 10, (int) (y - r) - 15, 2 * r, 2 * r);
	}
	// Getter Fonksiyonlarý
	public double getX () {return x;}
	public double getY () {return y;}
	public double getR () {return r;}
}