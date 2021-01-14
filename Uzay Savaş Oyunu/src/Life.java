import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;

public class Life {
	private double x, y;
	private int r;
	private BufferedImage imgLife;

	public Life (double x, double y) {
		this.x = x;
		this.y = y;
		r = 3;
		if (imgLife == null) imgLife = new Utilities().loadImg("/images/life.png");
	}

	// Getter Fonksiyonlarý
	public double getX () {return x;}
	public double getY () {return y;}
	public double getR () {return r;}

	// Ekran Pozisyonunu Güncelle
	public boolean update () {
		y += 2;
		if (y > GameWindow.height + r) return true;
		return false;
	}
	public void draw (Graphics2D g) {
		g.drawImage(imgLife, (int) (x - r), (int) (y - r), null);
	}
}