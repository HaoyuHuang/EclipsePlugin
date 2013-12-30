package edu.usc.haoyu.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import edu.usc.haoyu.utils.BuilderResource;
/**
 * @author Haoyu
 *
 */
public class BuilderEngine {

	private boolean generateBuilder = false;

	private boolean generateJSON = false;

	private String className;

	private String packageName;

	private List<String> srcClzMembers;

	private List<ClassMember> clzMembers;

	private String path;

	private static final String INTERFACE_BUILDER = "Builder";

	/**
	 * @param generateBuilder
	 * @param genereateJSON
	 * @param className
	 * @param packageName
	 * @param clzMembers
	 */
	public BuilderEngine(boolean generateBuilder, boolean genereateJSON,
			String className, String packageName, List<String> clzMembers) {
		super();
		this.generateBuilder = generateBuilder;
		this.generateJSON = genereateJSON;
		this.packageName = packageName;
		this.className = className;
		this.path = getAbsolutePath();
		this.srcClzMembers = clzMembers;
	}

	/**
	 * @param className
	 * @param packageName
	 * @param clzMembers
	 */
	public BuilderEngine(String className, String packageName,
			List<String> clzMembers) {
		super();
		this.generateBuilder = true;
		this.generateJSON = true;
		this.className = className;
		this.packageName = packageName;
		this.path = getAbsolutePath();
		this.srcClzMembers = clzMembers;
	}

	/**
	 * @throws IOException
	 */
	public void generate() throws IOException {
		File srcFile = new File(getAbsolutePath(), className + ".java");
		if (!srcFile.exists()) {
			srcFile.createNewFile();
		}
		StringBuilder builder = new StringBuilder();
		builder.append("package ");
		builder.append(packageName);
		builder.append(";");
		if (generateJSON) {
			builder.append("import org.json.JSONException;");
			builder.append("import org.json.JSONObject;");
		}
		builder.append("/**\n" + "* Auto-generated by Buidler Engine\n" + "*\n"
				+ "* @author haoyu\n" + "*\n" + "*/");
		builder.append("public class ");
		builder.append(className);
		builder.append("{");

		clzMembers = new ArrayList<ClassMember>();
		for (String em : srcClzMembers) {
			ClassMember cm = new ClassMember(em);
			cm.convert();
			clzMembers.add(cm);
		}

		String variables = generateVariables();
		builder.append(variables);

		if (generateBuilder) {
			createBuilderInterface();
			String privateConstructor = generatePrivateConstructor();
			String builderClass = generateBuilder();
			builder.append(privateConstructor);
			builder.append(builderClass);
		}

		if (generateJSON) {
			BuilderResource.writeJSONJarFile(getSelectedProjectAbsolutePath());
			String toJSON = generateToJSON();
			String fromJSON = generateFromJSON();
			builder.append(toJSON);
			builder.append(fromJSON);
		}

		builder.append("}");
		write(srcFile, builder);
	}

	private String generateVariables() {
		StringBuilder vbuilder = new StringBuilder();
		for (ClassMember cm : clzMembers) {
			vbuilder.append(cm.getVariableScope());
			vbuilder.append(" ");
			vbuilder.append(cm.getVariableDeclarationStatic());
			vbuilder.append(" ");
			vbuilder.append(cm.getVariableDeclaration2());
			vbuilder.append(" ");
			vbuilder.append(cm.getVariableType());
			vbuilder.append(" ");
			vbuilder.append(cm.getVariableName());
			vbuilder.append(";");
		}
		return vbuilder.toString();
	}

	/**
	 * private ClassName(Type parameter) { this.parameter = parameter; }
	 */
	private String generatePrivateConstructor() {
		String builderClassName = className + "Builder";
		String builderParaName = className + "builder";

		StringBuilder constructor = new StringBuilder();
		constructor.append("private ");
		constructor.append(className);
		constructor.append("(");
		constructor.append(builderClassName);
		constructor.append(" ");
		constructor.append(builderParaName);
		constructor.append(")");
		constructor.append("{");

		for (ClassMember cm : clzMembers) {
			constructor.append(builderParaName);
			constructor.append(".");
			constructor.append(cm.getVariableName());
			constructor.append("=");
			constructor.append(cm.getVariableName());
			constructor.append(";");
		}

		constructor.append("}");

		return constructor.toString();
	}

	private void createBuilderInterface() throws IOException {
		File file = new File(getAbsolutePath(), INTERFACE_BUILDER + ".java");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw e;
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append("package ");
		builder.append(packageName);
		builder.append(";");

		builder.append("/**\n" + "* Auto-generated by Buidler Engine\n" + "*\n"
				+ "* @author haoyu\n" + "*\n" + "*/");

		builder.append(ClassMember.DECLARATIVE_PUBLIC);
		builder.append(" interface ");
		builder.append(INTERFACE_BUILDER);
		builder.append("<T> {");
		builder.append("public T build();");
		builder.append("}");

		write(file, builder);
	}

	private void write(File file, StringBuilder builder) throws IOException {
		FileWriter writer = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(writer);
		bw.write(formatSourceCode(builder.toString()));
		bw.close();
	}

	/**
	 * public static class ClassBuilder implements
	 */
	private String generateBuilder() {
		String builderClassName = className + "Builder";

		StringBuilder builder = new StringBuilder();
		builder.append("public static class ");
		builder.append(builderClassName);
		builder.append(" implements Builder");
		builder.append("<");
		builder.append(className);
		builder.append(">");
		builder.append(" {");

		for (ClassMember cm : clzMembers) {
			builder.append(ClassMember.DECLARATIVE_PRIVATE);
			builder.append(" ");
			builder.append(cm.getVariableType());
			builder.append(" ");
			builder.append(cm.getVariableName());
			builder.append(";");
		}

		// public BuilderClassName memberName(Type parameter) {
		// this.memberName = parameter;
		// return this;
		// }
		for (ClassMember cm : clzMembers) {
			builder.append(ClassMember.DECLARATIVE_PUBLIC);
			builder.append(" ");
			builder.append(builderClassName);
			builder.append(" ");
			builder.append(BuilderResource.capatizeFirstCharacter(cm
					.getVariableName()));
			builder.append("(");
			builder.append(cm.getVariableType());
			builder.append(" ");
			builder.append(cm.getVariableName());
			builder.append(") {");
			builder.append("this.");
			builder.append(cm.getVariableName());
			builder.append("=");
			builder.append(cm.getVariableName());
			builder.append(";");
			builder.append("return this;");
			builder.append("}");
		}

		// public Type build() {
		// return new Type();
		// }
		builder.append(ClassMember.DECLARATIVE_PUBLIC);
		builder.append(" ");
		builder.append(className);
		builder.append(" build() {");
		builder.append("return new ");
		builder.append(className);
		builder.append("(this);");
		builder.append("}");

		builder.append("}");

		return builder.toString();
	}

	private String generateToJSON() {
		StringBuilder json = new StringBuilder();

		json.append("public String toJSON() throws JSONException {");
		json.append("JSONObject json = new JSONObject();");
		for (ClassMember cm : clzMembers) {
			json.append("json.put(");
			json.append("\"");
			json.append(cm.getVariableName());
			json.append("\", ");
			json.append("this.");
			if (cm.isPrimitiveType() || cm.isList()) {
				json.append(cm.getVariableName());
			} else {
				json.append("toString()");
			}
			json.append(");");
		}
		json.append("return json.toString();");
		json.append("} ");
		return json.toString();
	}

	private String generateFromJSON() {
		StringBuilder json = new StringBuilder();
		json.append("public ");
		json.append(className);
		json.append(" fromJSON(JSONObject obj) throws JSONException { ");
		json.append("if (obj == null) {return null;}");
		for (ClassMember cm : clzMembers) {
			json.append("this.");
			json.append(cm.getVariableName());
			json.append("= (");
			json.append(cm.getVariableType());
			json.append(") obj.get(");
			json.append("\"");
			json.append(cm.getVariableName());
			json.append("\");");
		}
		json.append("return this;");
		json.append("} ");
		return json.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> v = new ArrayList<String>();
		v.add("public String name");
		String path = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		System.out.println(path);
		BuilderEngine engine = new BuilderEngine("JavaBean",
				"edu.usc.haoyu.handler", v);
		try {
			engine.generate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private String getAbsolutePath() {
		return SourceBuilderHandler.getSelectedFileAbsolutePath();
	}

	private String getSelectedProjectAbsolutePath() {
		return SourceBuilderHandler.getSelectedProjectAbsolutePath();
	}

}
