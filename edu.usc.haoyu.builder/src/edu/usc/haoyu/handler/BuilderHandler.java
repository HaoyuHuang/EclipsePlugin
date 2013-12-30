package edu.usc.haoyu.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.usc.haoyu.view.BuilderView;
/**
 * @author Haoyu
 *
 */
@Deprecated
public class BuilderHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IPackageFragment) {
			IPackageFragment cu = (IPackageFragment) firstElement;
			BuilderView.display(cu.getElementName());
		} else {
			MessageDialog.openInformation(shell, "Info",
					"Please select a Java source file");
		}
		return null;
	}

	protected String getPersistentProperty(IResource res, QualifiedName qn) {
		try {
			return res.getPersistentProperty(qn);
		} catch (CoreException e) {
			return "";
		}
	}

	protected void setPersistentProperty(IResource res, QualifiedName qn,
			String value) {
		try {
			res.setPersistentProperty(qn, value);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private String getAbsolutePath(String packageName) {
		String binFolder = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		String srcFolder = binFolder.substring(0, binFolder.length() - 4)
				+ "src" + File.separator;
		String packageFolder = packageName.replace('.', File.separatorChar);
		return srcFolder + packageFolder + File.separator;
	}
}