package application.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CommonUtility {
	public static final String IMAGE_PATH = "/image/";

	public static Dimension getDimension() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = defaultToolkit.getScreenSize();
		return screenSize;
	}

	public static int getDimensionHeight() {
		Dimension dimension = getDimension();
		int height = dimension.height;
		return height;
	}

	public static int getDimensionWidth() {
		Dimension dimension = getDimension();
		int width = dimension.width;
		return width;
	}

	public static BufferedImage getBufferedImage(String url) {
		InputStream resource = CommonUtility.class.getResourceAsStream(IMAGE_PATH + url);
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bufferedImage;
	}

	public static ImageIcon getImageIcon(String url) {
		ImageIcon imageIcon = new ImageIcon(getBufferedImage(url));
		return imageIcon;
	}

	/**
	 * 
	 * @param url    图片路径
	 * @param width  返回图片宽度
	 * @param height 返回图片高度
	 */
	public static ImageIcon getImageIcon(String url, int width, int height) {
		ImageIcon imageIcon = new ImageIcon(getBufferedImage(url));
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT));// 可以用下面三句代码来代替
		return imageIcon;
	}

	public static Image getImage(String imageName) {
		InputStream resource = CommonUtility.class.getResourceAsStream(IMAGE_PATH + imageName);
		Image image = new Image(resource);
		return image;
	}

	public static void alert(String headerText) {
		Alert urlAlert = new Alert(AlertType.ERROR);
		Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
		Image image = getImage("title.png");
		window.getIcons().add(image);
		urlAlert.setHeaderText(headerText);
		urlAlert.setTitle("提示");
		urlAlert.show();
	}
}
