package project.dao;

import project.model.Book;
import project.utils.Str;

import java.sql.*;

/**
 * 图书数据访问对象
 * 负责处理与图书信息相关的数据库操作
 */
public class BookDao {

	/**
	 * 添加或更新图书信息
	 * 如果图书不存在则添加新记录，存在则更新现有记录
	 * @param con 数据库连接
	 * @param book 图书对象
	 * @return 执行结果（0表示失败，1表示成功）
	 * @throws Exception 数据库操作异常
	 */
	public int add(Connection con, Book book) throws Exception {
		// 检查图书是否已存在
		String preSql = "select * from book where no = ?";
		PreparedStatement prePstmt = con.prepareStatement(preSql);
		prePstmt.setString(1, book.getNo());
		ResultSet rs = prePstmt.executeQuery();

		if (!rs.isBeforeFirst()) {
			// 图书不存在，插入新记录
			String sql = "insert into book(no,name,author,type,publisher,storage) value(?,?,?,?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, book.getNo());
			pstmt.setString(2, book.getName());
			pstmt.setString(3, book.getAuthor());
			pstmt.setString(4, book.getType());
			pstmt.setString(5, book.getPublisher());
			pstmt.setLong(6, book.getStorage());
			return pstmt.executeUpdate();
		} else {
			// 图书已存在，更新记录
			String sql = "update book set name=?,author=?,type=?,publisher=?,storage=? where no = ?";
			int num = 0;
			String name = "";
			String author = "";
			String type = "";
			String publisher = "";

			// 获取当前图书信息
			while (rs.next()) {
				num = rs.getInt("storage");
				author = rs.getString("author");
				type = rs.getString("type");
				publisher = rs.getString("publisher");
				name = rs.getString("name");
			}

			// 检查库存更新是否合法
			if (book.getStorage() + num < 0) {
				return 0; // 库存不能为负
			}

			PreparedStatement pstmt = con.prepareStatement(sql);
			// 保留原值或使用新值
			pstmt.setString(1, book.getName().isEmpty() ? name : book.getName());
			pstmt.setString(2, book.getAuthor() == "佚名" ? author : book.getAuthor());
			pstmt.setString(3, book.getType() == "其他" ? type : book.getType());
			pstmt.setString(4, book.getPublisher() == "其他" ? publisher : book.getPublisher());
			pstmt.setLong(5, book.getStorage() + num); // 更新库存
			pstmt.setString(6, book.getNo());

			return pstmt.executeUpdate();
		}
	}

	/**
	 * 根据条件查询图书列表
	 * @param con 数据库连接
	 * @param book 包含查询条件的图书对象
	 * @return 查询结果集
	 * @throws Exception 数据库操作异常
	 */
	public ResultSet list(Connection con, Book book) throws Exception {
		StringBuffer sb = new StringBuffer("select * from book");

		// 根据条件构建SQL查询语句
		if (!Str.isEmpty(book.getNo())) {
			sb.append(" or no like '%" + book.getNo() + "%'");
		}
		if (!Str.isEmpty(book.getName())) {
			sb.append(" or name like '%" + book.getName() + "%'");
		}
		if (!Str.isEmpty(book.getAuthor())) {
			sb.append(" or author like '%" + book.getAuthor() + "%'");
		}
		if (!Str.isEmpty(book.getType())) {
			sb.append(" or type like '%" + book.getType() + "%'");
		}
		if (!Str.isEmpty(book.getPublisher())) {
			sb.append(" or publisher like '%" + book.getPublisher() + "%'");
		}

		// 修正SQL语法，将第一个"or"替换为"where"
		String sql = sb.toString().replaceFirst("or", "where");
		PreparedStatement pstmt = con.prepareStatement(sql);

		return pstmt.executeQuery();
	}

	/**
	 * 删除图书记录
	 * @param con 数据库连接
	 * @param no 图书编号
	 * @return 操作结果（1表示成功，0表示失败）
	 * @throws Exception 数据库操作异常
	 */
	public int delete(Connection con, String no) throws Exception {
		// 检查图书是否被借阅
		String preSql = "select * from record where no = ?";
		PreparedStatement prePstmt = con.prepareStatement(preSql);
		prePstmt.setString(1, no);
		ResultSet rs = prePstmt.executeQuery();

		if (!rs.next()) {
			// 没有借阅记录，可以删除
			String sql = "delete from book where no = '" + no + "'";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.execute();
			return 1;
		}
		return 0; // 有借阅记录，不能删除
	}

	/**
	 * 更新图书信息
	 * @param con 数据库连接
	 * @param book 包含更新信息的图书对象
	 * @return 执行结果
	 * @throws Exception 数据库操作异常
	 */
	public int update(Connection con, Book book) throws Exception {
		StringBuffer sb = new StringBuffer("update book");

		// 根据提供的信息构建更新语句
		if (!Str.isEmpty(book.getName())) {
			sb.append(" , name = '" + book.getName() + "'");
		}
		if (!Str.isEmpty(book.getAuthor())) {
			sb.append(" , author = '" + book.getAuthor() + "'");
		}
		if (!Str.isEmpty(book.getType())) {
			sb.append(" , type = '" + book.getType() + "'");
		}
		if (!Str.isEmpty(book.getPublisher())) {
			sb.append(" , publisher = '" + book.getPublisher() + "'");
		}
		if (book.getStorage() >= 0) {
			sb.append(" , storage = " + book.getStorage());
		}

		sb.append(" where no = '" + book.getNo() + "'");
		// 修正SQL语法，将第一个","替换为"set"
		String sql = sb.toString().replaceFirst(",", "set");
		PreparedStatement pstmt = con.prepareStatement(sql);
		return pstmt.executeUpdate();
	}
}

