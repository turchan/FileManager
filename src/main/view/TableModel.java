package main.view;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import main.model.FileDirectory;

import java.io.Serializable;

@SuppressWarnings("unchecked")
public class TableModel
{
    public static TreeItem<FileDirectory> getModel()
    {
        FileDirectory fileDirectoryRoot = new FileDirectory("New", "New", "New");

        TreeItem<FileDirectory> root = new TreeItem<>(fileDirectoryRoot);

        return root;
    }

    public static TreeTableColumn<FileDirectory, String> getNameColumn()
    {
        TreeTableColumn<FileDirectory, String> nameCol = new TreeTableColumn<>("Name");
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameCol.setPrefWidth(225);
        nameCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        return nameCol;
    }
    public static TreeTableColumn<FileDirectory, String> getCategoryColumn()
    {
        TreeTableColumn<FileDirectory, String> categoryCol = new TreeTableColumn<>("Category");
        categoryCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(150);
        categoryCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        return categoryCol;
    }
    public static TreeTableColumn<FileDirectory, String> getCommentColumn()
    {
        TreeTableColumn<FileDirectory, String> commentCol = new TreeTableColumn<>("Comment");
        commentCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("comment"));
        commentCol.setPrefWidth(320);
        commentCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        return commentCol;
    }

    public static TreeTableColumn<FileDirectory, String> getRepColumn()
    {
        TreeTableColumn<FileDirectory, String> repCol = new TreeTableColumn<>("Repository");
        repCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        repCol.setPrefWidth(262);
        return repCol;
    }
}
