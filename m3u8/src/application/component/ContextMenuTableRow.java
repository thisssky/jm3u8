package application.component;

import application.dto.TableItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;

public class ContextMenuTableRow extends TableRow<TableItem> {
	private ContextMenu contextMenu = new ContextMenu();
	private MenuItem deleteMenuItem = new MenuItem("删除");
	private MenuItem suspendMenuItem = new MenuItem("暂停");
	private MenuItem resumeMenuItem = new MenuItem("继续下载");

	public ContextMenuTableRow() {

		super();
		resumeMenuItem.setDisable(true);
		contextMenu.getItems().add(suspendMenuItem);
		contextMenu.getItems().add(resumeMenuItem);
		contextMenu.getItems().add(deleteMenuItem);
		setContextMenu(contextMenu);

		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("删除index:" + getIndex());
				getTableView().getItems().remove(getIndex());

			}
		});
		suspendMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				resumeMenuItem.setDisable(false);
				suspendMenuItem.setDisable(true);
				TableItem tableItem = getTableView().getItems().get(getIndex());
				tableItem.getDownloadColumn().supend();

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

	}

}
