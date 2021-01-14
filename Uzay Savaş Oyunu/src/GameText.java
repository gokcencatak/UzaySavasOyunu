import java.awt.*;

public class GameText {
	private double x;
	private double y;
	private long time, start;
	private String s;

	public GameText (double x, double y, long time, String s) {
		this.x = x;
		this.y = y;
		this.time = time;
		this.s = s;

		start = System.nanoTime();
	}
	// Durum Güncelleme Fonksiyonu
	public boolean update () {
		long elapsed = (System.nanoTime() - start) / 1000000;
		if (elapsed > time) 
			return true;
		return false;
	}
	
	// Ekranda Gösterme Fonksiyonu
	public void draw (Graphics2D g) {
		g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		long elapsed = (System.nanoTime() - start) / 1000000;
		g.setColor(new Color(255, 255, 255, 128));
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (int) (x - (length / 2)), (int) y);
	}
}