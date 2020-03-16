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

					System.out.println(2);

					ContextMenu contextMenu = new ContextMenu();
					MenuItem deleteMenuItem = new MenuItem("删除");
					MenuItem deleteMenuItem2 = new MenuItem("删除");
					MenuItem deleteMenuItem3 = new MenuItem("删除");
					deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							System.out.println("删除index:" + getIndex());
							getTableView().getItems().remove(getIndex());

						}
					});
					contextMenu.getItems().add(deleteMenuItem);
//					contextMenu.getItems().add(deleteMenuItem2);
//					contextMenu.getItems().add(deleteMenuItem3);
					Window window2 = getScene().getWindow();

//					contextMenu.show((Node) event.getTarget(), event.getSceneX() + window2.getX(),
//							event.getSceneY() + window2.getY());
					contextMenu.show(window2, event.getSceneX() + window2.getX() + 8,
							event.getSceneY() + window2.getY() + 30);
				}
			}
		});
	}
}
