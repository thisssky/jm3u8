package application.remain;

import application.dto.EXTINF;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Deprecated
public class ButtonTableCell extends TableCell<EXTINF, Button> {
	private String buttonText;
	private Button button;
	private int columnIndex;
	private boolean isLastVisibleColumn;
	private TableView<EXTINF> tableView;
	private TableColumn<EXTINF, Button> tableColumn;

	public ButtonTableCell(TableView<EXTINF> tableView, TableColumn<EXTINF, Button> tableColumn, String text) {
		this.tableView = tableView;
		this.tableColumn = tableColumn;
		this.buttonText = text;
		this.button = new Button(text);
		this.button.setText(text);
		System.out.println(text);

//		TableView<EXTINF> tableView = getTableView();
//		TableColumn<EXTINF, Button> tableColumn = getTableColumn();
//		System.out.println(tableView + "," + tableColumn);
		columnIndex = tableView == null || tableColumn == null ? -1 : tableView.getVisibleLeafIndex(tableColumn);

		// update the pseudo class state regarding whether this is the last
		// visible cell (i.e. the right-most).
		isLastVisibleColumn = tableColumn != null && columnIndex != -1
				&& columnIndex == tableView.getVisibleLeafColumns().size() - 1;

//		System.out.println(columnIndex + "," + isLastVisibleColumn);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("=============================================");
				ObservableList<EXTINF> items = ButtonTableCell.this.tableView.getItems();
				int index = getIndex();
				System.out.println(index);
				Button cellData = tableColumn.getCellData(index);
				System.out.println(cellData);
//				System.out.println(cellData.getText());
			}
		});
	}

	@Override
	protected void updateItem(Button item, boolean empty) {

		super.updateItem(item, empty);
		if (empty) {
			// 如果此列为空默认不添加元素
			setText(null);
			setGraphic(null);
		} else {
			this.setGraphic(button);
		}
	}
}
