package main.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class FileDirectory extends RecursiveTreeObject<FileDirectory>
{
    @XmlElement
    public StringProperty name;
    @XmlElement
    public StringProperty category;
    @XmlElement
    public StringProperty comment;
    @XmlElement
    public StringProperty path;
    public List<FileDirectory> list;

    public FileDirectory(TreeItem<FileDirectory> repository) { this(null, null, null, null); }

    public FileDirectory(String name, String category, String comment)
    {
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.comment = new SimpleStringProperty(comment);
    }

    public FileDirectory(String name, String category, String comment, String path) {
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.comment = new SimpleStringProperty(comment);
        this.path = new SimpleStringProperty(path);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getComment() {
        return comment.get();
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public List<FileDirectory> getList() {
        return list;
    }

    public void setList(List<FileDirectory> list) {
        this.list = list;
    }
}
