package application.dto;

import application.component.pane.DownloadColumnPane;
import application.component.pane.MergeColumnPane;
import application.utils.Common;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TableItem {

	private String m3u8;
	private String dir;

	private CheckBox mergeCheckBox;
	private Button mergeButton;
	private DownloadColumnPane downloadColumn;
	private MergeColumnPane mergeColumn;

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

	public DownloadColumnPane getDownloadColumn() {
		return downloadColumn;
	}

	public void setDownloadColumn(DownloadColumnPane downloadColumn) {
		this.downloadColumn = downloadColumn;
	}

	public MergeColumnPane getMergeColumn() {
		return mergeColumn;
	}

	public void setMergeColumn(MergeColumnPane mergeColumn) {
		this.mergeColumn = mergeColumn;
	}

	public TableItem(String m3u8, String dir) {
		this.m3u8 = m3u8;
		this.dir = dir;

		this.downloadColumn = new DownloadColumnPane(m3u8, dir);
		this.mergeColumn = new MergeColumnPane(dir);

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
					Image image = Common.getImage("title.png");
					window.getIcons().add(image);
					alert.setTitle("提示");
					alert.setHeaderText("已合并!");
					alert.show();

				} else {
					// 合并操作
					mergeColumn.merge();
					mergeCheckBox.setIndeterminate(false);
					mergeCheckBox.setSelected(true);
				}

			}
		});
	}

}
