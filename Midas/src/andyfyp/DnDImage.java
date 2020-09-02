package andyfyp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
//import org.openide.util.Exceptions;

public class DnDImage extends ImageIcon {
	private static final int SIZE = 150;

	int patternId;
	String url;

	public DnDImage(String _url, int _patternId) {
		super();
		url = _url;
		patternId = _patternId;

		BufferedImage image;
		try {
			image = ImageIO.read(new File(url));
			image = resizeImage(image, 280, SIZE);
			// image = resizeImage(image, 100, 280);
			// image = resizeImage(image, 50, 50);
			this.setImage(image);
		} catch (IOException ex) {
			System.out.println("ERROR:");
			ex.printStackTrace();
		}
	}

	/*------------------------------Utilites----------------------------------*/
	private BufferedImage resizeImage(BufferedImage image, int width, int height) {
		if (image.getWidth() == width && image.getHeight() == height) {
			return image;
		}
		try {
			int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(image, 0, 0, width, height, null);
			g.dispose();

			File outputFile = new File(url);
			ImageIO.write(resizedImage, "png", outputFile);

			return resizedImage;
		} catch (IOException ex) {
			// Exceptions.printStackTrace(ex);
		}

		return image;
	}
}// end outer class
