package categorization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class TextExtractionOffice {
	static String directory = "C:\\Users\\Cristhian\\workspace\\scanoa\\tempFile\\";
	static String tempFileDOC = "temp.doc";
	static String tempFileDOCX = "temp.docx";
	
	public static String readDoc(){
		String texto = null;
        try {
        	POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(directory+tempFileDOC));
            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor extractor = new WordExtractor(doc);
            texto = extractor.getText();
            extractor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return texto;
    }
	
	public static String readDocx(){
		String texto = null;
    	try {
			FileInputStream fs = new FileInputStream(directory+tempFileDOCX);
		    XWPFDocument hdoc=new XWPFDocument(OPCPackage.open(fs));
		    XWPFWordExtractor extractor = new XWPFWordExtractor(hdoc);
		    texto = extractor.getText();
		    extractor.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return texto;
	}
	
}
