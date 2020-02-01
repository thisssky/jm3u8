package application.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import application.component.ProgressBarBox;
import application.utils.CommonUtility;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

@XmlRootElement(name = "extinf")
@XmlAccessorType(XmlAccessType.FIELD)
public class EXTINF {
	@XmlElement
	private String dir;
	@XmlElement
	private String m3u8;// http://xxx/xxx/xxx.m3u8
	@XmlElement
	private int index;
	@XmlElement
	private String url;// http://xxxx/xxxx/xxx/9660a92c9d8000003.ts
	@XmlElement
	private String name;// 9660a92c9d8000003.ts

	private CheckBox mergeCheckBox = new CheckBox();
	private Button mergeButton = new Button("合并");
	private ProgressBarBox progressTableCell = new ProgressBarBox("", "");

	public ProgressBarBox getProgressTableCell() {
		return progressTableCell;
	}

	public void setProgressTableCell(ProgressBarBox progressTableCell) {
		this.progressTableCell = progressTableCell;
	}

	public Button getMergeButton() {
		return mergeButton;
	}

	public void setMergeButton(Button mergeButton) {
		this.mergeButton = mergeButton;
	}

	public CheckBox getMergeCheckBox() {
		return mergeCheckBox;
	}

	public void setMergeCheckBox(CheckBox mergeCheckBox) {
		this.mergeCheckBox = mergeCheckBox;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getM3u8() {
		return m3u8;
	}

	public void setM3u8(String m3u8) {
		this.m3u8 = m3u8;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EXTINF() {

		this(0, null, null);

	}

	public EXTINF(int index, String url, String name) {
		this.index = index;
		this.url = url;
		this.name = name;
		mergeCheckBox.setIndeterminate(false);
		mergeCheckBox.setSelected(false);
		mergeCheckBox.setDisable(true);
		mergeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (mergeCheckBox.isSelected()) {
					Alert alert = new Alert(AlertType.INFORMATION);
					Stage window = (Stage) alert.getDialogPane().getScene().getWindow();
					Image image = CommonUtility.getImage("title.png");
					window.getIcons().add(image);
					alert.setTitle("提示");
					alert.setHeaderText("已合并!");
					alert.show();

				}
				// 合并操作
				// TODO
				mergeCheckBox.setIndeterminate(false);
				mergeCheckBox.setSelected(true);

			}
		});
	}
}
