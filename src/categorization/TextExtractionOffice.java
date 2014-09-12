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

import beans.Util;

public class TextExtractionOffice {
	
	public static String readDoc(String path){
		System.out.println("en readDoc");
		String texto = null;
        try {
        	POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(Util.dir_oas+path));
            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor extractor = new WordExtractor(doc);
            texto = extractor.getText();
            extractor.close();
        } catch (Exception ex) {
        	System.out.println("Error en readDoc() -->" + ex);
        	return null;
        }
		return texto;
    }
	
	public static String readDocx(String path){
		String texto = null;
    	try {
			FileInputStream fs = new FileInputStream(Util.dir_oas+path);
		    XWPFDocument hdoc=new XWPFDocument(OPCPackage.open(fs));
		    XWPFWordExtractor extractor = new XWPFWordExtractor(hdoc);
		    texto = extractor.getText();
		    extractor.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error en readDocx() -->" + ex);
			return null;
		} catch (InvalidFormatException ex) {
			System.out.println("Error en readDocx() -->" + ex);
			return null;
		} catch (IOException ex) {
			System.out.println("Error en readDocx() -->" + ex);
			return null;
		}
    	return texto;
	}
	
}
