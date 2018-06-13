package model.Archivos;

import java.io.File;

import Excepciones.ErrorOCUNS;

public interface ArchivoAbstractFactory {
	public Archivo crearArchivo(File file) throws ErrorOCUNS;
	public Archivo crearArchivo(String filename) throws ErrorOCUNS;
	public Archivo crearArchivo(String filename,String contenido) throws ErrorOCUNS;
	public String cat(String fileName) throws ErrorOCUNS;
}
