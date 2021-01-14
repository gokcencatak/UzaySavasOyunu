import javax.swing.JFrame;

public class MainGame {
	public static void main(String[] args) {
		JFrame container = new JFrame("SpaceWar");

		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setContentPane(new GameWindow());

		// Pencere Kenarlýklarýný ve Butonlarýný Gizle
		container.setUndecorated(true);

		container.setResizable(false);
		container.pack();
		container.setLocationRelativeTo(null);
		container.setVisible(true);
	}
}