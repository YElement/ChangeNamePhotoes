package yy.home;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

public class CustomTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	final private String[] columnNames = { "Имя файла", "Размер, байт", "Дата", "% сжатия" };
	private File[] jpgFiles;

	public File[] getJpgFiles() {
		return jpgFiles;
	}

	public CustomTableModel(File dir) {
		if (dir.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String fileName) {
					return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
				}
			};
			jpgFiles = dir.listFiles(filter);
		}
	}

	public int getRowCount() {
		return jpgFiles.length;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public Object getValueAt(int r, int c) {
		switch (c) {
		case 0:
			return jpgFiles[r].getName();
		case 1:
			return String.format("%,d", jpgFiles[r].length());
		case 2:
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			return dateFormat.format(new Date(jpgFiles[r].lastModified()));
		case 3:
			return ChangeNamePhotoes.compress.get(jpgFiles[r]);
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}
}