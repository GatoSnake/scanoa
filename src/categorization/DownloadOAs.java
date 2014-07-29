package categorization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadOAs {

	public static boolean downloadOA(String url,String tempFile){
		System.out.println("Abriendo conexion ...");
		String directory = "C:\\Users\\Cristhian\\workspace\\scanoa\\tempFile\\";
		URL urls;
		boolean result = false;
		try {
			urls = new URL(url);
			InputStream in = urls.openStream();
			FileOutputStream fos = new FileOutputStream(new File(directory+tempFile));

			System.out.println("Leyendo archivo ...");
			int length = -1;
			byte[] buffer = new byte[1024];// buffer for portion of data from
			                                // connection
			while ((length = in.read(buffer)) > -1) {
			    fos.write(buffer, 0, length);
			}
			fos.close();
			in.close();
			System.out.println("Archivo descargado");
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
