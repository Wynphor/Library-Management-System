package project.dao;

import project.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 借书证数据访问对象
 * 负责处理与借书证相关的数据库操作
 */
public class CardDao {

	/**
	 * 获取用户拥有的借书证数量
	 * @param con 数据库连接
	 * @param username 用户名
	 * @return 借书证数量
	 * @throws Exception 数据库操作异常
	 */
	public int cardNumber(Connection con, String username) throws Exception {
		String sql = "select * from card where username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		int num = 0;

		// 遍历结果集，获取行数（即借书证数量）
		while(rs.next()) {
			num = rs.getRow();
		}
		return num;
	}

	/**
	 * 为用户添加借书证
	 * @param con 数据库连接
	 * @param user 用户对象
	 * @return 新添加的借书证编号，0表示失败
	 * @throws Exception 数据库操作异常
	 */
	public int add(Connection con, User user) throws Exception {
		// 验证用户密码
		String sql = "select * from user where username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user.getUsername());
		ResultSet rs = pstmt.executeQuery();

		String password = "";
		while(rs.next()) {
			password = rs.getString("password");
		}

		// 密码验证通过后创建借书证
		if(password.equals(user.getPassword())) {
			sql = "insert into card (username) value(?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.execute();

			// 获取新创建的借书证编号
			int cardNo = 0;
			sql = "select * from card where username = '" + user.getUsername() + "'";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				cardNo = rs.getInt("cardNo");
			}
			return cardNo;
		} else {
			return 0; // 密码不匹配，创建失败
		}
	}

	/**
	 * 获取用户的所有借书证信息
	 * @param con 数据库连接
	 * @param username 用户名
	 * @return 借书证信息结果集
	 * @throws Exception 数据库操作异常
	 */
	public ResultSet listCard(Connection con, String username) throws Exception {
		String sql = "select * from card where username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, username);

		return pstmt.executeQuery();
	}
}

