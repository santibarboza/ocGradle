package model.Analizadores;

import model.Archivos.Archivo;
import model.Tokens.Token;
import model.Tokens.TokenEOF;
import model.Tokens.TokenImpl;
import model.Tokens.TokenSalto;
import Excepciones.ErrorLexico;
import Excepciones.ErrorOCUNS;

public class AnalizadorLexicoImpl implements AnalizadorLexico {

	protected Archivo archivo;
	protected String lineaLeida;
	protected int indexLine;
	protected int numerolinea;
	protected Reglas reglas;
	
	public AnalizadorLexicoImpl(Reglas reglas){
		this.reglas=reglas;
	}
	public void iniciarConArchivo(Archivo archivo) throws ErrorOCUNS{
		this.archivo=archivo;
		numerolinea=0;
		recargarLinea();
	}
	private Token recargarLinea() throws ErrorOCUNS{
		indexLine=0;
		do{
			numerolinea++;
			lineaLeida=archivo.readLine();
		}while(lineaVacia(lineaLeida));
		
		return new TokenSalto(numerolinea,indexLine);
	}
	private boolean lineaVacia(String linea){
		return linea!=null && linea.length()==0;
	}
	
	@Override
	public Token getToken() throws ErrorOCUNS {

		if(terminoLinea())
			return recargarLinea();
		if(terminoArchivo())
			return findeArchivo();
		
		Token tokenARetornar=null;
		char caracterActual=caracterActual();
		
		
		if(esEspacioBlanco())
			tokenARetornar= saltarBlancos();
		else if(reglas.esTrivial(caracterActual))
				tokenARetornar= analizarCasoTrivial(caracterActual);
			else if(caracterActual=='/')
					tokenARetornar= analizarComentarios();
				else if(esOffsetNegativo())
						tokenARetornar=analizarDesplazamientoNegativo();
					else if(esDigito())
							tokenARetornar= analizarDigito();
						else if(esRegistro())
								tokenARetornar= analizarRegistro();
							else if(esDireccion())
									tokenARetornar= analizarDireccion();
								else if(esLetra())
										tokenARetornar= analizarIdentificador();
									else
		throw new ErrorLexico("ErrorLexico en "+numerolinea+":"+indexLine+"= El caracter "+caracterActual+" no esta valido en el lenguaje");
		return tokenARetornar;
	}

	private char caracterActual(){
		return lineaLeida.charAt(indexLine);
	}

	private boolean terminoLinea(){
		return lineaLeida!=null && indexLine== lineaLeida.length();
	}
	private boolean terminoArchivo(){
		return lineaLeida==null;
	}
	private boolean esEspacioBlanco(){
		return caracterActual()==' ' || caracterActual()==(char)9;
	}
	private boolean esSiguienteCaracter(char nextChar){
		return indexLine<(lineaLeida.length()-1) && lineaLeida.charAt(indexLine+1)==nextChar;
	}
	private boolean esOffsetNegativo(){
		return lineaLeida.charAt(indexLine)=='-' && digitoDe0a8(indexLine+1);
	}
	private boolean esDigito(){
		return (caracterActual()>='0' && caracterActual()<='9'); 
	}
	private boolean esRegistro(){
		return indexLine<(lineaLeida.length()-1) && lineaLeida.charAt(indexLine)=='R' && digitoHexa(indexLine+1);
	}
	private boolean esDireccion(){
		return digitoHexa(indexLine)&& digitoHexa(indexLine+1);
	}
	private boolean esLetra(){
		char caracterActual= lineaLeida.charAt(indexLine);
		return (caracterActual>='A' && caracterActual<='Z')||(caracterActual>='a' && caracterActual<='z');
	}
	private boolean digitoHexa(int indexLine){
		if(indexLine>=lineaLeida.length())
			return false;
		char caracter=lineaLeida.charAt(indexLine);
		return ((caracter>='0' && caracter<='9')||(caracter>='A' && caracter<='F'));
	}
	private boolean digitoDe0a8(int indexLine){
		if(indexLine>=lineaLeida.length())
			return false;
		char caracter=lineaLeida.charAt(indexLine);
		return (caracter>='0' && caracter<='8');
	}
	private Token analizarCasoTrivial(char caracterActual){
		indexLine++;
		return new TokenImpl(reglas.getIDTrivial(caracterActual),caracterActual+"",numerolinea,indexLine+1);
	}
	private Token analizarComentarios() throws ErrorOCUNS {
		if(esSiguienteCaracter('/'))			
			return comentarioSimple();
		else if(esSiguienteCaracter('*'))
			return comentarioMultilinea();
		else
			throw new ErrorLexico("Error Lexico en "+numerolinea+":"+indexLine+" = El caracter siguiente a / no es valido en el lenguaje");
	}
	private Token analizarDesplazamientoNegativo(){
		int i=(lineaLeida.charAt(indexLine-1)-48); 
		int complemento=16-i;
		indexLine+=2;
		return new TokenImpl("Lit_Desp",complemento,numerolinea,indexLine);
	}
	private Token analizarDigito() throws ErrorLexico{
		char caracterActual=lineaLeida.charAt(indexLine);
		indexLine++;
		if(digitoHexa(indexLine)){
			indexLine++;
			int i=Integer.parseInt(""+caracterActual+lineaLeida.charAt(indexLine-1),16);
			return new TokenImpl("Lit_Dir",i,numerolinea,indexLine-1);
		}else if(caracterActual<='7')
			return new TokenImpl("Lit_Desp",(caracterActual-48),numerolinea,indexLine);
		else
			throw new ErrorLexico("ErrorLexico en "+numerolinea+":"+indexLine+"= Desplazamiento fuera de rango");
	}
	private Token analizarRegistro(){
		indexLine=indexLine+2;
		int numero=lineaLeida.charAt(indexLine-1);
		if(numero>57)
			numero-=7;
		return new TokenImpl("Id_Reg",numero -48,numerolinea,indexLine-1);
	}
	private Token analizarDireccion(){
		int i= Integer.parseInt(""+lineaLeida.charAt(indexLine)+lineaLeida.charAt(indexLine+1),16);
		indexLine=indexLine+2; 
		return new TokenImpl("Lit_Dir",i,numerolinea,indexLine-1);
	}
	private Token analizarIdentificador() {
		int numerocolumna=indexLine+1;
		String lexema =nombreIdentificador();
	
		if(reglas.esSentencia(lexema)){
			int id= reglas.getIDSentencia(lexema);
			return new TokenImpl(reglas.getLexemaSentencia(id),id,numerolinea,numerocolumna);
		}else
			return new TokenImpl("Id_Etiq",lexema,numerolinea,numerocolumna);
				
	}
	private Token comentarioSimple() throws ErrorOCUNS{
		return recargarLinea();
	}
	private Token comentarioMultilinea() throws ErrorOCUNS {
		int linea_inicio=numerolinea;
		int numeroColumna=indexLine+1;
		Token token=null;

		indexLine+=2; 
		
		
		while(noTerminaComentarioMultilinea())
			token= recargarLinea();

		if(lineaLeida==null)
			throw new ErrorLexico("ErrorLexico en "+linea_inicio+":"+numeroColumna+"= comentario multilinea empieza pero nunca termina");

		indexLine=lineaLeida.indexOf("*/",indexLine)+2; 
		return token;
	}
	private boolean  noTerminaComentarioMultilinea(){
		return lineaLeida!=null && lineaLeida.indexOf("*/",indexLine)==-1;
	}
	private Token saltarBlancos() throws ErrorOCUNS {
		while(indexLine<lineaLeida.length() && esEspacioBlanco())
			indexLine++;
		return getToken();
	}
	private String nombreIdentificador() {
		String lexema="";
		while(esCaracterIdentificador(indexLine)){
			lexema+=lineaLeida.charAt(indexLine);
			indexLine++;
		}
		return lexema;
	}
	private boolean esCaracterIdentificador(int indexLine){
		return indexLine<lineaLeida.length() && (esLetra()||esDigito()|| esGuionBajo()); 
	}
	private boolean esGuionBajo() {
		return lineaLeida.lastIndexOf(indexLine)=='_';
	}
		private Token findeArchivo(){
		archivo.Close();
		return new TokenEOF(numerolinea,indexLine);
	}
		@Override
		public Reglas getReglas() {
			return reglas;
		}

}
