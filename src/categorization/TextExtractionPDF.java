package categorization;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

public class TextExtractionPDF {
	
	public static void getInformationFile(String directoryFile) throws IOException{
		PDDocument archivo = PDDocument.load(directoryFile);
		PDDocumentInformation informacion = archivo.getDocumentInformation();
		System.out.println("-------- INFORMACION DEL DOCUMENTO --------");
		System.out.println(informacion.getTitle());
		System.out.println(informacion.getCreator());
		System.out.println(informacion.getCreationDate());
		System.out.println(informacion.getModificationDate());
		System.out.println(informacion.getMetadataKeys());
		System.out.println(informacion.getAuthor());
		System.out.println(informacion.getKeywords());
		System.out.println(informacion.getTrapped());
		System.out.println(archivo.getNumberOfPages());
		System.out.println("-------------------------------------------");
		archivo.close();
	}
	
	public static String extractionTextPDF() throws IOException{
//		System.out.println("en metodo --> extractionTextPDF()");
		String directory = "C:\\Users\\Cristhian\\workspace\\scanoa\\tempFile\\";
		String tempFilePDF = "temp.pdf";
		
		PDFTextStripper stripper;
		PDDocument doc = null;
		String texto = null;
		try{
		  doc = PDDocument.load(directory+tempFilePDF);
		  stripper = new PDFTextStripper();
		  stripper.setStartPage( 1 );
		  stripper.setEndPage( Integer.MAX_VALUE );
		  texto = stripper.getText(doc);
		  return texto;
		}finally{
		   if(doc!=null)
		   doc.close();
		}  
	}
	
}
