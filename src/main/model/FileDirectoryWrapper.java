package main.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "repositories")
public class FileDirectoryWrapper
{
    private List<FileDirectory> file;
    private List<Directory> directory;

    @XmlElement(name = "file")
    public List<FileDirectory> getFileDirectory() {
        return file;
    }

    public void setFileDirectory(List<FileDirectory> file) {
        this.file = file;
    }

    @XmlElement(name = "directory")
    public List<Directory> getDirectory() { return directory; }

    public void setDirectory(List<Directory> directory) { this.directory = directory; }
}
