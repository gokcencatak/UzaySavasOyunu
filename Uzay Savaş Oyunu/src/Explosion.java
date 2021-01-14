import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;

public class Explosion {
	private double x, y;
	private int r, maxRadius;
	private BufferedImage ex;

	public Explosion (double x, double y, int r, int max) {
		if (ex == null) ex = new Utilities().loadImg("/images/explosion.png");

		this.x = x;
		this.y = y;
		this.r = r;
		this.maxRadius = max;
	}

	// Pozisyon Güncelleme Fonksiyonu
	public boolean update () {
		r += 2;
		if (r >= maxRadius) return true;
		return false;
	}
	// Ekranda Gösterme Fonksiyonu
	public void draw (Graphics2D g) {
		g.drawImage(ex, (int) (x - r), (int) (y - r), 75, 75, null);
	}
}