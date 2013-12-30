package edu.usc.haoyu.handler;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import edu.usc.haoyu.builder.Activator;
import edu.usc.haoyu.utils.BuilderResource;
import edu.usc.haoyu.view.SourceBuilderView;
import edu.usc.haoyu.view.SourceBuilderViewInterface;

/**
 * @author Haoyu
 *
 */
public class SourceBuilderHandler extends AbstractHandler {

	private static String filePath = "";

	private static String selectedFileAbsolutePath = null;

	private static String selectedProjectAbsolutePath = null;

	private ASTBuilderEngine astBuilderEngine;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IEditorPart editorPart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IEditorInput input = editorPart.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) input).getFile();
			filePath = file.getFullPath().toString();
			IJavaElement elem = JavaCore.create(file);
			if (elem instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit) elem;
				try {
					astBuilderEngine = ASTBuilderEngine.newInstance(unit);
					astBuilderEngine.initWithFilePath(filePath);
					createSourceBuilderView();
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	private void createSourceBuilderView() {
		final List<String> methods = astBuilderEngine.getMethods();
		final String packageName = astBuilderEngine.getPackageName();
		final String className = astBuilderEngine.getClassName();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SourceBuilderView frame = new SourceBuilderView(
							packageName, className, methods, null);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					frame.registerCallback(new SourceBuilderViewInterface() {

						@Override
						public void OKPressed(boolean generateBuilder,
								boolean generateJSON, int index,
								int optionsLength) {
							// TODO Auto-generated method stub
							try {
								astBuilderEngine.create(generateBuilder,
										generateJSON, index, optionsLength);
								BuilderResource.refreshWorkspace();
							} catch (JavaModelException | IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							frame.close();
						}

						@Override
						public void CancelPressed() {
							// TODO Auto-generated method stub

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Deprecated
	private void createBuilderView(ICompilationUnit unit) {
		try {
			String packageName = unit.getPackageDeclarations()[0]
					.getElementName();
			String className = unit.getElementName();
			List<String> allfields = new ArrayList<String>();
			IType[] types = unit.getTypes();
			for (IType type : types) {
				for (IField field : type.getFields()) {
					allfields.add(field.getSource());
				}
			}
			SourceBuilderView.display(allfields, packageName,
					className.substring(0, className.length() - 5));
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static String getSelectedFileAbsolutePath() {
		if (selectedFileAbsolutePath != null) {
			return selectedFileAbsolutePath;
		}
		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// get location of workspace (java.io.File)
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String workspacePath = workspaceDirectory.getAbsolutePath();
		String fileFullPath = workspacePath + filePath;
		String[] subs = fileFullPath.split("/");
		selectedFileAbsolutePath = fileFullPath.substring(0,
				fileFullPath.length() - subs[subs.length - 1].length());
		return selectedFileAbsolutePath;
	}

	public static String getSelectedProjectAbsolutePath() {
		if (selectedProjectAbsolutePath != null) {
			return selectedProjectAbsolutePath;
		}
		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// get location of workspace (java.io.File)
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String workspacePath = workspaceDirectory.getAbsolutePath();
		String[] subs = filePath.split("/");
		selectedProjectAbsolutePath = workspacePath + "/" + subs[1];
		return selectedProjectAbsolutePath;

	}
}
