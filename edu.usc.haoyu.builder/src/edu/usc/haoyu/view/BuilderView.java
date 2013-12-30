package edu.usc.haoyu.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import edu.usc.haoyu.handler.BuilderEngine;

/**
 * @author Haoyu
 *
 */
@Deprecated()
public class BuilderView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4753110878434461930L;

	private JPanel contentPane;
	private JTextField textField;
	private JCheckBox chckbxGeneratebuilder;
	private JCheckBox chckbxGeneratejson;

	private ArrayList<BuilderViewListElement> variableList = new ArrayList<BuilderViewListElement>();
	private BuilderEngine builderEngine;
	private String packageName;

	public BuilderView(String packageName) throws HeadlessException {
		super();
		this.packageName = packageName;
	}

	/**
	 * Launch the application.
	 */
	public static void display(final String packageName) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuilderView frame = new BuilderView(packageName);
					frame.init();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		display("");
	}

	public BuilderView() {
		init();
	}

	/**
	 * Create the frame.
	 */
	public void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();

		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblJavaClassName = new JLabel("Java Class name:");
		lblJavaClassName.setBounds(23, 11, 105, 16);
		panel.add(lblJavaClassName);

		textField = new JTextField();
		textField.setBounds(133, 5, 134, 28);
		panel.add(textField);
		textField.setColumns(10);

		JLabel lblClassMemberDeclaration = new JLabel(
				"Class Member Declaration:");
		lblClassMemberDeclaration.setBounds(23, 39, 177, 16);
		panel.add(lblClassMemberDeclaration);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(23, 67, 244, 200);

		panel_1.setAutoscrolls(true);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 1.0 };
		panel_1.setLayout(gridBagLayout);

		JScrollPane scrollPan = new JScrollPane(panel_1);
		scrollPan.setBounds(23, 67, 244, 200);
		panel.add(scrollPan);

		addRow(panel_1, 10);

		chckbxGeneratejson = new JCheckBox("generateJSON");
		chckbxGeneratejson.setSelected(true);
		chckbxGeneratejson.setBounds(23, 279, 128, 23);
		panel.add(chckbxGeneratejson);

		chckbxGeneratebuilder = new JCheckBox("generateBuilder");
		chckbxGeneratebuilder.setSelected(true);
		chckbxGeneratebuilder.setBounds(23, 314, 143, 23);
		panel.add(chckbxGeneratebuilder);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				build();
			}
		});
		btnOk.setBounds(197, 394, 70, 29);
		panel.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		btnCancel.setBounds(121, 394, 70, 29);
		panel.add(btnCancel);
	}

	private void addRow(final JPanel panel_1, int num) {
		for (int i = 0; i < num; i++) {
			GridBagConstraints gc = new GridBagConstraints();
			gc.insets = new Insets(0, 0, 5, 0);
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = i;
			BuilderViewListElement em = new BuilderViewListElement(i,
					new BuilderViewListElement.Callback() {

						@Override
						public void remove(int index) {
							// TODO Auto-generated method stub
							BuilderViewListElement em = variableList.get(index);
							panel_1.remove(em.getPanel());
						}

						@Override
						public void add(int index) {
							// TODO Auto-generated method stub
							addRowWithIndex(panel_1, index + 1);
						}
					});
			variableList.add(em);
			panel_1.add(em.getPanel(), gc);
		}
	}

	private void addRowWithIndex(final JPanel panel_1, int index) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(0, 0, 5, 0);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = index;
		BuilderViewListElement em = new BuilderViewListElement(index,
				new BuilderViewListElement.Callback() {

					@Override
					public void remove(int index) {
						BuilderViewListElement em = variableList.get(index);
						panel_1.remove(em.getPanel());
					}

					@Override
					public void add(int index) {
						// TODO Auto-generated method stub
						addRowWithIndex(panel_1, index + 1);
					}
				});
		variableList.add(index, em);
		// TODO: need to check
		panel_1.add(em.getPanel(), gc, index);
		panel_1.validate();
	}

	private void build() {
		boolean generateJSON = chckbxGeneratejson.isSelected();
		boolean generateBuilder = chckbxGeneratebuilder.isSelected();
		String clzName = textField.getText();
		List<String> clzMembers = new ArrayList<String>();
		for (BuilderViewListElement em : variableList) {
			if (validate(em.getText())) {
				clzMembers.add(em.getText());
				System.out.println(em.getText());
			}
		}
		builderEngine = new BuilderEngine(generateBuilder, generateJSON,
				clzName, packageName, clzMembers);
		try {
			builderEngine.generate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		exit();
	}

	private boolean validate(String text) {
		if (text != "" && text.length() != 0) {
			String[] sub = text.split(" ");
			if (sub.length >= 3) {
				if (sub[0].equals("public") || sub[0].equals("protected")
						|| sub[0].equals("private")) {
					return true;
				}
			}
		}
		return false;
	}

	private void exit() {
		this.setVisible(false);
		this.dispose();
	}
}
