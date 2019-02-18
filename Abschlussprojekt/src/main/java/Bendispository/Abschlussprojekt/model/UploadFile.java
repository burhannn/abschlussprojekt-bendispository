package Bendispository.Abschlussprojekt.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Embeddable
@Data
public class UploadFile {

    private String fileName;
    @Lob
    private byte[] data;
    public UploadFile(){}

    public UploadFile(String fileName, byte[] data){
        this.data = data;
        this.fileName = fileName;
    }
}
