package model.RepresentacionMemoria;

import java.util.Hashtable;

import Excepciones.ErrorSintactico;

public class TabladeEtiquetasImpl implements TabladeEtiquetas {

	protected Hashtable<String,Integer> etiquetas;
	protected Hashtable<Integer,String> pendiente;
	
	public TabladeEtiquetasImpl(){
		iniciar();
	}
	@Override
	public void iniciar() {
		etiquetas=new Hashtable<String,Integer> ();
		pendiente= new Hashtable<Integer,String>();
	}
	@Override
	public void cargarEtiquetaPendiente(int direccion, String etiqueta) {
		pendiente.put(direccion, etiqueta);
	}
	@Override
	public void cargarDirecciondeEtiqueta(String etiqueta, int direccion) {
		etiquetas.put(etiqueta,direccion);
	}
	@Override
	public void remplazarEtiquetas(Memoria memoria) throws ErrorSintactico {
		for(Integer i: pendiente.keySet()){
			int direccionTarget=getDireccionTarget(i);
			int opcode= getOpcode(memoria,i);
			if(!esDireccionAbsoluta(opcode))
				direccionTarget=getDesplazamiento(direccionTarget,i);
			memoria.escribirMemoria(i, direccionTarget);
		}
	}
	private int getOpcode(Memoria memoria,int direccion){
		return memoria.leerMemoria(direccion-1)/16;
	}
	private Integer getDireccionTarget(int direccion) throws ErrorSintactico{
		Integer direccionTarget=etiquetas.get(pendiente.get(direccion));
		if(direccionTarget==null)
			throw new ErrorSintactico("ErrorSintactico = La etiqueta \'"+pendiente.get(direccion)+"\' no definida");
		return direccionTarget.intValue();
	}
	private boolean esDireccionAbsoluta(int opcode){
		return opcode==8 || opcode==11;
	}
	private int getDesplazamiento(int direccionTarget, Integer direccion) {
		int direccionActual,desplazamiento;
		direccionActual=direccion+1;
		desplazamiento=direccionTarget-direccionActual;
		if(desplazamiento<0)
			desplazamiento=256+desplazamiento;		
		return desplazamiento;
	}
	@Override
	public String obtenerEtiqueta(int direccion) {
		return pendiente.get(direccion);
	}
}
