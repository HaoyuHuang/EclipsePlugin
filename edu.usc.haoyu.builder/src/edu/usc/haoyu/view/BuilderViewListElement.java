package edu.usc.haoyu.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.usc.haoyu.utils.BuilderResource;

@Deprecated
public class BuilderViewListElement {

	private int index;

	private JPanel panel;

	private JTextField textField;

	private JButton removeBtn;

	private JButton addBtn;

	private Callback callback = null;

	public BuilderViewListElement(final int index, final Callback callback) {
		super();
		this.index = index;
		this.panel = new JPanel(new FlowLayout());
		this.textField = new JTextField(8);
		this.removeBtn = new JButton();
		this.addBtn = new JButton();
		this.callback = callback;

		removeBtn.setIcon(new ImageIcon(BuilderResource.removeImg));
		addBtn.setIcon(new ImageIcon(BuilderResource.addImg));

		this.panel.add(textField);
		this.panel.add(removeBtn);
		this.panel.add(addBtn);

		removeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.remove(index);
				}
			}
		});

		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				callback.add(index);
			}
		});
	}

	public interface Callback {
		public void add(int index);

		public void remove(int index);
	}

	public String getText() {
		return textField.getText();
	}

	public void registerCallback(Callback callback) {
		this.callback = callback;
	}

	public int getIndex() {
		return index;
	}

	public JPanel getPanel() {
		return panel;
	}

}
