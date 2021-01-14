import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;


// Kolayl�k Sa�layacak Fonksiyonlar�n Oldu�u Class
public class Utilities {
	// Resim Dosyas� okuma Fonksiyonu
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