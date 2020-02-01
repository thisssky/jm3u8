package application.dto;

import application.component.ProgressBarBox;
import application.utils.CommonUtility;
import application.utils.FFMPEG;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TableItem {

	private String m3u8;
	private String dir;

	private CheckBox mergeCheckBox;
	private Button mergeButton;
	private ProgressBarBox progressBarBox;

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

	public CheckBox getMergeCheckBox() {
		return mergeCheckBox;
	}

	public void setMergeCheckBox(CheckBox mergeCheckBox) {
		this.mergeCheckBox = mergeCheckBox;
	}

	public Button getMergeButton() {
		return mergeButton;
	}

	public void setMergeButton(Button mergeButton) {
		this.mergeButton = mergeButton;
	}

	public ProgressBarBox getProgressBarBox() {
		return progressBarBox;
	}

	public void setProgressBarBox(ProgressBarBox progressBarBox) {
		this.progressBarBox = progressBarBox;
	}

	public TableItem(String m3u8, String dir) {
		this.m3u8 = m3u8;
		this.dir = dir;

		this.progressBarBox = new ProgressBarBox(m3u8, dir);

		this.mergeCheckBox = new CheckBox();
		this.mergeCheckBox.setIndeterminate(false);
		this.mergeCheckBox.setSelected(false);
		this.mergeCheckBox.setDisable(true);

		this.mergeButton = new Button("合并");
		this.mergeButton.setOnAction(new EventHandler<ActionEvent>() {

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
				FFMPEG.merge(dir);
				mergeCheckBox.setIndeterminate(false);
				mergeCheckBox.setSelected(true);

			}
		});
	}

}
