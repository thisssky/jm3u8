package application.component;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import application.dto.TableItem;
import application.utils.CommonUtility;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class DirTableCell extends TableCell<TableItem, String> {

	public DirTableCell() {
		Tooltip tooltip = new Tooltip("双击打开文件夹");
		tooltip.setFont(Font.font(16));
		setTooltip(tooltip);
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
					TableItem item = (TableItem) getTableRow().getItem();
					if (null != item) {
						File file = new File(item.getDir());
						if (!file.isDirectory()) {
							CommonUtility.alert("文件夹不存在!", AlertType.ERROR);
						} else {
							Runnable runnable = new Runnable() {
								
								@Override
								public void run() {
									try {
										Desktop.getDesktop().open(file);
									} catch (IOException e) {
										e.printStackTrace();
									}
									
								}
							};
							new Thread(runnable).start();
						}
					}
				}

			}
		});
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		this.setText(item);
		this.setAlignment(Pos.CENTER);
	}

}
