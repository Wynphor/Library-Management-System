package project.ui;
import project.dao.UserDao;
import project.model.User;
import project.utils.JDBC;
import project.utils.Str;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.event.KeyAdapter;
import javax.swing.ImageIcon;
public class Logln extends JFrame {
	private JPanel contentPane;
	private JPasswordField passwordText;
	private JTextField usernameText;
	private JDBC jdbc=new JDBC();
	private UserDao userDao=new UserDao();
	private JButton btnNewButton;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Logln frame = new Logln();
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
	public Logln() {
		setAutoRequestFocus(false);
		setTitle("登录");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 827, 588);
		contentPane = new JPanel();
		contentPane.setBorder(UIManager.getBorder("Button.border"));
		setContentPane(contentPane);		
		JLabel lblNewLabel = new JLabel("登录");
		lblNewLabel.setBounds(193, 42, 340, 68);
		lblNewLabel.setFont(new Font("楷体", Font.BOLD, 50));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lblNewLabel_1 = new JLabel("用户名:");
		JLabel lblNewLabel_2 = new JLabel("密  码:");
		lblNewLabel_1.setBounds(123, 154, 167, 51);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 30));
		lblNewLabel_2.setBounds(123, 267, 167, 51);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 30));
		passwordText = new JPasswordField();
		passwordText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar()=='\n')
				{
					btnNewButton.doClick();
				}
				if(e.getKeyCode()==38)
				{
					usernameText.requestFocus();
				}
			}
		});
		passwordText.setBounds(299, 267, 220, 51);
		passwordText.setFont(new Font("宋体", Font.PLAIN, 30));
		usernameText = new JTextField();
		usernameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar()=='\n')
				{
					btnNewButton.doClick();
				}
				if(e.getKeyCode()==40)
				{
					passwordText.requestFocus();
				}
			}
		});
		usernameText.setBounds(299, 158, 220, 49);
		usernameText.setFont(new Font("宋体", Font.PLAIN, 30));
		usernameText.setColumns(10);
		contentPane.setLayout(null);
		contentPane.add(lblNewLabel_2);
		contentPane.add(lblNewLabel_1);
		contentPane.add(passwordText);
		contentPane.add(usernameText);
		contentPane.add(lblNewLabel);
		//TODO:回车键登录
		btnNewButton = new JButton("登录");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logIn(e);
			}
		});
		btnNewButton.setFont(new Font("隶书", Font.BOLD, 40));
		btnNewButton.setBounds(228, 393, 176, 57);
		contentPane.add(btnNewButton);
		JButton btnNewButton_1 = new JButton("注册");
		btnNewButton_1.setIcon(new ImageIcon(Logln.class.getResource("/image/8666546_user_plus_icon.png")));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToRegister(e);
			}
		});
		btnNewButton_1.setFont(new Font("隶书", Font.BOLD, 40));
		btnNewButton_1.setBounds(443, 393, 186, 57);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("返回");
		btnNewButton_2.setBackground(new Color(230, 230, 230));
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Main().setVisible(true);
			}
		});
		btnNewButton_2.setBounds(374, 483, 99, 40);
		contentPane.add(btnNewButton_2);
		
		this.setLocationRelativeTo(null);
	}

	protected void goToRegister(ActionEvent e) {
		new Register().setVisible(true);
	}
	protected void gotoInitial(ActionEvent e) {
		Main frame=new Main();
		frame.setVisible(true);
	}
	private void logIn(ActionEvent evt) {
		String username=this.usernameText.getText();
		String password=new String(this.passwordText.getPassword());
		if(Str.isEmpty(username))
		{
			JOptionPane.showMessageDialog(null, "用户名不能为空！");
			return;
		}
		if(Str.isEmpty(password))
		{
			JOptionPane.showMessageDialog(null, "密码不能为空！");
			return;
		}
		User user=new User(username,password);
		Connection con=null;
		try {
			con=jdbc.getCon();
			User resultUser=userDao.login(con, user);
			if(resultUser==null)
			{
				JOptionPane.showMessageDialog(null, "用户名不存在或密码错误！");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "登录成功！");
				dispose();
				MainFrame mainFrame=new MainFrame();
				mainFrame.setVisible(true);
				mainFrame.usernameText.setText(username);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				jdbc.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return;
	}

}
