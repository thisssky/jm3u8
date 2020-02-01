package application.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import application.component.ProgressBarBox;
import application.utils.CommonUtility;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@XmlRootElement(name = "extinf")
@XmlAccessorType(XmlAccessType.FIELD)
public class EXTINF {
	@XmlElement
	private String m3u8;// http://xxx/xxx/xxx.m3u8
	@XmlElement
	private String dir;
	@XmlElement
	private int index;
	@XmlElement
	private String tsName;// 9660a92c9d8000003.ts
	@XmlElement
	private String ts;// http://xxxx/xxxx/xxx/9660a92c9d8000003.ts

//	private CheckBox mergeCheckBox;
//	private Button mergeButton;
//	private ProgressBarBox progressBarBox;

	public String getM3u8() {
		return m3u8;
	}

	public void setM3u8(String m3u8) {
		this.m3u8 = m3u8;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTsName() {
		return tsName;
	}

	public void setTsName(String tsName) {
		this.tsName = tsName;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

//	public CheckBox getMergeCheckBox() {
//		return mergeCheckBox;
//	}
//
//	public void setMergeCheckBox(CheckBox mergeCheckBox) {
//		this.mergeCheckBox = mergeCheckBox;
//	}
//
//	public Button getMergeButton() {
//		return mergeButton;
//	}
//
//	public void setMergeButton(Button mergeButton) {
//		this.mergeButton = mergeButton;
//	}
//
//	public ProgressBarBox getProgressBarBox() {
//		return progressBarBox;
//	}
//
//	public void setProgressBarBox(ProgressBarBox progressBarBox) {
//		this.progressBarBox = progressBarBox;
//	}

	public EXTINF() {
	}

	public EXTINF(String m3u8, String dir, int index) {
		this.index = index;
		this.m3u8 = m3u8;
		this.dir = dir;
	}

//	public void after() {
//		this.mergeCheckBox = new CheckBox();
//		this.mergeButton = new Button("合并");
//		this.progressBarBox = new ProgressBarBox("", "");
//		this.mergeCheckBox.setIndeterminate(false);
//		this.mergeCheckBox.setSelected(false);
//		this.mergeCheckBox.setDisable(true);
//		this.mergeButton.setOnAction(new EventHandler<ActionEvent>() {
//
//			@Override
//			public void handle(ActionEvent event) {
//				if (mergeCheckBox.isSelected()) {
//					Alert alert = new Alert(AlertType.INFORMATION);
//					Stage window = (Stage) alert.getDialogPane().getScene().getWindow();
//					Image image = CommonUtility.getImage("title.png");
//					window.getIcons().add(image);
//					alert.setTitle("提示");
//					alert.setHeaderText("已合并!");
//					alert.show();
//
//				}
//				// 合并操作
//				// TODO
//				mergeCheckBox.setIndeterminate(false);
//				mergeCheckBox.setSelected(true);
//
//			}
//		});
//	}

}
