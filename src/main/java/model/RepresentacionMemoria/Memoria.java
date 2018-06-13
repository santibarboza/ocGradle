package model.RepresentacionMemoria;

import java.util.Map;

import Excepciones.ErrorEjecucion;

public interface Memoria {
	public void escribirSiguienteByte(int valor) throws ErrorEjecucion;
	public int leerMemoria(int direccion);
	public void escribirMemoria(int direccion, int valor);
	public int leerRegistro(int numero);
	public void escribirRegistro(int numero, int valor);
	public void resetearRegistros();
	public void resetearMemoria();
	public void resetearDireccionActual();
	public int getDireccionInicio();
	public int getDireccionActual();
	public void iniciar(int direccionInicio);
	public Map<Integer,String> getUpdateRegistro();
	public Map<Integer,String> getUpdateMemoria();
	public void resetUpdateRegistro();
	public void resetUpdateMemoria();
}
