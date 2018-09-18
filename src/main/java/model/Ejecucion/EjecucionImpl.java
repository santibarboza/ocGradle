package model.Ejecucion;

import Utils.Hexadecimal;
import model.OCModel;
import model.RepresentacionMemoria.Memoria;
import Excepciones.ErrorEjecucion;

public class EjecucionImpl implements Ejecucion{
	
	private OCModel ocModel;
	private Memoria memoria;
	protected int pc, pcAnt,opcode,pcPAP;
	private int registroDIndex, registroSIndex, registroTIndex;
	private int bufferRegistroD,bufferRegistroS,bufferRegistroT;
	private int addr, offset,desplazamiento;
	private boolean condicion;
	protected boolean noTerminoPrograma;

	public EjecucionImpl(Memoria memoria){
		this.memoria=memoria;
	}
	@Override
	public void ejecutarCodigoCompleto() throws ErrorEjecucion {
		int instruccion=0;
		boolean noTermina=true;
		
		iniciarPC();	
		while(noTermina) {
			instruccion=fetch();
			decode(instruccion);
			noTermina=execute();
			memory();
			writeBack();
		}
	}
	@Override
	public void iniciarEjecucionPasoaPaso() throws ErrorEjecucion {
		pcPAP=memoria.getDireccionInicio();
		noTerminoPrograma=true;
		ejecutarSiguienteInstruccion();
	}
	@Override
	public void ejecutarSiguienteInstruccion() throws ErrorEjecucion {
		int instruccion=0;
		pc= pcPAP;
		instruccion=fetch();
		decode(instruccion);
		noTerminoPrograma=execute();
		memory();
		writeBack();
		pcPAP=pc;
	}
	
	private void iniciarPC() {
		pc= memoria.getDireccionInicio();
	}
	private int fetch(){
		int instruccion =(memoria.leerMemoria(pc) << 8)+ memoria.leerMemoria(pc+1);
		pcAnt=pc;
		pc = (pc + 2) & 255;
		ocModel.updatePCView(pc+"");
		ocModel.updateInstrucionView(memoria,pc);
		ocModel.updateLogs("Fetch:\n\t Nuevo PC="+pc+"\n\t Intruccion: "+hexaV(instruccion));
		return instruccion;	
	}
	private void decode(int instruccion) throws ErrorEjecucion{	
		leerIndices(instruccion);
		leerRegistros();
		calcularOffset();
		protegerRegistroF();
		updateLogDecode();
	}
	private void leerIndices(int instruccion) {
		opcode=(instruccion>>12)&15; 
		registroDIndex=(instruccion >> 8) & 15;
		registroSIndex=(instruccion >> 4) & 15;
		registroTIndex=(instruccion >> 0) & 15;
		addr=(instruccion >> 0) & 255;
	}
	private void leerRegistros() {
		bufferRegistroS=memoria.leerRegistro(registroSIndex);
		bufferRegistroT=memoria.leerRegistro(registroTIndex);
		bufferRegistroD=memoria.leerRegistro(registroDIndex);
	}
	private void calcularOffset() {
		offset=registroTIndex;
		if(offset>=8)
			offset=offset-16;
	}
	private void protegerRegistroF() throws ErrorEjecucion {
		if(registroDIndex==15 && opcode!=7 && opcode!=9 && opcode!=10&& opcode!=12)
			throw new ErrorEjecucion("El registro F es de solo lectura. error cuando pc= "+hexa(pc));
	}
	private boolean execute() throws ErrorEjecucion {
		if(opcode == 0xF)
			return false;		
		switch(opcode){
			case 0x0:realizarAdd();			break;		
			case 0x1:realizarSub();			break;		
			case 0x2:realizarAnd();			break;		
			case 0x3:realizarOr();			break;		
			case 0x4:realizarLeftShift();	break;		
			case 0x5:realizarRightShift();	break;		
			case 0x6:realizarLoad();		break;		
			case 0x7:realizarStore();		break;
			case 0x8:realizarLda();			break;		
			case 0x9:realizarJumpZero();	break;		
			case 0xA:realizarJumpGreater();	break;		
			case 0xB:realizarCall();		break;		
			case 0xC:realizarJump();		break;		
			case 0xD:realizarInc();			break;		
			case 0xE:realizarDec();			break;	
			default:
				throw new ErrorEjecucion("Opcode Invalido cuando pc= "+hexa(pc));
		}
		updateLogExecute();
		controlarValorRegistroD();
		return true;
	}
	private void realizarAdd() throws ErrorEjecucion {
		bufferRegistroD=complemento(bufferRegistroS)+complemento(bufferRegistroT);
		if(bufferRegistroD>127 || bufferRegistroD< -128)
			throw new ErrorEjecucion("Overflow cuando PC="+hexa(pc));
		bufferRegistroD=complemento(bufferRegistroD);
	}
	private void realizarSub() throws ErrorEjecucion {
		bufferRegistroD=complemento(bufferRegistroS)-complemento(bufferRegistroT);
		if(bufferRegistroD>127 || bufferRegistroD< -128)
			throw new ErrorEjecucion("Overflow cuando PC="+hexa(pc));
		bufferRegistroD=complemento(bufferRegistroD);
	}
	private void realizarAnd() {
		bufferRegistroD=(complemento(bufferRegistroS) & complemento(bufferRegistroT));
	}
	private void realizarOr() {
		bufferRegistroD=(complemento(bufferRegistroS) ^ complemento(bufferRegistroT));
	}
	private void realizarLeftShift() {
		bufferRegistroD=(bufferRegistroS<<bufferRegistroT) & 256;
	}
	private void realizarRightShift() {
		bufferRegistroD=(bufferRegistroS>>bufferRegistroT) & 256;
	}
	private void realizarLoad() throws ErrorEjecucion {
		desplazamiento=(bufferRegistroS+complemento(offset));		
	}
	private void realizarStore() {
		desplazamiento=bufferRegistroD+complemento(offset);
	}
	private void realizarLda() {
		bufferRegistroD=addr;
	}
	private void realizarJumpZero() {
		condicion= (bufferRegistroD==0);
	}
	private void realizarJumpGreater() {
		condicion=(complemento(bufferRegistroD)>0);
	}
	private void realizarCall() {
		bufferRegistroD=pc;
		pc=addr;
		ocModel.updatePCView(pc+"");
	}
	private void realizarJump() {
		pc=bufferRegistroD;
		ocModel.updatePCView(pc+"");
	}
	private void realizarInc() {
		bufferRegistroD=(complemento(bufferRegistroD)+1) & 255;
	}
	private void realizarDec(){
		bufferRegistroD=(complemento(bufferRegistroD)-1) & 255;
	}
	private void controlarValorRegistroD() throws ErrorEjecucion {
		if(bufferRegistroD>255 || bufferRegistroD<-128)
			throw new ErrorEjecucion("Overflow cuando PC="+hexa(pc));
	}
	private void memory() throws ErrorEjecucion{
		if(esBranch())
			MemoryBranch();
		else if(esLoad())
			MemoryLoad();
		else if(esStore())
			MemoryStore();
	}
	private boolean esBranch(){
		return(opcode==9)||(opcode==10);
	}
	private void MemoryBranch(){
		if(condicion)
			pc=pc+complemento(addr);
		ocModel.updatePCView(pc+"");
	}
	private boolean esLoad(){
		return (opcode==6);
	}
	private void MemoryLoad() throws ErrorEjecucion{
		if(esInteraccionConUsuario())
			read();
		else
			bufferRegistroD=memoria.leerMemoria(desplazamiento);
	}
	private boolean esStore(){
		return (opcode==7);
	}
	private void MemoryStore(){
		if(esInteraccionConUsuario())
			ocModel.mostrarMensaje(" Salida =  "+hexa(bufferRegistroS)+" = ("+complemento(bufferRegistroS)+")d");
		else
			memoria.escribirMemoria(desplazamiento, bufferRegistroS);
	}
	private boolean esInteraccionConUsuario(){
		return desplazamiento==255;
	}
	private void read() throws ErrorEjecucion{
		try{
			String ax=ocModel.pedirDialogo("Ingrese un numero de 00 a FF:");
			bufferRegistroD=Integer.parseInt(ax, 16);
			}catch(NumberFormatException e){
				pc=pcAnt;
				throw new ErrorEjecucion("El numero ingresado no es valido");		
			}
			if(bufferRegistroD<0 || bufferRegistroD>255){
				pc=pcAnt;
				throw new ErrorEjecucion("Se ingreso un numero fuera de rango");
			}
	}

	private void writeBack() {
		bufferRegistroD=bufferRegistroD&255;
		memoria.escribirRegistro(registroDIndex, bufferRegistroD);
		ocModel.updateMemoria();
		ocModel.updateRegistros();
		ocModel.updateLogs("WriteBack: Se actualizo RD\n");
	}
	private void updateLogDecode(){
		String log="Decode:\n\t Opcode:"+opcode;
		log+="\n\t RegistroD: R"+hexaV(registroDIndex);
		log+="\n\t RegistroS: R"+hexaV(registroSIndex)+"="+Hexadecimal.hex2Dig(bufferRegistroS);
		log+="\n\t RegistroT: R"+hexaV(registroTIndex)+"="+Hexadecimal.hex2Dig(bufferRegistroT);
		log+="\n\t Direccion: 0x"+Hexadecimal.hex2Dig(addr);
		ocModel.updateLogs(log);
	}
	private void updateLogExecute(){
		String log="Execute: ";
		switch(opcode){
			case 0x0:log+="ADD\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x1:log+="SUB\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x2:log+="AND\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x3:log+="OR\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x4:log+="LSHIFT\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x5:log+="RSHIFT\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x6:log+="Load\n\t DireccionEfectiva<-" +hexa(desplazamiento);		break;		
			case 0x7:log+="Store\n\t DireccionEfectiva<-"+hexa(desplazamiento);		break;
			case 0x8:log+="LDA\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0x9:log+="JZ\n\t condicion="+condicion;	break;		
			case 0xA:log+="JG\n\t condicion="+condicion;	break;		
			case 0xB:log+="CALL\n\t NuevoPC="+hexa(pc);			break;		
			case 0xC:log+="JUMP\n\t NuevoPC="+hexa(pc);			break;		
			case 0xD:log+="INC\n\t RD<-"+hexa(bufferRegistroD);	break;		
			case 0xE:log+="DEC\n\t RD<-"+hexa(bufferRegistroD);	break;
		}
		ocModel.updateLogs(log);
	}
	private void updateLogMemory(){
		String log="Memory: ";
		if(esBranch())
			log+="Branch\n\t PC="+hexa(pc);
		else if(esLoad())
				log+=logLoad();				
		else if(esStore())
				log+=logStore();
		else
			log+="Memory: NOP";
		ocModel.updateLogs(log);
	}
	private String logLoad(){
		String log="Load";
		if(esInteraccionConUsuario())
			log+="- Read()";
		log+="\n\t RD="+hexa(bufferRegistroD);
		return log;
	}
	private String logStore(){
		String log="Store";
		if(esInteraccionConUsuario())
			log+="- Print()";
		log+="\n\t RD="+hexa(bufferRegistroD);
		return log;
	}
	private String hexa(int i){
		return Hexadecimal.hex2(i);
	}
	private String hexaV(int i){
		return Hexadecimal.hex(i);
	} 
	private int complemento(int i){
		return Hexadecimal.comp(i);
	}
	@Override
	public void setModel(OCModel ocModel) {
		this.ocModel=ocModel;		
	}
	@Override
	public boolean hayCodigoParaEjecutar() {
		return noTerminoPrograma;
	}
	@Override
	public Memoria getMemoria() {
		return memoria;
	}

	
}
