package view;

import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import presenter.OCPresenter;
import view.help.VentanaHelp;



public class OCViewImpl implements OCView{
	private OCPresenter ocPresenter;
	
	//private JFrame ventanaPrincipal;
	private JFrame ventanaMemoria;
	private JFrame ventanaHelp;
	protected JPanel contentPane;
	private JButton botonAbrirArchivo; 
	private JButton botonVerAyuda;
	private JButton botonVerMemoria;
	private JButton botonCompilar;
	private JButton botonSiguiente;
	private JButton botonEjecutar;
	private JButton botonGuardarPanel;
	private JComboBox<String> tipoDeEjecucion;
	private TextArea contenidoArchivoActual;
	private JTextPane panelArchivoCompilado;
	private JTextField textoTutorial,direcccionDeInicioField;
	private JTable registrosTable,memoryTable;
	private JLabel lblArchivoOriginal,lblCompilado,lblDireccionInicio;
	private JLabel nombreArchivoActual,labelPc,labelInstruccion;
	private JFileChooser fileChooser;
	
	OCViewImpl(OCPresenter ocController) {

	    this.ocPresenter = ocController;

	    initialize();
	    initListeners();
	}
	public void updateTextoTutorial(String texto){
		this.textoTutorial.setText(texto);
	}
	@Override
	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(null, mensaje);
	}
	private void initListeners() {
	    
		botonAbrirArchivo.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  ocPresenter.onEventAbrirArchivo();
    	  }
		});
		botonCompilar.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  String direccion=direcccionDeInicioField.getText();
	    	  String contenido=contenidoArchivoActual.getText();
	    	  ocPresenter.onEventCompilar(contenido,direccion);
    	  }
		});
		botonEjecutar.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  ocPresenter.onEventEjecutar(esTipoEjecucionTotal());
    	  }
	      private boolean esTipoEjecucionTotal() {
			return tipoDeEjecucion.getSelectedIndex()==0;
	      }
		});		
		botonVerMemoria.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  ocPresenter.onEventVerMemoria();
    	  }
		});	
		botonSiguiente.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  ocPresenter.onEventSiguientePaso();
    	  }
		});
		botonVerAyuda.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  ocPresenter.onEventVerAyuda();
    	  }
		});	
		botonGuardarPanel.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  ocPresenter.onEventGuardarArchivo();
		      }
			});	
		contenidoArchivoActual.addKeyListener(new KeyListener() {
		    
				public void keyPressed(KeyEvent keyEvent) {}
		        public void keyReleased(KeyEvent keyEvent) {}
		        public void keyTyped(KeyEvent keyEvent) {
		        	ocPresenter.onEventModificoArchivo();
		        }
			});	

	}
	public void habilitarOpcionesdeEjecucion(){
		setearOpcionesdeEjecucion(true);
    }
	private void setearOpcionesdeEjecucion(boolean condicion){
		tipoDeEjecucion.setEnabled(condicion);
		botonEjecutar.setEnabled(condicion);
		registrosTable.setEnabled(condicion);
		botonVerMemoria.setEnabled(condicion);
    }
	@Override
	public void habilitarOpcionesdeCompilacion() {
		setearOpcionesdeEjecucion(false);  
		botonCompilar.setEnabled(true);
		direcccionDeInicioField.setEnabled(true);
		lblDireccionInicio.setEnabled(true);
		botonSiguiente.setEnabled(false);
	  	panelArchivoCompilado.setText("");	
	}
	@Override
	public void habilitarOpcionesdeEjecucionPasoaPaso() {
		botonSiguiente.setEnabled(true);
	}
	@Override
	public void deshabilitarOpcionesdeEjecucionPasoaPaso() {
		botonSiguiente.setEnabled(false);
	}
	@Override
	public void habilitarGuardarPanel() {
		botonGuardarPanel.setEnabled(true);
	}
	@Override
	public void deshabilitarGuardarPanel() {
		botonGuardarPanel.setEnabled(false);
	}
	private void initialize() {
		this.contentPane=new JPanel();
		this.contentPane.setLayout(null);
		
		botonAbrirArchivo = new JButton("Abrir Nuevo Archivo");
		botonAbrirArchivo.setBounds(12, 15, 199, 25);
		
		this.contentPane.add(botonAbrirArchivo);
		
		textoTutorial = new JTextField();
		textoTutorial.setEditable(false);
		textoTutorial.setBackground(null);
		textoTutorial.setBorder(null);
		textoTutorial.setText("Para iniciar la Aplicación Elegí el Archivo a Compilar");
		textoTutorial.setBounds(12, 460, 770, 19);
				
		this.contentPane.add(textoTutorial);
		textoTutorial.setColumns(10);
		
		nombreArchivoActual = new JLabel("");
		nombreArchivoActual.setBackground(null);
		nombreArchivoActual.setAutoscrolls(true);
		nombreArchivoActual.setBounds(236, 15, 534, 25);
		this.contentPane.add(nombreArchivoActual);
		
		botonCompilar = new JButton("Compilar");
		botonCompilar.setEnabled(false);
		botonCompilar.setBounds(12, 83, 117, 25);
		this.contentPane.add(botonCompilar);
		
		botonEjecutar = new JButton("Ejecutar");
		botonEjecutar.setEnabled(false);
		botonEjecutar.setBounds(653, 64, 117, 25);
		this.contentPane.add(botonEjecutar);
		
		tipoDeEjecucion = new JComboBox<String>();
		tipoDeEjecucion.addItem("Ejecutar todo el Codigo");
		tipoDeEjecucion.addItem("Ejecutar de a una Instruccion");
		tipoDeEjecucion.setEnabled(false);
		tipoDeEjecucion.setBounds(328, 64, 271, 25);
		this.contentPane.add(tipoDeEjecucion);
		
		contenidoArchivoActual = new TextArea();
		contenidoArchivoActual.setEnabled(true);
		contenidoArchivoActual.setEditable(true);
		contenidoArchivoActual.setBounds(30, 150, 200, 300);
		this.contentPane.add(contenidoArchivoActual);
		
		lblArchivoOriginal = new JLabel("Archivo Original");
		lblArchivoOriginal.setEnabled(false);
		lblArchivoOriginal.setBounds(58, 125, 111, 15);
		this.contentPane.add(lblArchivoOriginal);
		
		panelArchivoCompilado = new JTextPane();
		JScrollPane jsp = new JScrollPane(panelArchivoCompilado);
		jsp.setBounds(250, 150, 230, 300);
		this.contentPane.add(jsp);
		
		direcccionDeInicioField = new JTextField();
		direcccionDeInicioField.setEnabled(false);
		direcccionDeInicioField.setText("00");
		direcccionDeInicioField.setBounds(216, 86, 30, 19);
		this.contentPane.add(direcccionDeInicioField);
		direcccionDeInicioField.setColumns(10);
		
		lblDireccionInicio = new JLabel("Dir Inicio:");
		lblDireccionInicio.setEnabled(false);
		lblDireccionInicio.setBounds(141, 88, 70, 15);
		this.contentPane.add(lblDireccionInicio);
		
		lblCompilado = new JLabel("Compilado");
		lblCompilado.setEnabled(false);
		lblCompilado.setBounds(236, 125, 74, 15);
		this.contentPane.add(lblCompilado);
		
		String [][]a=new String[16][2];
		for(int i=0;i<16;i++){
			a[i][0]=("R"+Integer.toHexString(i)).toUpperCase();
			a[i][1]="00";
		}
		String[]columnNames={"Registros","Contenido"};
		registrosTable = new JTable(a, columnNames);
		registrosTable.setEnabled(false);
		JScrollPane jsp2 = new JScrollPane(registrosTable);
		jsp2.setBounds(496, 170, 150, 280);
		this.contentPane.add(jsp2);
		
		JLabel labelBancoDeRegistros = new JLabel("Banco de Registros");
		labelBancoDeRegistros.setEnabled(false);
		labelBancoDeRegistros.setBounds(503, 150, 137, 15);
		this.contentPane.add(labelBancoDeRegistros);
		
		botonVerMemoria = new JButton("Ver Memoria");
		botonVerMemoria.setEnabled(false);
		botonVerMemoria.setBounds(659, 276, 123, 25);
		this.contentPane.add(botonVerMemoria);
		
		botonSiguiente = new JButton("Siguiente");
		botonSiguiente.setEnabled(false);
		botonSiguiente.setBounds(659, 239, 123, 25);
		this.contentPane.add(botonSiguiente);
		
		JButton btnDetener = new JButton("Detener");
		btnDetener.setEnabled(false);
		btnDetener.setBounds(653, 99, 117, 25);
		this.contentPane.add(btnDetener);
		
		botonGuardarPanel = new JButton("Guardar el Panel");
		botonGuardarPanel.setEnabled(false);
		botonGuardarPanel.setBounds(12, 52, 199, 25);
		this.contentPane.add(botonGuardarPanel);
		
		labelPc = new JLabel("PC=");
		labelPc.setEnabled(false);
		labelPc.setBounds(659, 174, 113, 19);
		this.contentPane.add(labelPc);
		
		labelInstruccion = new JLabel("Intruccion:");
		labelInstruccion.setEnabled(false);
		labelInstruccion.setBounds(659, 197, 123, 30);
		this.contentPane.add(labelInstruccion);
		
		botonVerAyuda = new JButton("Ayuda");
		botonVerAyuda.setBounds(659, 386, 123, 25);
		this.contentPane.add(botonVerAyuda);

		
		JButton botonSobreMi = new JButton("Sobre Mi");
		botonSobreMi.setBounds(659, 423, 123, 25);
		this.contentPane.add(botonSobreMi);
		
		ventanaMemoria=new JFrame();
		ventanaMemoria.setTitle("Memoria de OCUNS");
		ventanaMemoria.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		ventanaMemoria.setBounds(50, 50, 170, 520);
		ventanaMemoria.getContentPane().setLayout(null);
		ventanaMemoria.setVisible(false);
		
		String[]cNames={"Dir","Memoria"};
		String [][]b=new String[256][2];
		for(int i=0;i<16;i++){
			b[i][0]=("0"+Integer.toHexString(i)).toUpperCase();
			b[i][1]="00";
		}for(int i=16;i<256;i++){
			b[i][0]=(""+Integer.toHexString(i)).toUpperCase();
			b[i][1]="00";
		}
		
		memoryTable = new JTable(b, cNames);
		memoryTable.setEnabled(false);
		JScrollPane jsp3 = new JScrollPane(memoryTable);
		jsp3.setBounds(0, 0, 150, 490);
		ventanaMemoria.getContentPane().add(jsp3);
		
		ventanaHelp = new VentanaHelp();
		ventanaHelp.setVisible(false);
		ventanaHelp.setTitle("Ayuda para OCUNS Virtual Machine");
		ventanaHelp.setSize(600,500);
		fileChooser= new JFileChooser(); 
		fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de OCVM","ocuns"));
		  
	}
	@Override
	public String pedirDialogo(String pedido) {
		return JOptionPane.showInputDialog(pedido);
	}
	@Override
	public boolean pedirAbrirArchivo() {
		int opcion = fileChooser.showDialog(null, "Abrir");
		return (opcion == JFileChooser.APPROVE_OPTION);
	}
	@Override
	public boolean guardarArchivo() {
		int seleccion = fileChooser.showSaveDialog(null);
		 if (seleccion == JFileChooser.APPROVE_OPTION){
			 try {
			 	PrintWriter printwriter= new PrintWriter(recuperarArchivo());
				printwriter.print(contenidoArchivoActual.getText());
			 	printwriter.close();
			 } catch (FileNotFoundException e) {
				 mostrarMensaje("Error al Guardar el Archivo");
			 }
		 }
		return (seleccion == JFileChooser.APPROVE_OPTION);
	}
	@Override
	public File recuperarArchivo() {
		return fileChooser.getSelectedFile();
	}
	@Override
	public void updateNombreArchivo(String fileName) {
		nombreArchivoActual.setText(fileName);
	}
	public void updateContenidoArchivoActual(String contenido){
		  contenidoArchivoActual.setText(contenido);
		  contenidoArchivoActual.setEnabled(true);
	}
	@Override
	public void updateCodigoCompilado(String codigo) {
		panelArchivoCompilado.setText(codigo);
	}
	@Override
	public void updateRegistros(Map<Integer,String> registros) {
		for (Map.Entry<Integer, String> entry : registros.entrySet())
			registrosTable.setValueAt(entry.getValue(),entry.getKey(), 1);
	}
	@Override
	public void updateMemoria(Map<Integer,String> memoria) {
		for (Map.Entry<Integer, String> entry : memoria.entrySet())
			memoryTable.setValueAt(entry.getValue(),entry.getKey(), 1);
	}
	@Override
	public void updatePCView(String pc) {
		labelPc.setText("PC:"+pc);
	}
	@Override
	public void updateInstrucionView(String instruccion) {
		labelInstruccion.setText("Intruccion:\n\n "+instruccion);
	}
	@Override
	public void mostrarMemoria() {
		ventanaMemoria.setVisible(true);
	}
	@Override
	public void mostrarAyuda() {
		ventanaHelp.setVisible(true);
	}
	@Override
	public void updateLogs(String log) {
		System.out.println(log);	
	}
}
