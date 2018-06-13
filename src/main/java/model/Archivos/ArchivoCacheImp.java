package model.Archivos;

import Excepciones.ErrorOCUNS;

public class ArchivoCacheImp implements Archivo {

	private String fileName;
	private String lineas[];
	private int numeroLinea;
	public ArchivoCacheImp(String fileName,String contenido){
		this.fileName=fileName;
		this.lineas=contenido.split("\n"); 
		this.numeroLinea=0;
	}
	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public String readLine() throws ErrorOCUNS {
		String lineaActual=null;
		if(noSeTerminoElArchivo())
			lineaActual=lineas[numeroLinea];
		numeroLinea++;
		return lineaActual;
	}

	private boolean noSeTerminoElArchivo() {
		return numeroLinea<lineas.length;
	}
	@Override
	public void Close() {
		lineas=null;
	}

}
