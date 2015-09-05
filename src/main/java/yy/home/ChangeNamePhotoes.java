package yy.home;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

public class ChangeNamePhotoes implements ActionListener {
	final static protected ArrayList<String> messages = new ArrayList<String>();
	final static protected HashMap<File, Integer> compress = new HashMap<File, Integer>();
	final static protected Settings settings = new Settings();
	private CustomTableModel tableModel = new CustomTableModel(settings.getDir());
	final private JTable tableFiles = new JTable(tableModel);
	final JTextField textOut = new JTextField();

	public void addComponentsToPane(Container pane) {
		final Insets buttonInsets = new Insets(4, 4, 4, 8);

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(0, 8, 0, 0);
		final JTextField textChooseCatalog = new JTextField();
		textChooseCatalog.setFont(new Font(null));
		textChooseCatalog.setEditable(false);
		textChooseCatalog.setText(settings.getDir().getAbsolutePath());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(textChooseCatalog, c);

		final JButton btnChooseCatalog = new JButton("...");
		btnChooseCatalog.setPreferredSize(new Dimension(20, 20));
		btnChooseCatalog.setFont(new Font(null));
		btnChooseCatalog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser dirOpen = new JFileChooser();
				dirOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				dirOpen.setSelectedFile(settings.getDir());
				dirOpen.setAcceptAllFileFilterUsed(false);
				int ret = dirOpen.showDialog(null, "Open file");
				if (ret == JFileChooser.APPROVE_OPTION) {
					if (dirOpen.getSelectedFile().isDirectory()) {
						settings.setDir(dirOpen.getSelectedFile());
						textChooseCatalog.setText(settings.getDir().getAbsolutePath());
						tableModel = new CustomTableModel(settings.getDir());
						tableFiles.setModel(tableModel);
						TableAlignment();
					}
				}
			}
		});
		c.fill = GridBagConstraints.NONE;
		c.ipady = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		pane.add(btnChooseCatalog, c);
		c.insets = buttonInsets;

		final JButton btnRun = new JButton("Запуск");
		btnRun.setPreferredSize(new Dimension(100, 20));
		c.fill = GridBagConstraints.NONE;
		c.ipady = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		pane.add(btnRun, c);
		btnRun.addActionListener(this);

		final JCheckBox cbChangeName = new JCheckBox("Изменять имя на ГГГГ-ММ-ДД");
		cbChangeName.setFont(new Font(null));
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(cbChangeName, c);
		cbChangeName.setSelected(settings.isChngName());

		final JCheckBox cbChangeOnly = new JCheckBox("Изменять только файлы с именами PXXXXXXX");
		cbChangeOnly.setFont(new Font(null));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		pane.add(cbChangeOnly, c);
		cbChangeOnly.setSelected(settings.isMtchName());
		cbChangeOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.setMtchName(cbChangeOnly.isSelected());
			}
		});
		cbChangeOnly.setEnabled(cbChangeName.isSelected());

		final JCheckBox cbPostfix = new JCheckBox("Постфикс к новому имени файла");
		cbPostfix.setFont(new Font(null));
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 2;
		pane.add(cbPostfix, c);
		cbPostfix.setSelected(settings.isPostfixFile());
		cbPostfix.setEnabled(cbChangeName.isSelected());

		final JTextField textPostfix = new JTextField();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = 2;
		pane.add(textPostfix, c);
		textPostfix.setText(settings.getPostfix());
		textPostfix.setEnabled(cbPostfix.isSelected() && cbChangeName.isSelected());
		textPostfix.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				settings.setPostfix(textPostfix.getText());
			}

			public void focusGained(FocusEvent e) {
			}
		});

		cbChangeName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.setChngName(cbChangeName.isSelected());
				cbChangeOnly.setEnabled(cbChangeName.isSelected());
				cbPostfix.setEnabled(cbChangeName.isSelected());
				textPostfix.setEnabled(cbPostfix.isSelected() && cbChangeName.isSelected());
			}
		});

		cbPostfix.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.setPostfixFile(cbPostfix.isSelected());
				textPostfix.setEnabled(cbPostfix.isSelected() && cbChangeName.isSelected());
			}
		});

		final JCheckBox cbCompress = new JCheckBox("Сжимать");
		cbCompress.setFont(new Font(null));
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		pane.add(cbCompress, c);
		cbCompress.setSelected(settings.isCmprsFile());

		final SpinnerModel model = new SpinnerNumberModel(settings.getCmprsValue() * 100, 1, 99, 1);
		final JSpinner spinCompress = new JSpinner(model);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridx = 2;
		c.gridy = 3;
		pane.add(spinCompress, c);
		spinCompress.setEnabled(cbCompress.isSelected());
		spinCompress.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setCmprsValue(((Double) spinCompress.getValue()).floatValue() / 100);
			}
		});

		cbCompress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.setCmprsFile(cbCompress.isSelected());
				spinCompress.setEnabled(cbCompress.isSelected());
			}
		});

		c.insets = new Insets(10, 0, 0, 0); // Внешний отступ компонента

		JScrollPane tableScrollPane = new JScrollPane(tableFiles);
		tableScrollPane.setPreferredSize(new Dimension(100, 100));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 300;
		c.weightx = 0.0;
		c.weighty = 1.0; // При изменении высоты окна - заполнять свободное пространство этим компонентом (0.0 - 1.0 - задать по каждому компоненту)
		c.fill = GridBagConstraints.BOTH; // Заполнять все свободное пространство формы
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		pane.add(tableScrollPane, c);
		TableAlignment();

		textOut.setFont(new Font(null));
		textOut.setPreferredSize(new Dimension(0, 14));
		textOut.setEditable(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 6;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		pane.add(textOut, c);
	}

	private void TableAlignment() {
		tableFiles.getColumnModel().getColumn(3).setPreferredWidth(5);
		tableFiles.getColumnModel().getColumn(2).setPreferredWidth(20);
		tableFiles.getColumnModel().getColumn(1).setPreferredWidth(35);
		tableFiles.getColumnModel().getColumn(0).setPreferredWidth(200);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		tableFiles.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
		tableFiles.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		tableFiles.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
	}

	public void actionPerformed(ActionEvent e) {
		long timer = -System.currentTimeMillis();
		if (!settings.isChngName() && !settings.isCmprsFile()) {
			return;
		}
		messages.clear();
		compress.clear();
		if (settings.getDir().isDirectory() && tableModel.getJpgFiles().length > 0) {
			ExecutorService exec = Executors.newFixedThreadPool(10);
			// ExecutorService exec = Executors.newCachedThreadPool();
			// ExecutorService exec = Executors.newSingleThreadExecutor();
			for (File fileP : tableModel.getJpgFiles()) {
				exec.execute(new CompressRenameFile(fileP, settings));
			}
			exec.shutdown();
			try {
				exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException ie) {
			}
			timer += System.currentTimeMillis();
			ChangeNamePhotoes.messages.add("Время выполнения: " + new SimpleDateFormat("mm:ss.SSS").format(new Date(timer)));
			System.out.println("Время выполнения: " + new SimpleDateFormat("mm:ss.SSS").format(new Date(timer)));
			textOut.setText(messages.get(messages.size() - 1));
			tableModel = new CustomTableModel(settings.getDir());
			tableFiles.setModel(tableModel);
			TableAlignment();
		}
	}

	private static void createAndShowGUI() {
		// Создание окна
		final JFrame frame = new JFrame("Сжатие/переименоватие jpg-файлов");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// ImageIcon icon = new ImageIcon("d://ProgJava//ChangeNamePhotoes//src//main//resources//img//PM6DOCS.ICO");
		// ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("d:\\ProgJava\\ChangeNamePhotoes\\src\\main\\resources\\Frame.ico"));
		ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("Frame.ico"));
		frame.setIconImage(icon.getImage());
		// Установить панель содержания
		ChangeNamePhotoes cnp = new ChangeNamePhotoes();
		cnp.addComponentsToPane(frame.getContentPane());
		// Показать окно
		frame.setLocation(700, 300);
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				settings.saveSettings();
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) throws IOException {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
