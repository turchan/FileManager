package main.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "repositories")
public class FileDirectoryWrapper
{
    private List<FileDirectory> fileDirectory;

    @XmlElement(name = "repository")
    public List<FileDirectory> getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(List<FileDirectory> fileDirectory) {
        this.fileDirectory = fileDirectory;
    }
}
