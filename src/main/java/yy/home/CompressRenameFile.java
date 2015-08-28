package yy.home;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class CompressRenameFile implements Runnable {

	/////////private static int taskCount = 0;
	//private final int id = taskCount++;
	private File fileP; // Изменяемый файл
	private boolean chngName; // Изменять имя файла
	private boolean mtchName; // Переименовывать только файлы вида Pxxxxxxx.jpg (фотографии)
	private float cmprsFile; // Сжимать файл (если 0 - без сжатия)
	private String prefix; // Префикс перед именем файла

	public CompressRenameFile(File fileP, boolean chngName, boolean mtchName, float cmprsFile, String prefix) {
		this.fileP = fileP;
		this.chngName = chngName;
		this.mtchName = mtchName;
		this.cmprsFile = cmprsFile;
		this.prefix = prefix;
	}

	public String status() {
		// return "#" + id + "# ";
		return "";
	}

	/**
	 * Проверяет входное имя файла
	 * 
	 * @param filename
	 *            Имя проверяемого файла
	 * @return true - если проверка прошла успешно
	 */
	private boolean checkName(String filename) {
		if (!mtchName) {
			return true;
		}
		Pattern p = Pattern.compile("^P[1-9A-C]{1}[0-9]{6}$");
		Matcher m = p.matcher(filename);
		return m.matches();
	}

	private String getFileExtention(String filename) {
		int dotPos = filename.lastIndexOf(".") + 1;
		return filename.substring(dotPos);
	}

	private String getFileNameWithoutExtention(String filename) {
		int dotIdx = filename.lastIndexOf('.');
		int bslashIdx = filename.lastIndexOf('\\');
		if (dotIdx != -1) {
			return filename.substring(bslashIdx + 1, dotIdx);
		}
		return filename;
	}

	/**
	 * Сжимает jpg файл
	 * 
	 * @param uncompressFile
	 *            Старый файл, который необходимо сжать
	 * @param outFile
	 *            Новый сжатый файл
	 * @param quality
	 *            Степень сжатия
	 * @return Процент сжатия
	 */
	private int compress(File uncompressFile, File outFile, float quality) throws IOException {
		BufferedImage input = ImageIO.read(uncompressFile);
		Iterator iter = ImageIO.getImageWritersByFormatName("JPG");
		if (iter.hasNext()) {
			ImageWriter writer = (ImageWriter) iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(quality);
			FileImageOutputStream output = new FileImageOutputStream(outFile);
			writer.setOutput(output);
			IIOImage image = new IIOImage(input, null, null);
			writer.write(null, image, iwp);
			output.close();
		}
		return (int) (100.0 * (uncompressFile.length() - outFile.length()) / uncompressFile.length());
	}

	/**
	 * Возвращает файл с именем ГГГГ-ММ-ДД_х.jpg ("_х" - при необходимости)
	 * 
	 * @param oldFile
	 *            Старый файл, который необходимо переименовать
	 * @param created
	 *            Если true - создает файл
	 * @return Объект File с именем вида ГГГГ-ММ-ДД_х.jpg
	 */
	public File getRenamedfile(File oldFile, boolean created) {
		File renamedFile;
		String ext = getFileExtention(oldFile.getName()).toLowerCase();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String newFileName = oldFile.getParent() + "\\" + prefix + dateFormat.format(new Date(oldFile.lastModified()));
		// Проверить, есть ли файл с таким же названием, если есть -
		// добавить "_i" (i = 1, 2, 3.. и т.д.)
		int i = 0;
		while (true) {
			renamedFile = new File(newFileName + (i == 0 ? "" : "_" + String.valueOf(i)) + "." + ext);
			if (renamedFile.exists()) {
				i++;
				continue;
			}
			if (created) {
				try {
					if (renamedFile.createNewFile()) {
						break;
					} else {
						i++;
					}
				} catch (IOException e) {
					// i++;
					// if (i > 1000) {
					e.printStackTrace();
					// }
				}
			} else {
				break;
			}
		}

		return renamedFile;
	}

	public void run() {
		String ext = getFileExtention(fileP.getName()).toLowerCase();
		if ("jpg".equals(ext) || "jpeg".equals(ext)) {

			// Переименование, без сжатия
			if (chngName && checkName(getFileNameWithoutExtention(fileP.getName())) && cmprsFile == 0) {
				try {
					while (true) {
						File fren = getRenamedfile(fileP, false);
						if (fileP.renameTo(fren)) {
							System.out.println(status() + fileP.getName() + " -> " + fren.getName());
							break;
						}
					}
				} catch (Throwable e) {
					System.out.println(status() + fileP.getName() + " не удалось переименовать файл: " + e.toString());
				}
				;
			} else {
				// Сжатие, с переименованием
				if (chngName && checkName(getFileNameWithoutExtention(fileP.getName())) && cmprsFile != 0) {
					try {
						File fren = getRenamedfile(fileP, true);
						int cmp = compress(fileP, fren, cmprsFile);
						fren.setLastModified(fileP.lastModified());
						System.out.println(status() + fileP.getName() + " -> " + fren.getName() + " " + cmp + "%");
						fileP.delete();
					} catch (Throwable e) {
						System.out.println(status() + fileP.getName() + " не удалось сжать и переименовать файл: " + e.toString());
					}
					;
				}
				// Сжатие, без переименования
				else if (cmprsFile != 0) {
					try {
						File tempFile = File.createTempFile("tmp", "");
						int cmp = compress(fileP, tempFile, cmprsFile);
						tempFile.setLastModified(fileP.lastModified());
						Files.move(tempFile.toPath(), fileP.toPath(), StandardCopyOption.REPLACE_EXISTING);
						System.out.println(status() + fileP.getName() + " " + cmp + "%");
					} catch (Throwable e) {
						System.out.println(status() + fileP.getName() + " не удалось сжать файл: " + e.toString());
					}
					;
				}
			}
		}
	}

}
