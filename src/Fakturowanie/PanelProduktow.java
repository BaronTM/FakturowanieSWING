package Fakturowanie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.MaskFormatter;

public class PanelProduktow extends JPanel{
	
	private JLabel tytul;
	private JLabel fakturyLab;
	private TabelaProduktow lista;
	private TabelaFaktur listaFaktur;
	private JScrollPane listaScroll;
	private JScrollPane listaFakturScroll;
	private JPanel panelPodListe;
	private JPanel panelPodListeFaktur;
	private JPanel zaslona;
	private JButton nowyProdukt;
	private JLayeredPane layeredPane;
	private RamkaDodawaniaProduktow ramkaDodawania;
	private DefaultTableModel modelListyProduktow;
	
	public PanelProduktow() {
		super();
		this.setBounds(260, 0, 740, 680);
		this.setLayout(null);
		this.setBackground(Color.YELLOW);
		
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 740, 680);
		zaslona = new JPanel();
		zaslona.setLayout(null);
		zaslona.setBounds(0, 0, 740, 680);
		zaslona.setBackground(Color.BLACK);
		zaslona.setVisible(false);
		
		tytul = new JLabel("PRODUKTY");
		tytul.setFont(new Font("TimesRoman", Font.BOLD, 30));
		tytul.setBounds(120, 20, 500, 40);
		tytul.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		modelListyProduktow = new DefaultTableModel(TabelaProduktow.getNazwyKolumn(), 0);
		lista = new TabelaProduktow(modelListyProduktow);
		listaScroll = new JScrollPane(lista);
		listaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		listaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		panelPodListe = new JPanel();
		panelPodListe.setLayout(new BorderLayout());
		panelPodListe.add(listaScroll, BorderLayout.CENTER);
		panelPodListe.setBounds(30, 70, 470, 250);

		fakturyLab = new JLabel("Faktury zawierające produkt");
		fakturyLab.setFont(new Font("TimesRoman", Font.BOLD, 20));
		fakturyLab.setBounds(30, 360, 350, 40);
		
		// ---------------------------- TESTOWE
		Object[][] data2 = {
				{"1", "9146/2018", "24.11.2018", 145505.43, 214012.99, "PLN", true, 
					false, "check"}
		};

		listaFaktur = new TabelaFaktur(data2, TabelaFaktur.getNazwyKolumn());
		listaFakturScroll = new JScrollPane(listaFaktur);
		listaFakturScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		listaFakturScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		panelPodListeFaktur = new JPanel();
		panelPodListeFaktur.setLayout(new BorderLayout());
		panelPodListeFaktur.add(listaFakturScroll, BorderLayout.CENTER);
		panelPodListeFaktur.setBounds(30, 400, 680, 250);
		
		nowyProdukt = new JButton("NOWY PRODUKT");
		nowyProdukt.setBounds(520, 70, 180, 30);
		
		// ------- panel dodawania
		ramkaDodawania = new RamkaDodawaniaProduktow("Dodawanie Nowego Produktu");
		
		// ------- Listenery
		nowyProdukt.addActionListener(l -> {
			ramkaDodawania.setVisible(true);
			zaslona.setVisible(true);
			
		});
		
		this.add(layeredPane);
		layeredPane.add(tytul, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(panelPodListe, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(fakturyLab, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(panelPodListeFaktur, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(nowyProdukt, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(zaslona, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(ramkaDodawania, JLayeredPane.MODAL_LAYER);
	}
	
	public void odswiezListy() {
		for (int k = modelListyProduktow.getRowCount(); k > 0; k--) {
			modelListyProduktow.removeRow(0);
		}
		int i = 0;
		for (Produkt p : Historia.getProdukty()) {
			Object[] element = new Object[5];
			element[0] = i + 1;
			element[1] = p.getNazwa();
			element[2] = p.getCenaNetto();
			element[3] = Ustawienia.getWaluta();
			element[4] = p.getJednostka();
			modelListyProduktow.addRow(element);
			i++;
		}
	}
	
	private class RamkaDodawaniaProduktow extends JInternalFrame {

		private JPanel panelDodawania;
		private JLabel nazwaLab;
		private JLabel cenaLab;
		private JLabel jednostkaLab;
		private JTextField nazwaTxt;
		private JFormattedTextField cenaTxt;
		private JComboBox<String> jednostkaCB;
		private JButton dodaj;
		private JButton anuluj;

		public RamkaDodawaniaProduktow(String nazwa) {
			super(nazwa, false, false, false, false);
			this.setBounds(120, 90, 425, 250);

			panelDodawania = new JPanel();
			panelDodawania.setLayout(null);
			panelDodawania.setBounds(0, 0, 425, 250);

			nazwaLab = new JLabel("Nazwa");
			nazwaLab.setFont(new Font("TimesRoman", Font.BOLD, 20));
			nazwaLab.setBounds(15, 15, 150, 30);
			cenaLab = new JLabel("Cena Netto");
			cenaLab.setFont(nazwaLab.getFont());
			cenaLab.setBounds(15, 60, 150, 30);
			jednostkaLab = new JLabel("Jednostka");
			jednostkaLab.setFont(nazwaLab.getFont());
			jednostkaLab.setBounds(15, 105, 150, 30);

			nazwaTxt = new JTextField();
			nazwaTxt.setFont(new Font("TimesRoman", Font.ITALIC, 15));
			nazwaTxt.setHorizontalAlignment(JTextField.CENTER);
			nazwaTxt.setBounds(200, 15, 200, 30);
			cenaTxt = new JFormattedTextField();
			cenaTxt.setFont(nazwaTxt.getFont());
			cenaTxt.setBounds(200, 60, 200, 30);
			cenaTxt.setHorizontalAlignment(JTextField.CENTER);
			cenaTxt.setFormatterFactory(new AbstractFormatterFactory() {
	            @Override
	            public AbstractFormatter getFormatter(JFormattedTextField tf) {
	                NumberFormat format = DecimalFormat.getInstance();
	                format.setMinimumFractionDigits(2);
	                format.setMaximumFractionDigits(2);
	                format.setRoundingMode(RoundingMode.HALF_UP);
	                InternationalFormatter formatter = new InternationalFormatter(format);
	                formatter.setAllowsInvalid(false);
	                formatter.setMinimum(0.0);
	                formatter.setMaximum(1000000.00);
	                return formatter;
	            }});
			cenaTxt.setText(Float.toString(0.00f));
			jednostkaCB = new JComboBox<String>(Produkt.getListaJednostek());
			jednostkaCB.setFont(nazwaTxt.getFont());
			jednostkaCB.setBounds(200, 105, 200, 30);
			jednostkaCB.setSelectedIndex(0);
			dodaj = new JButton("DODAJ");
			dodaj.setBounds(300, 170, 100, 30);
			anuluj = new JButton("ANULUJ");
			anuluj.setBounds(15, 170, 100, 30);

			panelDodawania.add(nazwaLab);
			panelDodawania.add(cenaLab);
			panelDodawania.add(jednostkaLab);
			panelDodawania.add(nazwaTxt);
			panelDodawania.add(cenaTxt);
			panelDodawania.add(jednostkaCB);
			panelDodawania.add(dodaj);
			panelDodawania.add(anuluj);

			// ------- Listenery
			anuluj.addActionListener(l -> {
				this.setVisible(false);
				zaslona.setVisible(false);
				nazwaTxt.setText("");
				cenaTxt.setText("0.00");
				jednostkaCB.setSelectedIndex(0);
			});			
			dodaj.addActionListener(l -> {
				String[] parts = cenaTxt.getText().split(",");
				float cenaNowego = Float.parseFloat(parts[0]) + Float.parseFloat(parts[1]) / 100;
				String nazwaNowego = (String) nazwaTxt.getText();
				String jednostkaNowego = (String) jednostkaCB.getSelectedItem();
				if ((cenaNowego < 0) || (nazwaNowego.equals("")) || jednostkaNowego.equals("")) {
					JOptionPane.showMessageDialog(this,
						    "Wprowadź wymagane dane.",
						    "Błąd wprowadzania",
						    JOptionPane.ERROR_MESSAGE);
				} else {
					boolean istnieje = false;
					for (Produkt p : Historia.getProdukty()) {
						if (p.getNazwa().equals(nazwaNowego)) {
							istnieje = true;
						}
					}
					if (istnieje) {
						JOptionPane.showMessageDialog(this,
							    "Produkt o takiej nazwie już istnieje.",
							    "Błąd",
							    JOptionPane.ERROR_MESSAGE);
					} else {
						Historia.getProdukty().add(new Produkt(nazwaNowego, cenaNowego, jednostkaNowego));
						nazwaTxt.setText("");
						cenaTxt.setText("0.00");
						jednostkaCB.setSelectedIndex(0);
						odswiezListy();
					}
				}				
			});

			this.add(panelDodawania);
			this.setVisible(false);
		}

	}
}
