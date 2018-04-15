package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.model.FileDirectory;
import main.model.FileDirectoryWrapper;
import main.view.MainStageController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Main extends Application {

    private Stage primaryStage;
    private VBox rootLayout;
    private ObservableList<FileDirectory> fileData = FXCollections.observableArrayList();


    public Main() { }

    public ObservableList<FileDirectory> getFileData() {
        return fileData;
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("File Manager");
        this.primaryStage.getIcons().add(new Image("file:resources/images/ic_folder_shared_black_18dp.png"));

        initMainStage();
    }

    private void initMainStage()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("view/MainStageMaterialDesign.fxml"));
            rootLayout = fxmlLoader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            MainStageController controller = fxmlLoader.getController();
            controller.setMain(this);

            primaryStage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        File file = getFileDirectoryPath();
        if (file != null)
        {
            loadFileDataFromFile(file);
        }
    }

    public Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }

    public File getFileDirectoryPath()
    {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null)
        {
            return new File(filePath);
        }
        else
        {
            return null;
        }
    }

    public void setFileDirectoryPath(File file)
    {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null)
        {
            prefs.put("filePath", file.getPath());
        }
        else
        {
            prefs.remove("filePath");
        }
    }

    public void loadFileDataFromFile(File file)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(FileDirectoryWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            FileDirectoryWrapper wrapper = (FileDirectoryWrapper) unmarshaller.unmarshal(file);

            fileData.clear();
            fileData.addAll(wrapper.getFileDirectory());

            setFileDirectoryPath(file);
        }
        catch (JAXBException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public void saveFileDataToFile(File file)
    {
        try
        {
            JAXBContext context = JAXBContext.
                    newInstance(FileDirectoryWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            FileDirectoryWrapper wrapper = new FileDirectoryWrapper();
            wrapper.setFileDirectory(fileData);

            marshaller.marshal(wrapper, file);

            setFileDirectoryPath(file);
        }
        catch (JAXBException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}
