package view;

import javax.swing.JFrame;
import presenter.OCPresenter;


public class OCViewModule {
	  private static OCViewModule instance;

	  private OCViewModule() { }

	  public static OCViewModule getInstance() {
	    if (instance == null) {
	      instance = new OCViewModule();
	    }
	    return instance;
	  }

	  public OCView openOCWindow(OCPresenter ocPresenter) {
	    OCViewImpl ocView = new OCViewImpl(ocPresenter);

	    JFrame ventanaPrincipal = new JFrame();
		ventanaPrincipal.setTitle("OCUNS - VirtualMachine");
		ventanaPrincipal.setContentPane(ocView.contentPane);
		ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventanaPrincipal.pack();
		ventanaPrincipal.setBounds(50, 50, 800, 520);
		ventanaPrincipal.setVisible(true);

	    return ocView;
	  }
}
