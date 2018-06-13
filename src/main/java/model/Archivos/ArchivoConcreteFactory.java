package model.Archivos;

import java.io.File;

import Excepciones.ErrorOCUNS;

public class ArchivoConcreteFactory implements ArchivoAbstractFactory {

	@Override
	public Archivo crearArchivo(File file) throws ErrorOCUNS {
		return new ArchivoImp(file);
	}

	@Override
	public Archivo crearArchivo(String filename) throws ErrorOCUNS {
		return new ArchivoImp(filename);
	}

	@Override
	public String cat(String fileName) throws ErrorOCUNS {
		Archivo archivo= new ArchivoImp(fileName);
		String linea="",retorno="";
		while((linea=archivo.readLine())!=null)
			retorno+="\n"+linea;
		archivo.Close();
		return retorno;
	}

	@Override
	public Archivo crearArchivo(String filename, String contenido){
		return new ArchivoCacheImp(filename,contenido);
	}
	
}
