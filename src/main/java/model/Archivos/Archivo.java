package model.Archivos;

public interface Archivo {
	public String getName();
	public String readLine()throws Excepciones.ErrorOCUNS; 
	public void Close();
}
