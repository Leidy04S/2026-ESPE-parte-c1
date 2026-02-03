package es.upm.grise.profundizacion.file;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class File {

    private FileType type;
    private List<Character> content;

	/*
	 * Constructor
	 */
    public File() {
        this.content = new ArrayList<>();
        this.type = null;
    }

	/*
	 * Method to code / test
	 */
    public void addProperty(char[] newcontent) throws InvalidContentException, WrongFileTypeException {
        
        if (newcontent == null) {
            throw new InvalidContentException("Content cannot be null");
        }
        
        if (this.type == FileType.IMAGE) {
            throw new WrongFileTypeException("Cannot add property to an IMAGE file");
        }
        
        for (char c : newcontent) {
            this.content.add(c);
        }
    }

	/*
	 * Method to code / test
	 */
    public long getCRC32() {
    	
        if (this.content.isEmpty()) {
            return 0L;
        }
        
        // Convert ArrayList<Character> to byte[]
        byte[] bytes = new byte[this.content.size()];
        for (int i = 0; i < this.content.size(); i++) {
            // Use only the least significant byte (values in range [0, 255])
            bytes[i] = (byte) (this.content.get(i) & 0xFF);
        }
        
        // Calculate CRC32 using Java's built-in CRC32 class
        CRC32 crc = new CRC32();
        crc.update(bytes);
        
        return crc.getValue();
    }
    
    
	/*
	 * Setters/getters
	 */
    public void setType(FileType type) {
    	
    	this.type = type;
    	
    }
    
    public List<Character> getContent() {
    	
    	return content;
    	
    }
    
}
