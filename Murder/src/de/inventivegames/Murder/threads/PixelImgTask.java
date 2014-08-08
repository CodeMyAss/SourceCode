package de.inventivegames.murder.threads;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;

import de.inventivegames.murder.Spectate;
import de.inventivegames.utils.pixelimage.ImageChar;
import de.inventivegames.utils.pixelimage.PixelImage;

public class PixelImgTask extends Thread {

	public PixelImgTask(Player p) {
		this.p = p;
	}

	private final Player	p;

	@Override
	public void run() {
		String[] lines = new String[8];
		if (Spectate.faces.containsKey(p)) {
			lines = Spectate.faces.get(p);
		}

		BufferedImage img = null;
		try {
			final URL url = new URL("https://minotar.net/avatar/" + p.getName() + "/32");
			final URLConnection conn = url.openConnection();
			conn.setConnectTimeout(500);
			final InputStream is = conn.getInputStream();
			img = ImageIO.read(is);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			final PixelImage pimg = new PixelImage(img, 8, ImageChar.BLOCK.getChar());

			Spectate.faces.put(p, pimg.getLines());

			lines = pimg.getLines();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		Spectate.faces.put(p, lines);
	}

}
