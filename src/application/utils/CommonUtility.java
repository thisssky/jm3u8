package application.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.ImageIcon;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CommonUtility {
	public static final String IMAGE_PATH = "/image/";
//	private static SSLContext sc;
	private static SSLSocketFactory socketFactory;

	static {
		try {
//			SSLContext sc = SSLContext.getInstance("SSL", "SunJSSE");
			SSLContext sc = SSLContext.getInstance("SSL");
			X509TrustManager x509TrustManager = new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

				}
			};
			sc.init(null, new TrustManager[] { x509TrustManager }, new java.security.SecureRandom());
			socketFactory = sc.getSocketFactory();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

	}

	public static SSLSocketFactory getSSLSocketFactory() {
		return socketFactory;
	}

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

	public static void alert(String headerText, AlertType alertType) {
		Alert urlAlert = new Alert(alertType);
		Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
		Image image = getImage("title.png");
		window.getIcons().add(image);
		urlAlert.setHeaderText(headerText);
		urlAlert.setTitle("提示");
		urlAlert.show();
	}

	public static boolean confirm(AlertType alertType, String headerText) {
		Alert alert = new Alert(alertType);
		Stage window = (Stage) alert.getDialogPane().getScene().getWindow();
		Image image = getImage("title.png");
		window.getIcons().add(image);
		alert.setHeaderText(headerText);
		alert.setTitle("提示");
		Optional<ButtonType> condition = alert.showAndWait();
		if (condition.get().getButtonData().equals(ButtonData.OK_DONE)) {
			return true;
		} else {
			return false;
		}
	}

}
