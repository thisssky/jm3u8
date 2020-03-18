package application.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Window;

public class ExtTableRow<T> extends TableRow<T> {

	public ExtTableRow() {

		super();
		this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {

				if (getIndex() < getTableView().getItems().size()) {

					ContextMenu contextMenu = new ContextMenu();
					MenuItem deleteMenuItem = new MenuItem("删除");
					contextMenu.getItems().add(deleteMenuItem);
					MenuItem suspendMenuItem = new MenuItem("暂停");
					contextMenu.getItems().add(suspendMenuItem);
					MenuItem resumeMenuItem = new MenuItem("继续下载");
					contextMenu.getItems().add(resumeMenuItem);
					deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							System.out.println("删除index:" + getIndex());
							getTableView().getItems().remove(getIndex());

						}
					});
					Window window = getScene().getWindow();
					contextMenu.show(window, event.getSceneX() + window.getX() + 8,
							event.getSceneY() + window.getY() + 30);
				}
			}
		});
	}
}
