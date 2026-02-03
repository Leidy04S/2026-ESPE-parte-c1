package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileTest {
	
	private File file;
	
	@BeforeEach
	public void setUp() {
		file = new File();
	}
	
	// ========== Constructor Tests ==========
	
	@Test
	public void testConstructorInitializesEmptyContent() {
		List<Character> content = file.getContent();
		assertNotNull(content, "Content should not be null");
		assertTrue(content.isEmpty(), "Content should be empty after construction");
		assertTrue(content instanceof ArrayList, "Content should be ArrayList");
	}
	
	@Test
	public void testConstructorInitializesTypeAsNull() {
		// We cannot directly test type without a getter, but we can verify through behavior
		// Type should be null, so addProperty should work without throwing WrongFileTypeException
		char[] content = {'K', '=', 'V'};
		assertDoesNotThrow(() -> file.addProperty(content), 
			"addProperty should not throw when type is null");
	}
	
	// ========== addProperty Tests - Normal Cases ==========
	
	@Test
	public void testAddPropertyToPropertyFile() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] newContent = {'D', 'A', 'T', 'E', '=', '2', '0', '2', '5', '0', '9', '1', '9'};
		
		file.addProperty(newContent);
		
		List<Character> content = file.getContent();
		assertEquals(newContent.length, content.size(), "Content size should match added characters");
		for (int i = 0; i < newContent.length; i++) {
			assertEquals(newContent[i], content.get(i), "Character at index " + i + " should match");
		}
	}
	
	@Test
	public void testAddPropertyMultipleTimes() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] firstContent = {'K', 'E', 'Y', '='};
		char[] secondContent = {'V', 'A', 'L', 'U', 'E'};
		
		file.addProperty(firstContent);
		file.addProperty(secondContent);
		
		List<Character> content = file.getContent();
		assertEquals(firstContent.length + secondContent.length, content.size(),
			"Content should accumulate from multiple addProperty calls");
		
		// Verify first content
		for (int i = 0; i < firstContent.length; i++) {
			assertEquals(firstContent[i], content.get(i));
		}
		// Verify second content
		for (int i = 0; i < secondContent.length; i++) {
			assertEquals(secondContent[i], content.get(firstContent.length + i));
		}
	}
	
	@Test
	public void testAddPropertyWithMultipleCharacters() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] newContent = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
		
		file.addProperty(newContent);
		
		assertEquals(newContent.length, file.getContent().size());
	}
	
	// ========== addProperty Tests - Error Cases ==========
	
	@Test
	public void testAddPropertyWithNullContentThrowsException() {
		file.setType(FileType.PROPERTY);
		
		assertThrows(InvalidContentException.class, () -> file.addProperty(null),
			"addProperty should throw InvalidContentException when newcontent is null");
	}
	
	@Test
	public void testAddPropertyToImageFileThrowsException() {
		file.setType(FileType.IMAGE);
		char[] newContent = {'s', 'o', 'm', 'e', 'c', 'o', 'n', 't', 'e', 'n', 't'};
		
		assertThrows(WrongFileTypeException.class, () -> file.addProperty(newContent),
			"addProperty should throw WrongFileTypeException when file type is IMAGE");
	}
	
	// ========== addProperty Tests - Boundary Cases ==========
	
	@Test
	public void testAddPropertyWithEmptyArray() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] emptyArray = {};
		
		// Should not throw exception
		file.addProperty(emptyArray);
		
		assertTrue(file.getContent().isEmpty(), "Content should remain empty after adding empty array");
	}
	
	@Test
	public void testAddPropertyWithSingleCharacter() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] singleChar = {'X'};
		
		file.addProperty(singleChar);
		
		assertEquals(1, file.getContent().size());
		assertEquals('X', file.getContent().get(0));
	}
	
	@Test
	public void testAddPropertyWithUnicodeCharacters() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] unicodeContent = {'\u00E1', '\u00E9', '\u00ED', '\u00F3', '\u00FA'}; // áéíóú
		
		file.addProperty(unicodeContent);
		
		assertEquals(unicodeContent.length, file.getContent().size());
		for (int i = 0; i < unicodeContent.length; i++) {
			assertEquals(unicodeContent[i], file.getContent().get(i));
		}
	}
	
	// ========== getCRC32 Tests - Normal Cases ==========
	
	@Test
	public void testGetCRC32WithContent() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] content = {'T', 'E', 'S', 'T'};
		file.addProperty(content);
		
		long crc = file.getCRC32();
		
		assertNotEquals(0L, crc, "CRC32 of non-empty content should not be 0");
		assertTrue(crc > 0, "CRC32 should be positive");
	}
	
	@Test
	public void testGetCRC32Deterministic() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] content = {'D', 'A', 'T', 'E', '=', '2', '0', '2', '5'};
		file.addProperty(content);
		
		long crc1 = file.getCRC32();
		long crc2 = file.getCRC32();
		
		assertEquals(crc1, crc2, "CRC32 should be deterministic for same content");
	}
	
	// ========== getCRC32 Tests - Boundary Cases ==========
	
	@Test
	public void testGetCRC32WithEmptyContent() {
		long crc = file.getCRC32();
		
		assertEquals(0L, crc, "getCRC32 should return 0 when content is empty");
	}
	
	@Test
	public void testGetCRC32WithSingleCharacter() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] singleChar = {'A'};
		file.addProperty(singleChar);
		
		long crc = file.getCRC32();
		
		assertNotEquals(0L, crc, "CRC32 of single character should not be 0");
	}
	
	@Test
	public void testGetCRC32WithHighByteValues() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		// Characters with high byte values (> 127)
		char[] highByteContent = {'\u00FF', '\u00FE', '\u00FD'};
		file.addProperty(highByteContent);
		
		long crc = file.getCRC32();
		
		assertNotEquals(0L, crc, "CRC32 should handle high byte values correctly");
		assertTrue(crc > 0, "CRC32 should be positive");
	}
	
	@Test
	public void testGetCRC32MatchesExpectedValue() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] content = {'T', 'E', 'S', 'T'};
		file.addProperty(content);
		
		// Calculate expected CRC32
		byte[] bytes = new byte[content.length];
		for (int i = 0; i < content.length; i++) {
			bytes[i] = (byte) (content[i] & 0xFF);
		}
		CRC32 expectedCrc = new CRC32();
		expectedCrc.update(bytes);
		long expectedValue = expectedCrc.getValue();
		
		long actualCrc = file.getCRC32();
		
		assertEquals(expectedValue, actualCrc, "CRC32 should match calculated value");
	}
	
	// ========== setType and getContent Tests ==========
	
	@Test
	public void testSetType() {
		file.setType(FileType.PROPERTY);
		// We verify indirectly: if type was set correctly, addProperty should work
		char[] content = {'A'};
		assertDoesNotThrow(() -> file.addProperty(content));
		
		file.setType(FileType.IMAGE);
		// Now addProperty should throw
		assertThrows(WrongFileTypeException.class, () -> file.addProperty(content));
	}
	
	@Test
	public void testGetContent() throws InvalidContentException, WrongFileTypeException {
		file.setType(FileType.PROPERTY);
		char[] content = {'H', 'E', 'L', 'L', 'O'};
		file.addProperty(content);
		
		List<Character> retrievedContent = file.getContent();
		
		assertEquals(content.length, retrievedContent.size());
		for (int i = 0; i < content.length; i++) {
			assertEquals(content[i], retrievedContent.get(i));
		}
	}
	
	@Test
	public void testGetContentReturnsCorrectListInstance() {
		List<Character> content = file.getContent();
		
		assertNotNull(content, "getContent should not return null");
		assertTrue(content instanceof ArrayList, "getContent should return ArrayList");
	}

}
