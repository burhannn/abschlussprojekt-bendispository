package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
@Data
public class UploadFile {

	private String fileName;
	@Lob
	private byte[] data;

	public UploadFile() {
	}

	public UploadFile(String fileName, byte[] data) {
		this.data = data;
		this.fileName = fileName;
	}
}
