package edu.usc.haoyu.handler;
/**
 * @author Haoyu
 *
 */
@Deprecated
public class ClassMember {
	public static final String DECLARATIVE_PUBLIC = "public";

	public static final String DECLARATIVE_PROTECTED = "protected";

	public static final String DECLARATIVE_PRIVATE = "private";

	public static final String DECLARATIVE_FINAL = "final";

	public static final String DECLARATIVE_STATIC = "static";

	public static final String DECLARATIVE_VOLATILE = "volatile";

	private String source;

	private String variableScope;

	private String variableName;

	private String variableType;

	private String variableDeclarationStatic = "";

	private String variableDeclaration2 = "";

	public ClassMember(String source) {
		super();
		this.source = source;
	}

	public void convert() {
		String[] ems = source.split(" ");
		variableScope = ems[0];
		variableName = ems[ems.length - 1];
		if (variableName.length() != 0
				&& variableName.charAt(variableName.length() - 1) == ';') {
			variableName = variableName.substring(0, variableName.length() - 1);
		}
		variableType = ems[ems.length - 2];
		if (ems.length == 4) {
			String temp = ems[1];
			if (DECLARATIVE_FINAL.equals(temp)) {
				variableDeclaration2 = DECLARATIVE_FINAL;
			} else if (DECLARATIVE_STATIC.equals(temp)) {
				variableDeclarationStatic = DECLARATIVE_STATIC;
			} else if (DECLARATIVE_VOLATILE.equals(temp)) {
				variableDeclaration2 = DECLARATIVE_VOLATILE;
			}
		} else if (ems.length == 5) {
			variableDeclarationStatic = DECLARATIVE_STATIC;
			variableDeclaration2 = ems[2];
		}
	}

	public boolean isPrimitiveType() {
		return "byte".equals(variableType) || "Byte".equals(variableType)
				|| "short".equals(variableType) || "Short".equals(variableType)
				|| "int".equals(variableType) || "Integer".equals(variableType)
				|| "long".equals(variableType) || "Long".equals(variableType)
				|| "double".equals(variableType)
				|| "Double".equals(variableType)
				|| "float".equals(variableType) || "Float".equals(variableType)
				|| "char".equals(variableType)
				|| "Character".equals(variableType)
				|| "String".equals(variableType)
				|| "boolean".equals(variableType)
				|| "Boolean".equals(variableType);
	}

	public boolean isList() {
		return false;
	}

	public String getSource() {
		return source;
	}

	public String getVariableScope() {
		return variableScope;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getVariableType() {
		return variableType;
	}

	public String getVariableDeclarationStatic() {
		return variableDeclarationStatic;
	}

	public String getVariableDeclaration2() {
		return variableDeclaration2;
	}

}
