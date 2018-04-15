package main.view;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.Main;
import main.model.FileDirectory;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static main.view.TableModel.*;

@SuppressWarnings("unchecked")
public class MainStageController implements Initializable
{
    private Main main;

    @FXML
    private JFXTreeTableView<FileDirectory> fileTreeTableView;
    @FXML
    private JFXTreeTableView<FileDirectory> repositoryTableView;
    @FXML
    private JFXTextField input;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        TreeItem<FileDirectory> rootNode = getModel();
        rootNode.setExpanded(true);

        fileTreeTableView.getColumns().addAll(
                getNameColumn(),
                getCategoryColumn(),
                getCommentColumn());

        fileTreeTableView.setEditable(true);
        fileTreeTableView.setRoot(rootNode);
        fileTreeTableView.setShowRoot(false);
        fileTreeTableView.getSelectionModel().selectFirst();

        input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                fileTreeTableView.setPredicate(new Predicate<TreeItem<FileDirectory>>() {
                    @Override
                    public boolean test(TreeItem<FileDirectory> fileDirectory) {
                        boolean flag = fileDirectory.getValue().name.getValue().contains(newValue);
                        return flag;
                    }
                });
            }
        });

        TreeItem<FileDirectory> repNode = getModel();
        repNode.setExpanded(true);

        repositoryTableView.getColumns().add(getRepColumn());
        repositoryTableView.setRoot(repNode);
        repositoryTableView.setShowRoot(true);
        repositoryTableView.getSelectionModel().selectFirst();

        repositoryTableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2)
                {
                    TreeItem<FileDirectory> item = fileTreeTableView.getSelectionModel().getSelectedItem();
                    FileDirectory rep = repositoryTableView.getSelectionModel().getSelectedItem().getValue();

                    File repository = new File(rep.getPath());

                    if (item != null)
                    {
                        TreeItem<FileDirectory> newRepository = new TreeItem(new FileDirectory(rep.getName(), rep.getCategory(),
                                                                                rep.getComment(), rep.getPath()));

                        JFXTreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = fileTreeTableView.getSelectionModel();

                        int rowIndex = sm.getSelectedIndex();

                        TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

                        selectedItem.getChildren().addAll(newRepository);

                        selectedItem.setExpanded(true);

                        recursionRepository(repository, newRepository, rep);
                    }
                }
            }
        });
    }

    private void recursionRepository(File repository, TreeItem<FileDirectory> newRepository, FileDirectory fileDirectory)
    {
        for (File item : repository.listFiles())
        {
            if (item.isDirectory())
            {
                TreeItem<FileDirectory> newDirectory = new TreeItem<>(new FileDirectory(item.getName(), fileDirectory.getCategory(),
                        fileDirectory.getComment(), item.getPath()));

                newRepository.getChildren().add(newDirectory);

                newDirectory.setExpanded(true);

                recursionRepository(item, newDirectory, fileDirectory);
            }
            else
            {
                TreeItem<FileDirectory> newFile = new TreeItem<>(new FileDirectory(item.getName(), fileDirectory.getCategory(),
                        fileDirectory.getComment(), item.getPath()));

                newRepository.getChildren().add(newFile);
            }
        }
    }

    public void setMain(Main main)
    {
        this.main = main;
    }

    @FXML
    private void handleCreateRepository()
    {
        FileDirectory selectedDirectory = fileTreeTableView.getSelectionModel().getSelectedItem().getValue();
        String path = selectedDirectory.getPath();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose directory");
        File dest = fileChooser.showSaveDialog(main.getPrimaryStage());
        String stringDest = dest.getPath();

        if (dest != null)
        {
            recDownloadRepository(path, stringDest);
        }
    }

    private void recDownloadRepository(String directory, String dest)
    {
        File dir = new File(directory);
        File des = new File(dest);
        FileDirectory item = fileTreeTableView.getSelectionModel().getSelectedItem().getValue();

        des.mkdir();

        TreeItem<FileDirectory> repository = new TreeItem<>(new FileDirectory(item.getName(), item.getCategory(), item.getComment(),
                                                            des.getPath()));

        JFXTreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = repositoryTableView.getSelectionModel();

        int rowIndex = sm.getSelectedIndex();

        TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

        selectedItem.getChildren().add(repository);

        main.getFileData().add(new FileDirectory(repository));

        selectedItem.setExpanded(false);

        File nextDirFile;
        String nextDirFilename, nextDesFilename;

        for (String filename : dir.list())
        {
            nextDirFilename = dir.getAbsolutePath() + File.separator + filename;
            nextDesFilename = des.getAbsolutePath() + File.separator + filename;
            nextDirFile = new File(nextDirFilename);

            if (nextDirFile.isDirectory())
            {
                recDownloadDirectory(nextDirFilename, nextDesFilename);
            }
            else
            {
                copyFile(nextDirFilename, nextDesFilename);
            }
        }
    }

    @FXML
    private void handleRemoveRepository()
    {
        FileDirectory rep = repositoryTableView.getSelectionModel().getSelectedItem().getValue();
        File repository = new File(rep.getPath());

        JFXTreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = repositoryTableView.getSelectionModel();
        int rowIndex = sm.getSelectedIndex();
        TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

        TreeItem<FileDirectory> parent = selectedItem.getParent();

        if (sm == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No selection");
            alert.setHeaderText("No repository selected");
            alert.setContentText("Please select the deleted repository");

            alert.showAndWait();
        }

        for (String s : repository.list())
        {
            File currentFile = new File(repository.getPath(), s);
            currentFile.delete();
        }

        if (parent != null)
        {
            if (repository.delete())
            {
                parent.getChildren().remove(selectedItem);
            }
            else
            {
                System.out.println("We have a problem");
            }
        }
    }

    @FXML
    private void handleNewDirectory()
    {
        if (fileTreeTableView.getExpandedItemCount() == 0)
        {
            addNewRootItem();
        }
        else if (fileTreeTableView.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No selection");
            alert.setHeaderText("No parent catalog selected");
            alert.setContentText("Please, select parent catalog for created file");

            alert.showAndWait();
        }
        else
        {
            addNewDirectory();
        }
    }

    private void addNewDirectory()
    {
        TreeItem<FileDirectory> newDirectory = new TreeItem<>(new FileDirectory("New", "New", "New"));

        TreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = fileTreeTableView.getSelectionModel();

        int rowIndex = sm.getSelectedIndex();

        TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

        selectedItem.getChildren().add(newDirectory);

        selectedItem.setExpanded(true);

        this.editNewItem(newDirectory);
    }

    private void addNewRootItem()
    {
        TreeItem<FileDirectory> item = new TreeItem<>(new FileDirectory("New", "New", "New"));
        fileTreeTableView.setRoot(item);
        fileTreeTableView.setShowRoot(true);

        this.editNewItem(item);
    }

    private void editNewItem(TreeItem<FileDirectory> item)
    {
        int newRowIndex = fileTreeTableView.getRow(item);
        fileTreeTableView.scrollTo(newRowIndex);

        TreeTableColumn<FileDirectory, ?> firstCol = fileTreeTableView.getColumns().get(0);
        fileTreeTableView.getSelectionModel().select(item);
        fileTreeTableView.getFocusModel().focus(newRowIndex, firstCol);
        fileTreeTableView.edit(newRowIndex, firstCol);
    }

    @FXML
    private void handleOpenFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        List<File> file = fileChooser.showOpenMultipleDialog(main.getPrimaryStage());

        if (file != null)
        {
            for (File files : file)
            {
                TreeItem<FileDirectory> openFile = new TreeItem<>(new FileDirectory(files.getName(), "", "", files.getPath()));

                TreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = fileTreeTableView.getSelectionModel();

                int rowIndex = sm.getSelectedIndex();

                TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

                selectedItem.getChildren().add(openFile);

                selectedItem.setExpanded(true);
            }
        }
    }

    @FXML
    private void handleOpenDirectory()
    {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Directory");
        File directory = directoryChooser.showDialog(main.getPrimaryStage());

        if (directory != null)
        {
            TreeItem<FileDirectory> openDirectory = new TreeItem<>(new FileDirectory(directory.getName(), "", "", directory.getPath()));

            TreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = fileTreeTableView.getSelectionModel();

            int rowIndex = sm.getSelectedIndex();

            TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

            selectedItem.getChildren().add(openDirectory);

            selectedItem.setExpanded(true);

            recursionDirectory(directory, openDirectory);
        }
    }

    private void recursionDirectory(File directory, TreeItem<FileDirectory> openDirectory)
    {
        for (File item : directory.listFiles())
        {
            if (item.isDirectory())
            {
                TreeItem<FileDirectory> newDirectory = new TreeItem<>(new FileDirectory(item.getName(), "", "", item.getPath()));

                openDirectory.getChildren().add(newDirectory);

                openDirectory.setExpanded(true);

                recursionDirectory(item, newDirectory);
            }
            else
            {
                TreeItem<FileDirectory> newFile = new TreeItem<>(new FileDirectory(item.getName(), "", "", item.getPath()));

                openDirectory.getChildren().add(newFile);
            }
        }
    }

    @FXML
    private void handleDownloadFile()
    {
        FileDirectory selectedFile = fileTreeTableView.getSelectionModel().getSelectedItem().getValue();

        String path = selectedFile.getPath();

        FileChooser fileChooser= new FileChooser();
        fileChooser.setTitle("Choose directory");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3"),
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Doc Files", "*.doc", "*.docs")
        );
        File dest = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (dest != null)
        {
            try(FileInputStream fin = new FileInputStream(path);
                FileOutputStream fos = new FileOutputStream(dest))
            {
                byte[] buffer = new byte[fin.available()];

                fin.read(buffer, 0, buffer.length);

                fos.write(buffer, 0, buffer.length);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDownloadDirectory()
    {
        FileDirectory selectedFilePath = fileTreeTableView.getSelectionModel().getSelectedItem().getValue();

        String path = selectedFilePath.getPath();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose directory");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File dest = fileChooser.showSaveDialog(main.getPrimaryStage());
        String stringDest = dest.getPath();

        if (dest != null)
        {
            recDownloadDirectory(path, stringDest);
        }
    }

    private void recDownloadDirectory(String directory, String dest)
    {
        File dir = new File(directory);
        File des = new File(dest);

        des.mkdir();
        File nextDirFile;
        String nextDirFilename, nextDesFilename;

        for (String filename : dir.list())
        {
            nextDirFilename = dir.getAbsolutePath() + File.separator + filename;
            nextDesFilename = des.getAbsolutePath() + File.separator + filename;
            nextDirFile = new File(nextDirFilename);

            if (nextDirFile.isDirectory())
            {
                recDownloadDirectory(nextDirFilename, nextDesFilename);
            }
            else
            {
                copyFile(nextDirFilename, nextDesFilename);
            }
        }
    }

    private void copyFile(String directory, String dest)
    {
        File dir = new File(directory);
        File des = new File(dest);

        try(InputStream fin = new FileInputStream(dir);
            OutputStream fos = new FileOutputStream(des))
        {
            byte[] buffer = new byte[fin.available()];

            int bytes;

            while ((bytes = fin.read(buffer)) > 0)
            {
                fos.write(buffer, 0, bytes);
            }

            fos.close();
            fin.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemove()
    {
        TreeTableView.TreeTableViewSelectionModel<FileDirectory> sm = fileTreeTableView.getSelectionModel();

        if (sm == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No selection");
            alert.setHeaderText("No file selected");
            alert.setContentText("Please select the deleted file");

            alert.showAndWait();
        }

        int rowIndex = sm.getSelectedIndex();
        TreeItem<FileDirectory> selectedItem = sm.getModelItem(rowIndex);

        TreeItem<FileDirectory> parent = selectedItem.getParent();

        if (parent != null)
        {
            parent.getChildren().remove(selectedItem);
        }
        else
        {
            fileTreeTableView.setRoot(null);
        }
    }

    @FXML
    private void handleMenuNewRepositoryList()
    {
        main.getFileData().clear();
        main.setFileDirectoryPath(null);
    }

    @FXML
    private void handleMenuOpenRepositoryList()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML files (.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showOpenDialog(main.getPrimaryStage());

        if (file != null)
        {
            main.loadFileDataFromFile(file);
        }
    }

    @FXML
    private void handleMenuSaveRepositoryList()
    {
        File repositoryFile = main.getFileDirectoryPath();

        if (repositoryFile != null)
        {
            main.saveFileDataToFile(repositoryFile);
        }
        else
        {
            handleMenuSaveAs();
        }
    }

    @FXML
    private void handleMenuSaveAs()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML files (.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null)
        {
            if (!file.getPath().endsWith(".xml"))
            {
                file = new File(file.getPath() + ".xml");
            }
            main.saveFileDataToFile(file);
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Manager");
        alert.setHeaderText("About");
        alert.setContentText("Author: Alexander Turchanovskiy");

        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}


