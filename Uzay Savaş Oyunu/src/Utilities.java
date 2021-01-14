import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;


// Kolaylýk Saðlayacak Fonksiyonlarýn Olduðu Class
public class Utilities {
	// Resim Dosyasý okuma Fonksiyonu
	public BufferedImage loadImg (String urlName) {
		try {
			URL url = getClass().getResource(urlName);
			BufferedImage img = ImageIO.read(url);
			return img;
		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			return null;
		}
	}
}