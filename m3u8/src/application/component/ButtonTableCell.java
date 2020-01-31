package application.component;

import application.dto.EXTINF;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class ButtonTableCell extends TableCell<EXTINF, Button> {
	private String text;

	public ButtonTableCell(String text) {
		this.text = text;
	}

	@Override
	protected void updateItem(Button item, boolean empty) {

		super.updateItem(item, empty);
		Button button = new Button(text);
		if (empty) {
			// 如果此列为空默认不添加元素
			setText(null);
			setGraphic(null);
		} else {
			this.setGraphic(button);
		}
	}
}
