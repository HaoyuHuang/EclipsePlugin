package edu.usc.haoyu.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.usc.haoyu.handler.BuilderEngine;
import edu.usc.haoyu.utils.BuilderResource;

/**
 * @author Haoyu
 *
 */
public class SourceBuilderView extends JFrame {

	private JPanel contentPane;

	@Deprecated
	private List<String> allfields;

	private String packageName;

	private String className;

	private JCheckBox chckbxGeneratebuilder;

	private JCheckBox chckbxGeneratejson;

	private JComboBox<String> comboBox;

	private List<String> methods;

	private SourceBuilderViewInterface callback;

	public SourceBuilderView(String packageName, String className,
			List<String> methods, SourceBuilderViewInterface callback)
			throws HeadlessException {
		super();
		this.packageName = packageName;
		this.className = className;
		this.methods = methods;
		this.callback = callback;
		this.init();
	}

	@Deprecated
	private BuilderEngine engine;

	@Deprecated
	public SourceBuilderView(List<String> allfields, String packageName,
			String className) throws HeadlessException {
		super();
		this.allfields = allfields;
		this.packageName = packageName;
		this.className = className;
		this.init();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		display(null, "", "");
	}

	@Deprecated
	public static void display(final List<String> allfields,
			final String packageName, final String className) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SourceBuilderView frame = new SourceBuilderView(allfields,
							packageName, className);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void display(final List<String> methods,
			final String packageName, final String className,
			final SourceBuilderViewInterface callback) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SourceBuilderView frame = new SourceBuilderView(
							packageName, className, methods, callback);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SourceBuilderView() {
		init();
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(BuilderResource.TITLE);
		this.setLocationRelativeTo(null);
		setFont(new Font("Apple LiSung", Font.BOLD, 13));
		setBounds(100, 100, 400, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		chckbxGeneratebuilder = new JCheckBox("Generate Builder Class");
		chckbxGeneratebuilder.setFont(new Font("Apple LiSung", Font.BOLD, 13));
		chckbxGeneratebuilder.setBounds(55, 17, 167, 23);
		chckbxGeneratebuilder.setSelected(true);
		contentPane.add(chckbxGeneratebuilder);

		chckbxGeneratejson = new JCheckBox("Generate JSON Method");
		chckbxGeneratejson.setFont(new Font("Apple LiSung", Font.BOLD, 13));
		chckbxGeneratejson.setBounds(55, 53, 167, 23);
		chckbxGeneratejson.setSelected(true);
		contentPane.add(chckbxGeneratejson);

		JButton btnOk = new JButton("OK");
		btnOk.setFont(new Font("Apple LiSung", Font.BOLD, 13));
		btnOk.setBounds(249, 214, 67, 23);
		contentPane.add(btnOk);
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				buildWithASTEngine();
			}
		});

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Apple LiSung", Font.BOLD, 13));
		btnCancel.setBounds(182, 214, 67, 23);
		contentPane.add(btnCancel);

		JLabel lblInsertionPoint = new JLabel("Insertion Point:");
		lblInsertionPoint.setFont(new Font("Apple LiSung", Font.PLAIN, 13));
		lblInsertionPoint.setBounds(65, 90, 112, 16);
		contentPane.add(lblInsertionPoint);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(62, 118, 187, 27);
		List<String> comboSelectItem = new ArrayList<String>();

		for (String method : methods) {
			comboSelectItem.add("After '" + method + "'");
		}

		comboSelectItem.add("First member");
		comboSelectItem.add("Last member");

		for (String str : comboSelectItem) {
			comboBox.addItem(str);
		}
		comboBox.setSelectedIndex(comboSelectItem.size() - 1);
		comboBox.setFont(new Font("Apple LiSung", Font.PLAIN, 13));
		contentPane.add(comboBox);

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				cancel();
			}
		});
	}

	private void buildWithASTEngine() {
		boolean generateJSON = chckbxGeneratejson.isSelected();
		boolean generateBuilder = chckbxGeneratebuilder.isSelected();
		int index = comboBox.getSelectedIndex();
		if (callback != null) {
			callback.OKPressed(generateBuilder, generateJSON, index,
					comboBox.getItemCount());
		}
	}

	@Deprecated
	private void buildWithBuildEngine() {
		boolean generateJSON = chckbxGeneratejson.isSelected();
		boolean generateBuilder = chckbxGeneratebuilder.isSelected();
		engine = new BuilderEngine(generateBuilder, generateJSON, className,
				packageName, allfields);
		try {
			engine.generate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cancel();
	}

	private void cancel() {
		if (callback != null) {
			callback.CancelPressed();
		}
		this.setVisible(false);
		this.dispose();
	}
	
	public void close() {
		this.setVisible(false);
		this.dispose();
	}

	public void registerCallback(SourceBuilderViewInterface callback) {
		this.callback = callback;
	}
}
