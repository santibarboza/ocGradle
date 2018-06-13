package model.Analizadores;

import java.util.Hashtable;

import model.Archivos.Archivo;
import model.RepresentacionMemoria.Memoria;
import model.RepresentacionMemoria.RepresentacionDeLaMemoria;
import model.RepresentacionMemoria.TabladeEtiquetas;
import model.Tokens.Token;
import Excepciones.ErrorEjecucion;
import Excepciones.ErrorOCUNS;
import Excepciones.ErrorSintactico;

public class AnalizadorSintacticoySemanticoImpl implements AnalizadorSintacticoySemantico {
	protected AnalizadorLexico analizadorLexico;
	protected TabladeEtiquetas tablaDeEtiquetas;
	protected Memoria memoria;
	protected Token tokenActual;
	
	protected Hashtable<String,String> explicacionDeSimbolos;
	
	public AnalizadorSintacticoySemanticoImpl
				(AnalizadorLexico analizadorLexico,RepresentacionDeLaMemoria representacion){
		this.analizadorLexico=analizadorLexico;
		this.tablaDeEtiquetas=representacion.getTablaDeEtiquetas();
		this.memoria=representacion.getMemoria();
		cargarExplicacionDeSimbolos();
	}
	private void cargarExplicacionDeSimbolos() {
		explicacionDeSimbolos= new Hashtable<String,String>();
		try{
			explicacionDeSimbolos.put("T_ParenIni","un (");
			explicacionDeSimbolos.put("T_ParenFin","un )");
			explicacionDeSimbolos.put("T_Puntos","un : para terminar una etiqueta");
			explicacionDeSimbolos.put("T_Coma","una ,");
			explicacionDeSimbolos.put("T_Salto","un \\n");
			explicacionDeSimbolos.put("Lit_Desp","un Desplazamiento");
			explicacionDeSimbolos.put("Lit_Dir","una Direccion");
			explicacionDeSimbolos.put("Id_Reg","un Registro");
			explicacionDeSimbolos.put("Id_Etiq","una Etiqueta");
			explicacionDeSimbolos.put("EOF","el fin de Archivo");
		}catch(NullPointerException e){}
	}
	public void compilar(Archivo archivo,String direccionInicio)throws ErrorOCUNS {
		iniciarMemoria(archivo,obtenerDireccion(direccionInicio));
		this.tokenActual=analizadorLexico.getToken();
		inicial();
		tablaDeEtiquetas.remplazarEtiquetas(memoria);
	}
	private void iniciarMemoria(Archivo archivo,int direccionInicio) throws ErrorOCUNS {
		memoria.iniciar(direccionInicio);
		tablaDeEtiquetas.iniciar();
		analizadorLexico.iniciarConArchivo(archivo);
	}
	/**
	 * <Inicial> → <Sentencias>  EOF
	 */
	private void inicial() throws ErrorOCUNS {
		try{		
			Sentencias();
			match("EOF");
		}catch(ArrayIndexOutOfBoundsException e){
			throw new ErrorEjecucion("La direccion de ensamblado es muy grande,no se puede cargar el programa a partir de esa direccion");
		}
	}
	
	/**
	*   <Sentencias> → λ | <EtiquetaOLam><Sentencia> <Sentencias>
	 * @throws ErrorEjecucion 
	*/
	public void Sentencias()throws ErrorOCUNS{
		if(esIgual("Id_Etiq") || esSentencia()){
			EtiquetaOLam();
			Sentencia();
			Sentencias();
		}else if(!esIgual("EOF"))
			throw new ErrorSintactico(Error("Fin de Archivo o inicio de Sentencia"));
	}
	private boolean esSentencia() {
		return esIgual("T_SentenciaOperacion") ||esIgual("T_SentenciaMemoria")||esIgual("T_SentenciaAddress")|esIgual("T_SentenciaT3") ||esIgual("T_Halt")||esIgual("T_Salto");
	}

	/**
	 * <Sentencia>   → <SentenciaOperacion>  idReg, idReg, idReg
	 * <Sentencia>   → <SentenciaMemoria>  idReg, literalDesplazamiento (idReg) 
	 * <Sentencia>   → <SentenciaAddress>  idReg, <DirOEtiq>
	 * <Sentencia>   → <SentenciaT3>  idReg B | hlt | \n
	 * @throws ErrorEjecucion 
	 */
	private void Sentencia() throws ErrorOCUNS{
		int opcode = 0,registroS,registroT,offset,registroD;

		if(!esIgual("T_Salto"))
			opcode=tokenActual.get_Lexema();

		if(esIgual("T_SentenciaOperacion")){
			match("T_SentenciaOperacion");
				registroD=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				registroS=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				registroT=tokenActual.get_Lexema();
			match("Id_Reg");
				memoria.escribirSiguienteByte(opcode*16+registroD);
				memoria.escribirSiguienteByte(registroS*16+registroT);
		}else if(esIgual("T_SentenciaMemoria")){
			boolean esLoad=tokenActual.get_Lexema()==6;
			match("T_SentenciaMemoria"); 
			if(esLoad){
					registroD=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_Coma");
					offset=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					registroS=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");
			}else{
					registroS=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_Coma");
					offset=tokenActual.get_Lexema();
				match("Lit_Desp");
				match("T_ParenIni");
					registroD=tokenActual.get_Lexema();
				match("Id_Reg");
				match("T_ParenFin");;
			}
			memoria.escribirSiguienteByte(opcode*16+registroD);
			memoria.escribirSiguienteByte(registroS*16+offset);
		}else if(esIgual("T_SentenciaAddress")){
			match("T_SentenciaAddress");
				registroD=tokenActual.get_Lexema();
			match("Id_Reg");
			match("T_Coma");
				memoria.escribirSiguienteByte(opcode*16+registroD);
			dirOEtiq();
		}else if(esIgual("T_SentenciaT3")){
			match("T_SentenciaT3");
				registroD=tokenActual.get_Lexema();
			match("Id_Reg");
			memoria.escribirSiguienteByte(opcode*16+registroD);
			memoria.escribirSiguienteByte(0);		
		}else if(esIgual("T_Halt")){
				match("T_Halt");
					memoria.escribirSiguienteByte(opcode*16);
					memoria.escribirSiguienteByte(0);
		}else if(esIgual("T_Salto"))
				match("T_Salto");
		else
			throw new ErrorSintactico(Error("Inicio de Sentencia"));
	}
	
	private void dirOEtiq() throws ErrorOCUNS{
		if(esIgual("Lit_Dir")){
			memoria.escribirSiguienteByte(tokenActual.get_Lexema());
			match("Lit_Dir");
		}
		else if(esIgual("Id_Etiq")){
			tablaDeEtiquetas.cargarEtiquetaPendiente(memoria.getDireccionActual(), tokenActual.get_Etiqueta());
			match("Id_Etiq");
			memoria.escribirSiguienteByte(0);
		}
		else
			throw new ErrorSintactico(Error("Direccion o Etiqueta"));
		
	}
	/**
	 *	λ | etiqueta :  
	 */
	private void EtiquetaOLam() throws ErrorOCUNS{
		if(esIgual("Id_Etiq")){
			tablaDeEtiquetas.cargarDirecciondeEtiqueta(tokenActual.get_Etiqueta(), memoria.getDireccionActual());
			match("Id_Etiq");
			match("T_Puntos");
		}else if(!esSentencia())
			throw new ErrorSintactico(Error("inicio de Sentencia"));
		
	}
	private void match(String ID_T)throws ErrorOCUNS{
		String posibles=explicacionDeSimbolos.get(ID_T);
		if(esIgual(ID_T)){
			tokenActual=analizadorLexico.getToken();
		}else
			throw new ErrorSintactico(Error(posibles));
	}
	private String Error(String esperado){
		return"Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")= Se esperaba "+esperado+" y se encontro un "+explicacionDeSimbolos.get(tokenActual.get_IDTOKEN())+" "+tokenActual.get_Lexema();
	}
	private boolean esIgual(String txt){
		return tokenActual.get_IDTOKEN().equals(txt);
	}
	@Override
	public Memoria getMemoria() {
		return memoria;
	}

	@Override
	public TabladeEtiquetas getTablaEtiqueta() {
		return tablaDeEtiquetas;
	}
	private int obtenerDireccion(String dirIni) throws ErrorEjecucion {
		int dirInicio;
		try{
			dirInicio=Integer.parseInt(dirIni, 16);
		}catch(NumberFormatException e){
			throw new ErrorEjecucion("La direccion de ensamblado es invalida");		
		}
		if(dirInicio>255 ||dirInicio<0)
			throw new ErrorEjecucion("La direccion de ensamblado es invalida");
		return dirInicio;
	}
	public Reglas getReglas(){
		return analizadorLexico.getReglas();
	}

}
