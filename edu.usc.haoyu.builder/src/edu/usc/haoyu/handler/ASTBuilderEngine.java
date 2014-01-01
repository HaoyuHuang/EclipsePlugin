package edu.usc.haoyu.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import edu.usc.haoyu.utils.BuilderResource;
import edu.usc.haoyu.utils.FileUtils;

/**
 * @author Haoyu
 * 
 */
public class ASTBuilderEngine {

	/**
	 * @param compilationUnit
	 * @throws JavaModelException
	 */
	private ASTBuilderEngine(ICompilationUnit compilationUnit)
			throws JavaModelException {
		astBuilder = ASTBuilder.newInstance(compilationUnit);
	}

	/**
	 * @param compilationUnit
	 * @return
	 * @throws JavaModelException
	 */
	public static ASTBuilderEngine newInstance(ICompilationUnit compilationUnit)
			throws JavaModelException {
		return new ASTBuilderEngine(compilationUnit);
	}

	public static final String BUILDER_INTERFACE_NAME = "Builder";

	private String selectedFilePath = null;

	private String selectedFileAbsolutePath = null;

	private String selectedProjectAbsolutePath = null;

	private ICompilationUnit compilationUnit;

	private ASTBuilder astBuilder;

	/**
	 * @param filePath
	 * @param compilationUnit
	 */
	public void initWithFilePath(String filePath) {
		this.selectedFilePath = filePath;
	}

	/**
	 * @param createBuilder
	 * @param createJSONMethods
	 * @param index
	 * @param optionsLength
	 * @return
	 * @throws IOException
	 * @throws JavaModelException
	 */
	public boolean create(boolean createBuilder, boolean createJSONMethods,
			int index, int optionsLength) throws IOException,
			JavaModelException {

		File selectedFile = FileUtils.createNewFile(
				getSelectedFileAbsolutePath(), astBuilder.getClassName()
						+ ".java");

		if (astBuilder.containsAllFields()) {
			return false;
		}

		boolean firstMember = false;
		boolean lastMember = false;

		if (index == optionsLength - 1) {
			lastMember = true;
		}

		if (index == optionsLength - 2) {
			firstMember = true;
		}

		if (createBuilder) {
			File file = FileUtils.createNewFile(getSelectedFileAbsolutePath(),
					BUILDER_INTERFACE_NAME + ".java");
			String content = astBuilder.createBuilderInterface();
			FileUtils.write(file, formatSourceCode(content));

			index = astBuilder.createInnerBuilderClass(index, firstMember,
					lastMember);

		}

		if (createJSONMethods) {
			BuilderResource.writeJSONJarFile(getSelectedProjectAbsolutePath());
			index = astBuilder.createInnerJSONMethods(index + 1);
		}

		if (astBuilder.getContent() != null) {
			FileUtils.write(selectedFile,
					formatSourceCode(astBuilder.getContent()));
		}
		return true;
	}

	public String getClassName() {
		try {
			return astBuilder.getClassName();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getPackageName() {
		try {
			return astBuilder.getPackageName();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getMethods() {
		try {
			return astBuilder.getMethodDeclarations();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean containsAllFields() throws JavaModelException {
		return astBuilder.containsAllFields();
	}

	public void removeDuplicates(boolean createBuilder,
			boolean createJSONMethods) throws JavaModelException {
		if (createBuilder) {
			astBuilder.removeInnerBuilderClass();
		}
		if (createJSONMethods) {
			astBuilder.removeInnerJSONMethods();
		}
	}

	private String formatSourceCode(String source) {
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

	private String getSelectedFileAbsolutePath() {
		if (selectedFileAbsolutePath != null) {
			return selectedFileAbsolutePath;
		}
		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// get location of workspace (java.io.File)
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String workspacePath = workspaceDirectory.getAbsolutePath();
		String fileFullPath = workspacePath + selectedFilePath;
		String[] subs = fileFullPath.split("/");
		selectedFileAbsolutePath = fileFullPath.substring(0,
				fileFullPath.length() - subs[subs.length - 1].length());
		return selectedFileAbsolutePath;
	}

	private String getSelectedProjectAbsolutePath() {
		if (selectedProjectAbsolutePath != null) {
			return selectedProjectAbsolutePath;
		}
		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// get location of workspace (java.io.File)
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String workspacePath = workspaceDirectory.getAbsolutePath();
		String[] subs = selectedFilePath.split("/");
		selectedProjectAbsolutePath = workspacePath + "/" + subs[1];
		return selectedProjectAbsolutePath;

	}
}
