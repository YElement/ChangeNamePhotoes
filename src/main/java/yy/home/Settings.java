package yy.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	private File dir = new File(System.getProperty("user.dir")); // Каталог
	private boolean chngName = true; // Изменять имя файла
	private boolean mtchName = true; // Переименовывать только файлы вида Pxxxxxxx.jpg (фотографии)
	private float cmprsFile = 0.8f; // Сжимать файл (если 0 - без сжатия)
	private String postfix = ""; // Префикс перед именем файла

	public Settings(File fileSettings) {
		Properties props = new Properties();
		if (!fileSettings.exists()) {
			try {
				fileSettings.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			props.load(new FileInputStream(fileSettings));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dir = new File(props.getProperty("Dir"));
		chngName = Boolean.valueOf(props.getProperty("Change_name", "0"));
		mtchName = Boolean.valueOf(props.getProperty("Match_name", "0"));
		cmprsFile = Float.valueOf(props.getProperty("Compress_value", "0.8f"));
		postfix = props.getProperty("Postfix");

	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public boolean isChngName() {
		return chngName;
	}

	public void setChngName(boolean chngName) {
		this.chngName = chngName;
	}

	public boolean isMtchName() {
		return mtchName;
	}

	public void setMtchName(boolean mtchName) {
		this.mtchName = mtchName;
	}

	public float getCmprsFile() {
		return cmprsFile;
	}

	public void setCmprsFile(float cmprsFile) {
		this.cmprsFile = cmprsFile;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String prefix) {
		this.postfix = postfix;
	}

}
