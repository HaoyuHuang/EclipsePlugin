package edu.usc.haoyu.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PopupView extends JFrame {

	private JPanel contentPane;

	private String message;
	
	/**
	 * Launch the application.
	 */
	public static void display(final String message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PopupView frame = new PopupView(message);
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
	public PopupView(String message) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblMessage = new JLabel(message);
		lblMessage.setBounds(105, 42, 204, 14);
		lblMessage.setFont(new Font("Apple LiSung", Font.BOLD, 13));
		lblMessage.setSize(lblMessage.getPreferredSize());
		
		contentPane.add(lblMessage);
		
		JButton btnOk = new JButton("OK");
		btnOk.setFont(new Font("Apple LiSung", Font.PLAIN, 13));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		btnOk.setBounds(170, 77, 67, 23);
		contentPane.add(btnOk);
	}
	
	private void exit() {
		this.setVisible(false);
		this.dispose();
	}
}
