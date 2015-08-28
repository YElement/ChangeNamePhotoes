package yy.home;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ChangeNamePhotoes implements ActionListener {
	private File dir = new File(System.getProperty("user.dir")); // �������
	private boolean chngName = true; // �������� ��� �����
	private boolean mtchName = true; // ��������������� ������ ����� ���� Pxxxxxxx.jpg (����������)
	private float cmprsFile = 0.8f; // ������� ���� (���� 0 - ��� ������)
	private String prefix = ""; // ������� ����� ������ �����

	public void addComponentsToPane(Container pane) {
		final Insets buttonInsets = new Insets(4, 4, 4, 8);

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(0, 8, 0, 0);
		final JTextField textChooseCatalog = new JTextField();
		textChooseCatalog.setFont(new Font(null));
		textChooseCatalog.setEditable(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(textChooseCatalog, c);

		JButton btnChooseCatalog = new JButton("...");
		btnChooseCatalog.setPreferredSize(new Dimension(20, 20));
		btnChooseCatalog.setFont(new Font(null));
		btnChooseCatalog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser dirOpen = new JFileChooser();
				dirOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				dirOpen.setAcceptAllFileFilterUsed(false);
				int ret = dirOpen.showDialog(null, "Open file");
				if (ret == JFileChooser.APPROVE_OPTION) {
					dir = dirOpen.getSelectedFile();
					textChooseCatalog.setText(dir.getAbsolutePath());

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

		JButton btnRun = new JButton("������");
		btnRun.setPreferredSize(new Dimension(100, 20));
		btnRun.addActionListener(this);
		c.fill = GridBagConstraints.NONE;
		c.ipady = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		pane.add(btnRun, c);

		JCheckBox cbChangeName = new JCheckBox("�������� ��� �� ����-��-��");
		cbChangeName.setFont(new Font(null));
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(cbChangeName, c);

		JCheckBox cbChangeOnly = new JCheckBox("�������� ������ ����� � ������� PXXXXXXX");
		cbChangeOnly.setFont(new Font(null));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		pane.add(cbChangeOnly, c);

		JCheckBox cbPrefix = new JCheckBox("�������� � ������ ����� �����");
		cbPrefix.setFont(new Font(null));
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 2;
		pane.add(cbPrefix, c);

		JTextField textPrefix = new JTextField();
		textPrefix.setText("_");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = 2;
		pane.add(textPrefix, c);

		JCheckBox cbCompress = new JCheckBox("�������");
		cbCompress.setFont(new Font(null));
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		pane.add(cbCompress, c);

		JSpinner spinCompress = new JSpinner();
		spinCompress.setValue(80);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridx = 2;
		c.gridy = 3;
		pane.add(spinCompress, c);

		c.insets = new Insets(10, 0, 0, 0); // ������� ������ ����������

		JTable tableFiles = new JTable();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 300;
		c.weightx = 0.0;
		c.weighty = 1.0; // ��� ��������� ������ ���� - ��������� ��������� ������������ ���� ����������� (0.0 - 1.0 - ������ �� ������� ����������)
		c.fill = GridBagConstraints.BOTH; // ��������� ��� ��������� ������������ �����
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		pane.add(tableFiles, c);

		final JTextField textOut = new JTextField();
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

	public void actionPerformed(ActionEvent e) {
		long timer = -System.currentTimeMillis();
		if (!chngName && cmprsFile == 0) {
			return;
		}
		// ��������� ��� �����
		if (dir.isDirectory()) {
			File f[] = dir.listFiles();
			ExecutorService exec = Executors.newFixedThreadPool(10);
			// ExecutorService exec = Executors.newCachedThreadPool();
			// ExecutorService exec = Executors.newSingleThreadExecutor();
			for (File fileP : f) {
				exec.execute(new CompressRenameFile(fileP, chngName, mtchName, cmprsFile, prefix));
			}
			exec.shutdown();
			try {
				exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException ie) {
			}
			timer += System.currentTimeMillis();
			System.out.println("����� ����������: " + new SimpleDateFormat("mm:ss.SSS").format(new Date(timer)));
		}
	}

	private static void createAndShowGUI() {
		// �������� ����
		JFrame frame = new JFrame("������/�������������� jpg-������");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ���������� ������ ����������
		ChangeNamePhotoes cnp = new ChangeNamePhotoes();
		cnp.addComponentsToPane(frame.getContentPane());
		// �������� ����
		frame.setLocation(700, 300);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		Settings settings = new Settings(new File ("ChangeNamePhotoes.ini"));
		
		//File dir = new File("D:\\ProgJava\\ChangeNamePhotoes\\_Photoes\\");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
