package infoKupStreamClient;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class ScreenRecord {

	private static final double FRAME_RATE = 50;

	private static final int SECONDS_TO_RUN_FOR = 20;

	private static final String outputFilename = "c:/mydesktop.mp4";

	private static Dimension screenSize;

	public static void main(String[] args) {

		final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
				screenSize.width / 2, screenSize.height / 2);

		long startTime = System.nanoTime();

		for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {

			BufferedImage screen = getDesktopScreenshot();

			BufferedImage bgrScreen = convertToType(screen,
					BufferedImage.TYPE_3BYTE_BGR);

			writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime,
					TimeUnit.NANOSECONDS);

			try {
				Thread.sleep((long) (1000 / FRAME_RATE));
			} catch (InterruptedException e) {

			}

		}

		writer.close();

	}

	public static BufferedImage convertToType(BufferedImage sourceImage,
			int targetType) {

		BufferedImage image;

		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}

		else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}

		return image;

	}

	private static BufferedImage getDesktopScreenshot() {
		try {
			Robot robot = new Robot();
			Rectangle captureSize = new Rectangle(screenSize);
			return robot.createScreenCapture(captureSize);
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}

	}

}
