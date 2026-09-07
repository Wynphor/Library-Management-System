package project.dao;
import project.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * 借阅记录数据访问对象
 * 负责处理与借阅记录、借书证和用户相关的数据库操作
 */
public class RecordDao {

	/**
	 * 添加借阅记录
	 * @param con 数据库连接
	 * @param cardNo 借书证号
	 * @param no 图书编号
	 * @return 操作结果（0表示成功）
	 * @throws Exception 数据库操作异常
	 */
	public int add(Connection con, int cardNo, String no) throws Exception {
		// 插入借阅记录
		String sql = "insert into record (cardNo,no) value(?,?)";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, cardNo);
		pstmt.setString(2, no);
		pstmt.executeUpdate();

		// 更新借书证已借数量
		sql = "update card set amount = amount + 1 where cardNo = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, cardNo);
		pstmt.execute();

		// 更新图书库存
		sql = "update book set storage = storage - 1 where no = '"+no+"'";
		pstmt = con.prepareStatement(sql);
		pstmt.execute();
		return 0;
	}

	/**
	 * 删除借阅记录（还书）
	 * @param con 数据库连接
	 * @param cardNo 借书证号
	 * @param no 图书编号
	 * @return 删除的记录数
	 * @throws Exception 数据库操作异常
	 */
	public int remove(Connection con, int cardNo, String no) throws Exception {
		// 删除借阅记录
		String sql = "delete from record where cardNo = ? and no = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, cardNo);
		pstmt.setString(2, no);
		int num = pstmt.executeUpdate();

		// 更新借书证已借数量
		sql = "update card set amount = amount - ? where cardNo = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, num);
		pstmt.setInt(2, cardNo);
		pstmt.execute();

		// 更新图书库存
		sql = "update book set storage = storage + ? where no = '"+no+"'";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, num);
		pstmt.execute();
		return num;
	}

	/**
	 * 归还指定借书证的所有图书
	 * @param con 数据库连接
	 * @param cardNo 借书证号
	 * @return 操作结果（0表示无操作，1表示成功归还，-1表示借书证不存在）
	 * @throws Exception 数据库操作异常
	 */
	public int returnAll(Connection con, int cardNo) throws Exception {
		// 检查借书证是否存在
		String sql = "select * from card where cardNo = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, cardNo);
		ResultSet rs = pstmt.executeQuery();
		if(!rs.next()) {
			return -1; // 没有该借书证
		}

		// 获取该借书证的所有借阅记录
		sql = "select * from record where cardNo = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, cardNo);
		rs = pstmt.executeQuery();

		int flag = 0;
		// 遍历所有借阅记录，更新图书库存
		while(rs.next()) {
			String no = rs.getString("no");
			sql = "update book set storage = storage + 1 where no = '"+no+"'";
			pstmt = con.prepareStatement(sql);
			pstmt.execute();
			flag = 1;
		}

		// 如果有图书归还，则删除借阅记录并更新借书证已借数量
		if(flag == 1) {
			sql = "delete from record where cardNo = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, cardNo);
			int num = pstmt.executeUpdate();

			sql = "update card set amount = amount - ? where cardNo = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setInt(2, cardNo);
			pstmt.execute();
		}
		return flag;
	}

	/**
	 * 注销借书证
	 * @param con 数据库连接
	 * @param cardNo 借书证号
	 * @return 操作结果（0表示成功注销，1表示有书未还，-1表示借书证不存在）
	 * @throws Exception 数据库操作异常
	 */
	public int deleteCard(Connection con, int cardNo) throws Exception {
		// 检查借书证是否存在
		String sql = "select * from card where cardNo = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, cardNo);
		ResultSet rs = pstmt.executeQuery();
		if(!rs.next()) {
			return -1; // 没有该借书证
		} else {
			// 检查是否有未归还的图书
			if(rs.getInt("amount") != 0) {
				return 1; // 有书未还
			} else {
				// 删除借书证记录
				sql = "delete from card where cardNo = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, cardNo);
				pstmt.execute();
				return 0; // 成功注销
			}
		}
	}

	/**
	 * 删除用户账户
	 * @param con 数据库连接
	 * @param username 用户名
	 * @return 操作结果（0表示删除成功，-1表示用户不存在）
	 * @throws Exception 数据库操作异常
	 */
	public int deleteUser(Connection con, String username) throws Exception {
		// 检查用户是否存在
		String sql = "select * from user where username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		if(!rs.next()) {
			return -1; // 没有该用户
		} else {
			// 获取用户的所有借书证
			sql = "select * from card where username = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();

			// 处理每个借书证（归还所有图书并注销）
			while(rs.next()) {
				returnAll(con, rs.getInt("cardNo"));
				deleteCard(con, rs.getInt("cardNo"));
			}

			// 删除用户记录
			sql = "delete from user where username = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.execute();
			return 0; // 删除成功
		}
	}

	/**
	 * 检查用户是否有借书证
	 * @param con 数据库连接
	 * @param username 用户名
	 * @return true表示有借书证，false表示没有
	 * @throws Exception 数据库操作异常
	 */
	public boolean checkUserCard(Connection con, String username) throws Exception {
		String sql = "select * from card where username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();

		return rs.next();
	}

	/**
	 * 检查借书证状态
	 * @param con 数据库连接
	 * @param card 借书证对象
	 * @return 状态码（0表示无此证，1表示有证且借书量未满，2表示借书量满，3表示没有借书）
	 * @throws Exception 数据库操作异常
	 */
	public int checkCard(Connection con, Card card) throws Exception {
		String sql = "select * from card where cardNo = ? and username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, card.getCardNo());
		pstmt.setString(2, card.getUsername());
		ResultSet rs = pstmt.executeQuery();

		if(rs.next()) {
			if(rs.getInt("amount") >= 10) {
				return 2; // 借书量满
			} else if(rs.getInt("amount") == 0) {
				return 3; // 没有借书
			}
			return 1; // 有证且借书量未满
		}
		return 0; // 无此证
	}

	/**
	 * 验证用户密码
	 * @param con 数据库连接
	 * @param username 用户名
	 * @param password 密码
	 * @return true表示密码正确，false表示错误
	 * @throws Exception 数据库操作异常
	 */
	public boolean checkPassword(Connection con, String username, String password) throws Exception {
		String sql = "select * from user where username = ?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();

		String correctPassword = "";
		while(rs.next()) {
			correctPassword = rs.getString("password");
		}
		return correctPassword.equals(password);
	}

	/**
	 * 检查借书输入是否为空
	 * @param cardNo 借书证号
	 * @param password 密码
	 * @return 状态码（0表示卡号为空，1表示密码为空，2表示正确）
	 */
	public int checkBlankBorrow(int cardNo, String password) {
		if(cardNo == 0) {
			return 0; // 卡号为空
		} else if(password.isEmpty()) {
			return 1; // 密码为空
		} else {
			return 2; // 正确
		}
	}

	/**
	 * 检查还书输入是否为空
	 * @param cardNo 借书证号
	 * @param no 图书编号
	 * @return 状态码（0表示卡号为空，1表示书号为空，2表示正确）
	 */
	public int checkBlankReturn(int cardNo, String no) {
		if(cardNo == 0) {
			return 0; // 卡号为空
		} else if(no.isEmpty()) {
			return 1; // 书号为空
		} else {
			return 2; // 正确
		}
	}

	/**
	 * 获取指定借书证的借阅记录
	 * @param con 数据库连接
	 * @param cardNo 借书证号
	 * @return 借阅记录结果集
	 * @throws Exception 数据库操作异常
	 */
	public ResultSet getRecords(Connection con, int cardNo) throws Exception {
		String sql = "select * from record where cardNo = " + cardNo;
		PreparedStatement pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		return rs;
	}

	/**
	 * 将借阅记录转换为表格模型
	 * @param con 数据库连接
	 * @param rsRecord 借阅记录结果集
	 * @param dtm 表格模型
	 * @return 填充后的表格模型
	 * @throws Exception 数据库操作异常
	 */
	public DefaultTableModel getBooks(Connection con, ResultSet rsRecord, DefaultTableModel dtm) throws Exception {
		// 遍历借阅记录，查询对应的图书信息并添加到表格模型
		while(rsRecord.next()) {
			String no = rsRecord.getString("no");
			String sql = "select * from book where no = '"+no+"'";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			Vector v = new Vector();
			v.add(rs.getString("no"));
			v.add(rs.getString("name"));
			v.add(rs.getString("author"));
			v.add(rs.getString("type"));
			v.add(rs.getString("publisher"));
			dtm.addRow(v);
		}
		return dtm;
	}
}

