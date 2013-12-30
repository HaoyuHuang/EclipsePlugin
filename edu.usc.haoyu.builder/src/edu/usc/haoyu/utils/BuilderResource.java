package edu.usc.haoyu.utils;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import edu.usc.haoyu.handler.JarFileLoader;

/**
 * @author Haoyu
 *
 */
public class BuilderResource {

	public static Image removeImg;

	public static Image addImg;

	public static final String TITLE = "Generate Java Bean Builder and JSON Convertor";

	public static void load() {
		try {
			removeImg = ImageIO.read(Thread.currentThread()
					.getContextClassLoader()
					.getResource("resources/delete_icon.png"));
			addImg = ImageIO.read(Thread.currentThread()
					.getContextClassLoader()
					.getResource("resources/add_icon.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeJSONJarFile(String projectAbsoluteUrl) {
		try {
			File dir = new File(projectAbsoluteUrl + "/libs/");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(projectAbsoluteUrl + "/libs/", "org.json.jar");
			if (!file.exists()) {
				file.createNewFile();
			}

			URL url;
			url = new URL(
					"platform:/plugin/edu.usc.haoyu.builder/libs/org.json.jar");
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedInputStream br = new BufferedInputStream(inputStream);

			FileOutputStream bw = new FileOutputStream(file);

			int temp = 0;
			while ((temp = br.read()) != -1) {
				bw.write(temp);
			}

			br.close();
			bw.close();
			URL urls[] = {};
			JarFileLoader loader = new JarFileLoader(urls);
			loader.addFile("/libs/org.json.jar");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String capatizeFirstCharacter(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		char firstCharacter = str.charAt(0);
		if (firstCharacter <= 122 && firstCharacter >= 97) {
			firstCharacter = (char) (firstCharacter - 32);
		}
		return firstCharacter + str.substring(1);
	}

	public static String formatSourceCode(String source) {
		Map options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		// initialize the compiler settings to be able to format 1.5 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);

		// change the option to wrap each enum constant on a new line
		options.put(
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
				DefaultCodeFormatterConstants.createAlignmentValue(true,
						DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
						DefaultCodeFormatterConstants.INDENT_ON_COLUMN));

		// instantiate the default code formatter with the given options
		final CodeFormatter codeFormatter = ToolFactory
				.createCodeFormatter(options);

		final TextEdit edit = codeFormatter.format(
				CodeFormatter.K_COMPILATION_UNIT
						| CodeFormatter.F_INCLUDE_COMMENTS, // format a
															// compilation unit
				source, // source to format
				0, // starting position
				source.length(), // length
				0, // initial indentation
				System.getProperty("line.separator") // line separator
				);

		IDocument document = new Document(source);
		try {
			if (edit != null) {
				edit.apply(document);
			}
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (org.eclipse.jface.text.BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// display the formatted string on the System out
		return document.get();
	}

	public static void refreshWorkspace() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	public static void main(String[] args) {
		BuilderResource.load();
	}
}
