package yy.home;

import java.io.File;
import java.util.prefs.Preferences;

public class Settings {
	private Preferences userPrefs;
	private File dir; // Рабочий каталог
	private boolean chngName; // Изменять имя файла
	private boolean mtchName; // Переименовывать только файлы вида Pxxxxxxx.jpg (фотографии)
	private boolean cmprsFile; // Сжимать файл
	private float cmprsValue; // Степень сжатия
	private boolean postfixFile; // Необходим ли постфикс после имени файла
	private String postfix; // Постфикс после имени файла

	public Settings() {
		userPrefs = Preferences.userRoot().node("changenamephotoes"); // HKEY_CURRENT_USER\Software\JavaSoft\Prefs\changenamephotoes
		String path = userPrefs.get("dir", "");
		if (path.equals("")) {
			dir = new File(System.getProperty("user.home")); // user.dir
		} else {
			try {
				dir = new File(path);
				if (!dir.exists()) {
					throw new Exception();
				}
			} catch (Exception e) {
				dir = new File(System.getProperty("user.home")); // user.dir
			}
		}
		chngName = userPrefs.getBoolean("change_name", true);
		mtchName = userPrefs.getBoolean("match_name", true);
		cmprsFile = userPrefs.getBoolean("compress_file", true);
		cmprsValue = userPrefs.getFloat("compress_value", 0.8f);
		postfixFile = userPrefs.getBoolean("postfix_file", false);
		postfix = userPrefs.get("postfix_value", "_");
	}

	public void saveSettings() {
		userPrefs.put("dir", dir.getPath());
		userPrefs.putBoolean("change_name", chngName);
		userPrefs.putBoolean("match_name", mtchName);
		userPrefs.putBoolean("compress_file", cmprsFile);
		userPrefs.putFloat("compress_value", cmprsValue);
		userPrefs.putBoolean("postfix_file", postfixFile);
		userPrefs.put("postfix_value", postfix);
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

	public boolean isCmprsFile() {
		return cmprsFile;
	}

	public void setCmprsFile(boolean cmprsFile) {
		this.cmprsFile = cmprsFile;
	}

	public float getCmprsValue() {
		return cmprsValue;
	}

	public void setCmprsValue(float cmprsFile) {
		this.cmprsValue = cmprsFile;
	}

	public boolean isPostfixFile() {
		return postfixFile;
	}

	public void setPostfixFile(boolean postfixFile) {
		this.postfixFile = postfixFile;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}
}
