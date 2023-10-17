package application.component;

import application.dto.TableItem;
import application.utils.CommonUtility;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;

public class ContextMenuTableRow extends TableRow<TableItem> {
	private ContextMenu contextMenu = new ContextMenu();
	private MenuItem suspendMenuItem = new MenuItem("暂停");
	private MenuItem resumeMenuItem = new MenuItem("继续下载");
	private MenuItem removeMenuItem = new MenuItem("移除");

	public ContextMenuTableRow() {

		super();
		resumeMenuItem.setDisable(true);
		contextMenu.getItems().add(suspendMenuItem);
		contextMenu.getItems().add(resumeMenuItem);
		contextMenu.getItems().add(removeMenuItem);
		setContextMenu(contextMenu);

		suspendMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				resumeMenuItem.setDisable(false);
				suspendMenuItem.setDisable(true);
				TableItem tableItem = getTableView().getItems().get(getIndex());
				tableItem.getDownloadColumn().suspend();

			}
		});
		resumeMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				suspendMenuItem.setDisable(false);
				resumeMenuItem.setDisable(true);
				TableItem tableItem = getTableView().getItems().get(getIndex());
				tableItem.getDownloadColumn().resume();
			}
		});
		removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				boolean confirm = CommonUtility.confirm(AlertType.CONFIRMATION, "确定移除任务吗?");
				if (confirm) {
					ObservableList<TableItem> items = getTableView().getItems();
					TableItem tableItem = items.get(getIndex());
					items.remove(getIndex());
					tableItem.getDownloadColumn().suspend();
				}
			}
		});
	}

}
