package application.component;

import application.dto.TableItem;
import javafx.event.EventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ExtTableRow extends TableRow<TableItem> {
	public ExtTableRow() {
		super();
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2
						&& ExtTableRow.this.getIndex() < ExtTableRow.this.getTableView().getItems().size()) {
					// doSomething
					TableItem t = ExtTableRow.this.getTableView().getItems().get(ExtTableRow.this.getIndex());

				}
			}
		});
	}
}