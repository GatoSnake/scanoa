package categorization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import beans.Util;

public class DownloadOAs {

	public static boolean downloadOA(String url,String tempFile){
		System.out.println("Abriendo conexion ...");
		try {
			URL urls = new URL(url);
			InputStream in = urls.openStream();
			FileOutputStream fos = new FileOutputStream(new File(Util.dir_oas+tempFile));
			System.out.println("Leyendo archivo ...");
			int length = -1;
			byte[] buffer = new byte[1024]; // buffer for portion of data from connection
			while ((length = in.read(buffer)) > -1) {
			    fos.write(buffer, 0, length);
			}
			fos.close();
			in.close();
			System.out.println("Archivo descargado");
		} catch (IOException ex) {
			System.out.println("Error en downloadOA() -->" + ex);
			return false;
		}
		return true;
	}
	
}
