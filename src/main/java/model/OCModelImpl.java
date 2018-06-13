package model;


import presenter.OCPresenter;
import Utils.Hexadecimal;
import Excepciones.ErrorEjecucion;
import Excepciones.ErrorOCUNS;
import model.Analizadores.AnalizadorLexico;
import model.Analizadores.AnalizadorSintacticoySemantico;
import model.Archivos.Archivo;
import model.Archivos.ArchivoAbstractFactory;
import model.Ejecucion.Ejecucion;
import model.RepresentacionMemoria.Memoria;

public class OCModelImpl implements OCModel {
	private OCPresenter ocPresenter;
	private AnalizadorSintacticoySemantico analizadorSintactico;
	private ArchivoAbstractFactory creadorArchivo;
	private Ejecucion ejecucion;

		
	  public OCModelImpl(AnalizadorLexico analizadorLexico,AnalizadorSintacticoySemantico analizadorSintactico,
			  ArchivoAbstractFactory creadorArchivo, Ejecucion ejecucion) {
		  this.analizadorSintactico= analizadorSintactico;
		  this.creadorArchivo=creadorArchivo;
		  this.ejecucion=ejecucion;
	  }
	
	  public void setOCPresenter(OCPresenter ocPresenter) {
	    this.ocPresenter = ocPresenter;
	  }
	
	@Override
	public boolean compilaElArchivo(Archivo archivo, String DireccionInicio) {
		try {
			analizadorSintactico.compilar(archivo, DireccionInicio);
			this.updateMemoria();
			this.updateRegistros();
		} catch (ErrorOCUNS e) {
			mostrarMensaje(e.getMessage());
			return false;
		}
		return true;
	}
	@Override
	public ArchivoAbstractFactory getCreadorArchivo() {
		return creadorArchivo;
	}
	@Override
	public String obtenerCodigoCompilado() {
		Memoria memoria=analizadorSintactico.getMemoria();
		String codigoCompilado="";
		int pc=0,direccionInicio=memoria.getDireccionInicio();
		for(pc=direccionInicio;quedaCodigoPorCodificar(memoria,pc);pc=pc+2){
			codigoCompilado+=codificarPC(pc);
			codigoCompilado+=codificar2Bytes(memoria,pc);
			codigoCompilado+=codificarInstruccion(memoria,pc);
			codigoCompilado+="\n";
		}
		codigoCompilado+="\n";
		return codigoCompilado;
	}
	private String codificarInstruccion(Memoria memoria,int pc) {
		return "|| "+(mostrarInstruccion(memoria,pc));
	}
	private String mostrarInstruccion(Memoria memoria,int pc) {
		int opcode=memoria.leerMemoria(pc)/16;
		int registroD=memoria.leerMemoria(pc)%16;
		int direccion=memoria.leerMemoria(pc+1);
		int registroS=direccion/16,registroT_offset=direccion%16;
				
		String codigoFuente="";
		String opcodeCodificado=codificarOpcode(opcode);
		
		if(esTipoI(opcode))
			codigoFuente+=codificarTipoI(opcode,opcodeCodificado,registroD,registroS,registroT_offset);
		else if(esTipoII(opcode))
			codigoFuente+=codificarTipoII(opcodeCodificado,registroD,direccion,pc);
		else if(esTipoIII(opcode))
			codigoFuente+=codificarTipoIII(opcodeCodificado,registroD);
		return codigoFuente;
	}
	private String codificarOpcode(int opcode) {
		return analizadorSintactico.getReglas().getOpcode(opcode);
	}

	private boolean esTipoI(int opcode) {
		return opcode>=0 && opcode<=7;
	}
	private String codificarTipoI(int opcode,String opcodeCodificado, int registroD,
			int registroS, int registroT_offset) {
		if(esMemoriaOpcode(opcode))
			return codificarTipoIMemoria(opcode,registroD,registroS,registroT_offset);
		else 
			return codificarTipoIDefault(opcodeCodificado,registroD,registroS,registroT_offset);
	}
	private boolean esMemoriaOpcode(int opcode) {
		return opcode==6 || opcode==7;
	}

	private String codificarTipoIDefault(String opcodeCodificado, int registroD,
			int registroS, int registroT_offset) {
		String codificado=opcodeCodificado;
		codificado+=" R"+Hexadecimal.hex(registroD);
		codificado+=", R"+Hexadecimal.hex(registroS);
		codificado+=", R"+Hexadecimal.hex(registroT_offset);
		return codificado;
	}
	private String codificarTipoIMemoria(int opcode, int registroD,
			int registroS, int registroT_offset) {
		if(esLoad(opcode))
			return "load R"+Hexadecimal.hex(registroD)+", "+Hexadecimal.comp(registroT_offset)+"(R"+Hexadecimal.hex(registroS)+")";
		else
			return"store R"+Hexadecimal.hex(registroS)+", "+Hexadecimal.comp(registroT_offset)+"(R"+Hexadecimal.hex(registroD)+")";
	}
	private boolean esLoad(int opcode) {
		return opcode==0;
	}

	private boolean esTipoII(int opcode) {
		return opcode>=8 && opcode<=11;
	}	
	private String codificarTipoII(String opcodeCodificado, int registroD,
			int direccion,int pc) {
		String codificado=opcodeCodificado;
		codificado+=" R"+Hexadecimal.hex(registroD);
		codificado+=getDireccionOEtiqueta(direccion,pc);
		return codificado;
	}
	private String getDireccionOEtiqueta(int direccion,int pc) {
		String etiqueta=analizadorSintactico.getTablaEtiqueta().obtenerEtiqueta(pc+1);
		if(etiqueta==null)
			etiqueta=Hexadecimal.hex2Dig(direccion);
		return etiqueta;
	}

	private boolean esTipoIII(int opcode) {
		return opcode>=12 && opcode<=15;
	}
	private String codificarTipoIII(String opcodeCodificado, int registroD) {
		String codificado=opcodeCodificado;
		if(!esHalt(opcodeCodificado))
			codificado+=" R"+Hexadecimal.hex(registroD);
		return codificado;
	}
	private boolean esHalt(String opcodeCodificado) {
		return analizadorSintactico.getReglas().getIDSentencia(opcodeCodificado)==15;
	}

	private boolean quedaCodigoPorCodificar(Memoria memoria, int direccion){
		return direccion<memoria.getDireccionActual()-1;
	}
	private String codificarPC(int pc) {
		String codigoCompilado="";
		if(tiene1DigitoHexadecimal(pc))
			codigoCompilado+="0";
		codigoCompilado+=Hexadecimal.hex(pc)+"h. ";
		return codigoCompilado;
	}
	private boolean tiene1DigitoHexadecimal(int pc) {
		return pc<16;
	}
	private String codificar2Bytes(Memoria memoria, int pc) {
		return Hexadecimal.hex2Dig(memoria.leerMemoria(pc))+Hexadecimal.hex2Dig(memoria.leerMemoria(pc+1));
	}

	@Override
	public void mostrarMensaje(String mensaje) {
		ocPresenter.mostrarMensaje(mensaje);
	}
	@Override
	public String pedirDialogo(String pedido) {
		return ocPresenter.pedirDialogo(pedido);
	}

	@Override
	public void ejecutarCodigoCompleto() throws ErrorEjecucion {
		ejecucion.ejecutarCodigoCompleto();
	}

	@Override
	public void habilitarEjecucionPasoaPaso() throws ErrorEjecucion {
		ejecucion.iniciarEjecucionPasoaPaso();
	}

	@Override
	public void ejecutarSiguienteIntruccion() throws ErrorEjecucion {
		ejecucion.ejecutarSiguienteInstruccion();
	}

	@Override
	public boolean hayCodigoParaEjecutar() {
		return ejecucion.hayCodigoParaEjecutar();
	}

	@Override
	public void updateRegistros() {
		Memoria memoria=ejecucion.getMemoria();
		ocPresenter.updateRegistros(memoria.getUpdateRegistro());
	}

	@Override
	public void updateMemoria() {
		Memoria memoria=ejecucion.getMemoria();
		ocPresenter.updateMemoria(memoria.getUpdateMemoria());
	}
	@Override
	public void updatePCView(String pc) {
		ocPresenter.updatePCView(pc);
	}

	@Override
	public void updateInstrucionView(Memoria memoria,int pc) {
		String instruccion=mostrarInstruccion(memoria,pc);
		ocPresenter.updateInstrucionView(instruccion);
	}

	@Override
	public void updateLogs(String log) {
		ocPresenter.updateLogs(log);
	}
}
