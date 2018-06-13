package model.RepresentacionMemoria;

import java.util.Map;

import Utils.Hexadecimal;

import model.Mapeo.MapFactory;

import Excepciones.ErrorEjecucion;

public class MemoriaImpl implements Memoria{
	protected int memoria[];
	protected int registro[];
	protected int direccionInicio;
	protected int direccionActual;
	protected MapFactory<Integer,String> mapFactory;
	protected Map<Integer,String> updateRegistros;
	protected Map<Integer,String> updateMemoria;

	public MemoriaImpl(MapFactory<Integer,String> mapFactory){
		this.mapFactory=mapFactory;
		direccionInicio=0;
		direccionActual=0;
		memoria= new int[256];
		registro= new int[16];
		crearMaps();
	}
	@Override
	public void iniciar(int direccionInicio) {
		this.direccionInicio=direccionInicio;
		this.direccionActual=direccionInicio;
		memoria= new int[256];
		registro= new int[16];
		crearMaps();
	}
	private void crearMaps(){
		updateRegistros=mapFactory.createMap();
		updateMemoria=mapFactory.createMap();
	}
	@Override
	public void escribirSiguienteByte(int valor) throws ErrorEjecucion {
		if(direccionActual>255)
			throw new ErrorEjecucion("Codigo alocado fuera de la memoria");
		direccionActual=direccionActual & 255;
		memoria[direccionActual]=valor;
		updateMemoria.put(direccionActual, Hexadecimal.hex2Dig(valor));
		direccionActual++;
	}
	@Override
	public int leerMemoria(int direccion) {
		return memoria[direccion];
	}
	@Override
	public void escribirMemoria(int direccion, int valor) {
		memoria[direccion]=valor;
		updateMemoria.put(direccion, Hexadecimal.hex2Dig(valor));
	}
	@Override
	public int leerRegistro(int numero) {
		return registro[numero];
	}
	@Override
	public void escribirRegistro(int numero, int valor) {
		registro[numero]=valor;
		updateRegistros.put(numero, cargarContenidoRegistro(valor));
	}
	@Override
	public void resetearRegistros() {
		registro= new int[16];
		for(int i=0;i<16;i++)
			registro[i]=0;
		resetUpdateRegistro();
	}
	@Override
	public void resetearDireccionActual() {
		direccionActual=direccionInicio;		
	}
	@Override
	public int getDireccionInicio() {
		return direccionInicio;
	}
	@Override
	public int getDireccionActual() {
		return direccionActual;
	}
	public Map<Integer,String> getUpdateRegistro(){
		return updateRegistros;
	}
	public Map<Integer,String> getUpdateMemoria(){
		return updateMemoria;
	}
	public void resetUpdateRegistro(){
		updateRegistros.clear();
	}
	public void resetUpdateMemoria(){
		updateMemoria.clear();
		
	}
	@Override
	public void resetearMemoria() {
		memoria= new int[256];
		for(int i=0;i<256;i++)
			registro[i]=0;
		resetUpdateMemoria();
	}
	private String cargarContenidoRegistro(int valor) {
		String contenido="";
		if(tieneUnDigitoHexa(valor))
			contenido+="0";
		contenido+=Hexadecimal.hex(valor);
		return contenido;
	}	
	private boolean tieneUnDigitoHexa(int valor) {
		return valor<16;
	}
}
