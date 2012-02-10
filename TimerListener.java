import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TimerListener implements ActionListener {

	private DevicePhysique devPhy;
	
	public TimerListener(DevicePhysique devPhy) {
		super();
		this.devPhy = devPhy;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("TimerListener : Capteur disparu");
		// Création des trames de Retrait pour tous les devices logiques associés
		long timestamp = System.currentTimeMillis();
		char typeTrame = Constantes.TYPE_STATUS;
		char typeStatus = Constantes.TYPE_RETRAIT;
		for(int i = 0; i < this.devPhy.getListeDevicesLogiques().size(); i++) {
			DeviceLogique capteurLogique = this.devPhy.getListeDevicesLogiques().get(i);
			int idCapteurLogique = capteurLogique.getIdLogique();
			char typeCapteurLogique = capteurLogique.getTypeLogique();
			ProxyTrameS proxyTrameS = new ProxyTrameS(timestamp, typeTrame, typeStatus, idCapteurLogique, typeCapteurLogique);

			System.out.println("TimerListener : Type de capteur : " + proxyTrameS.getTypeDevice());
			System.out.println("TimerListener : Avant envoi trame n°" + i);
			// Ajout des trames à envoyer
			ClientEnvoiGHome.addProxyTrame(proxyTrameS);
	}
}

}