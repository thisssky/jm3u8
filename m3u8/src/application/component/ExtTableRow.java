package application.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;

public class ExtTableRow<T> extends TableRow<T> {

	private ContextMenu contextMenu = new ContextMenu();
	private MenuItem deleteMenuItem = new MenuItem("删除");
	private MenuItem suspendMenuItem = new MenuItem("暂停");
	private MenuItem resumeMenuItem = new MenuItem("继续下载");

	public ExtTableRow() {

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
			}
		});
		resumeMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				suspendMenuItem.setDisable(false);
				resumeMenuItem.setDisable(true);
			}
		});

	}

}
