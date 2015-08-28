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
	private File fileP; // ���������� ����
	private boolean chngName; // �������� ��� �����
	private boolean mtchName; // ��������������� ������ ����� ���� Pxxxxxxx.jpg (����������)
	private float cmprsFile; // ������� ���� (���� 0 - ��� ������)
	private String prefix; // ������� ����� ������ �����

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
	 * ��������� ������� ��� �����
	 * 
	 * @param filename
	 *            ��� ������������ �����
	 * @return true - ���� �������� ������ �������
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
	 * ������� jpg ����
	 * 
	 * @param uncompressFile
	 *            ������ ����, ������� ���������� �����
	 * @param outFile
	 *            ����� ������ ����
	 * @param quality
	 *            ������� ������
	 * @return ������� ������
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
	 * ���������� ���� � ������ ����-��-��_�.jpg ("_�" - ��� �������������)
	 * 
	 * @param oldFile
	 *            ������ ����, ������� ���������� �������������
	 * @param created
	 *            ���� true - ������� ����
	 * @return ������ File � ������ ���� ����-��-��_�.jpg
	 */
	public File getRenamedfile(File oldFile, boolean created) {
		File renamedFile;
		String ext = getFileExtention(oldFile.getName()).toLowerCase();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String newFileName = oldFile.getParent() + "\\" + prefix + dateFormat.format(new Date(oldFile.lastModified()));
		// ���������, ���� �� ���� � ����� �� ���������, ���� ���� -
		// �������� "_i" (i = 1, 2, 3.. � �.�.)
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

			// ��������������, ��� ������
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
					System.out.println(status() + fileP.getName() + " �� ������� ������������� ����: " + e.toString());
				}
				;
			} else {
				// ������, � ���������������
				if (chngName && checkName(getFileNameWithoutExtention(fileP.getName())) && cmprsFile != 0) {
					try {
						File fren = getRenamedfile(fileP, true);
						int cmp = compress(fileP, fren, cmprsFile);
						fren.setLastModified(fileP.lastModified());
						System.out.println(status() + fileP.getName() + " -> " + fren.getName() + " " + cmp + "%");
						fileP.delete();
					} catch (Throwable e) {
						System.out.println(status() + fileP.getName() + " �� ������� ����� � ������������� ����: " + e.toString());
					}
					;
				}
				// ������, ��� ��������������
				else if (cmprsFile != 0) {
					try {
						File tempFile = File.createTempFile("tmp", "");
						int cmp = compress(fileP, tempFile, cmprsFile);
						tempFile.setLastModified(fileP.lastModified());
						Files.move(tempFile.toPath(), fileP.toPath(), StandardCopyOption.REPLACE_EXISTING);
						System.out.println(status() + fileP.getName() + " " + cmp + "%");
					} catch (Throwable e) {
						System.out.println(status() + fileP.getName() + " �� ������� ����� ����: " + e.toString());
					}
					;
				}
			}
		}
	}

}
