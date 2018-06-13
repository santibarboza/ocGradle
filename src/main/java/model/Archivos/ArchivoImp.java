package model.Archivos;

	import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Excepciones.ErrorOCUNS;
	
	public class ArchivoImp implements Archivo{
	
		protected String fileName;
		protected FileReader fileReader;
		protected BufferedReader bufferedReader;
		
		public ArchivoImp(String fileName) throws ErrorOCUNS{
			this.fileName=fileName;
			File archivo = new File (fileName);
			initArchivo(archivo);
		}
		public ArchivoImp(File file) throws ErrorOCUNS{
			this.fileName=file.getAbsolutePath();
			initArchivo(file);
		}
		private void initArchivo(File archivo)throws ErrorOCUNS {
			try{
				fileReader= new FileReader (archivo);
				bufferedReader = new BufferedReader(fileReader);
			}catch(FileNotFoundException e){
				throw new ErrorOCUNS("Error al abrir el Archivo");
			}
		}
		public String readLine() throws ErrorOCUNS {
			String retorno="";
			try{
				retorno= bufferedReader.readLine();
			}catch(IOException e){
				throw new ErrorOCUNS("Error al intentar leer el archivo");
			}
			return retorno;
		}
		public void Close(){
			try {
				bufferedReader.close();
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public String getName() {
			return fileName;
		}
	}

