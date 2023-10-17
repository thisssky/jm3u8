package application.testexample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TableViewEx extends Application {
	private TableView<Student> tableView;
	private TableColumn<Student, String> indexColumn, firstNameColumn, lastNameColumn, deleteColumn;
	private TableColumn<Student, Integer> ageColumn;
	private TableColumn<Student, Boolean> adultColumn;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		tableView();
		Scene scene = new Scene(tableView, 506, 460);
		primaryStage.sizeToScene();
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public ObservableList<Student> items() {

		Student stu1 = new Student("赵", "哈", 16);
		Student stu2 = new Student("钱", "大", 26);
		Student stu3 = new Student("孙", "阿", 23);
		Student stu4 = new Student("李", "佛山", 17);
		Student stu5 = new Student("周", "阿萨德", 23);
		Student stu6 = new Student("吴", "更好", 12);
		Student stu7 = new Student("郑", "和", 28);
		Student stu8 = new Student("王", "费", 23);
		Student stu9 = new Student("刘", "的", 15);
		Student stu10 = new Student("关", "时是", 23);
		Student stu11 = new Student("张", "良好", 19);
		Student stu12 = new Student("诸葛", "列", 23);
		Student stu13 = new Student("司马", "咯跑", 20);

		ObservableList<Student> stuLists = FXCollections.observableArrayList(stu1, stu2, stu3, stu4, stu5, stu6, stu7,
				stu8, stu9, stu10, stu11, stu12, stu13);
		return stuLists;
	}

	/**
	 * 显示学生表格
	 *
	 * @param stuLists
	 */
	public void tableView() {
		tableView = new TableView<>();
		tableView.setEditable(true);
		indexColumn = new TableColumn<>("序号");
		indexColumn.setCellFactory((col) -> {
			TableCell<Student, String> cell = new TableCell<Student, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						int rowIndex = this.getIndex() + 1;
						this.setText(String.valueOf(rowIndex));
					}
				}
			};
			return cell;
		});

		firstNameColumn = new TableColumn<>("姓氏");
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("firstName"));

		lastNameColumn = new TableColumn<>("名字");
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));

		ageColumn = new TableColumn<>("年龄");
		ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
		ageColumn.setCellFactory((col) -> {
			TableCell<Student, Integer> cell = new TableCell<Student, Integer>() {

				@Override
				public void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						int age = this.getTableView().getItems().get(this.getIndex()).getAge();
						this.setText(String.valueOf(age));
						if (age < 18) {
							this.getStyleClass().add("mark");
						}
					}
				}

			};
			return cell;
		});

		adultColumn = new TableColumn<>("成年");
		adultColumn.setCellFactory((col) -> {
			TableCell<Student, Boolean> cell = new TableCell<Student, Boolean>() {

				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						CheckBox checkBox = new CheckBox();
						this.setGraphic(checkBox);
						checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
							if (newVal) {
								// 添加选中时执行的代码

								System.out.println("第" + this.getIndex() + "行被选中！");
								// 获取当前单元格的对象
							}

						});
					}
				}

			};
			return cell;
		});

		deleteColumn = new TableColumn<>("删除");
		deleteColumn.setCellFactory((col) -> {
			TableCell<Student, String> cell = new TableCell<Student, String>() {

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
//						ImageView delICON = new ImageView(getClass().getResource("delete.png").toString());
						Button delBtn = new Button("删除");
						this.setGraphic(delBtn);
						delBtn.setOnMouseClicked((me) -> {
							Student clickedStu = this.getTableView().getItems().get(this.getIndex());
							System.out.println("删除 " + clickedStu.getFirstName() + clickedStu.getLastName() + " 的记录");
						});
					}
				}

			};
			return cell;
		});
		tableView.getColumns().addAll(indexColumn, firstNameColumn, lastNameColumn, ageColumn, deleteColumn,
				adultColumn);
		tableView.setItems(items());
	}

	public static class Student {
		private String firstName;
		private String lastName;
		private int age;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Student(String firstName, String lastName, int age) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.age = age;
		}
	}

}