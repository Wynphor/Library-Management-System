package project.dao;

import project.model.User;

import java.sql.*;

/**
 * 用户数据访问对象
 * 负责处理与用户相关的数据库操作，包括普通用户和管理员用户
 */
public class UserDao {

    /**
     * 普通用户登录验证
     * @param con 数据库连接
     * @param user 包含登录信息的用户对象
     * @return 登录成功返回用户对象，失败返回null
     * @throws Exception 数据库操作异常
     */
    public User login(Connection con, User user) throws Exception {
        User result = null;
        String sql = "select * from user where username=? and password = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPassword());
        ResultSet rs = pstmt.executeQuery();

        // 验证成功，构建并返回用户对象
        if(rs.next()) {
            result = new User();
            result.setId(rs.getInt("id"));
            result.setUsername(rs.getString("username"));
            result.setPassword(rs.getString("password"));
        }

        // 关闭资源
        pstmt.close();
        con.close();
        return result;
    }

    /**
     * 管理员用户登录验证
     * @param con 数据库连接
     * @param user 包含登录信息的用户对象
     * @return 登录成功返回用户对象，失败返回null
     * @throws Exception 数据库操作异常
     */
    public User loginA(Connection con, User user) throws Exception {
        User result = null;
        String sql = "select * from auser where username=? and password=?";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPassword());
        ResultSet rs = pstmt.executeQuery();

        // 验证成功，构建并返回用户对象
        if(rs.next()) {
            result = new User();
            result.setId(rs.getInt("id"));
            result.setUsername(rs.getString("username"));
            result.setPassword(rs.getString("password"));
        }

        // 关闭资源
        pstmt.close();
        con.close();
        return result;
    }

    /**
     * 普通用户注册
     * @param con 数据库连接
     * @param user 包含注册信息的用户对象
     * @return 注册结果（1表示成功，0表示用户名已存在）
     * @throws Exception 数据库操作异常
     */
    public int register(Connection con, User user) throws Exception {
        // 检查用户名是否已存在
        String sql = "select * from user where username=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        ResultSet rs = pstmt.executeQuery();

        if(rs.next()) {
            return 0; // 用户名已存在
        }

        // 插入新用户
        sql = "insert into user(username,password) value(?,?)";
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPassword());
        pstmt.execute();
        return 1;
    }

    /**
     * 添加管理员用户
     * @param con 数据库连接
     * @param user 包含管理员信息的用户对象
     * @return 添加结果（1表示成功，0表示用户名已存在）
     * @throws Exception 数据库操作异常
     */
    public int add(Connection con, User user) throws Exception {
        // 检查用户名是否已存在
        String sql = "select * from auser where username=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        ResultSet rs = pstmt.executeQuery();

        if(rs.next()) {
            return 0; // 用户名已存在
        }

        // 插入新管理员
        sql = "insert into auser(username,password) value(?,?)";
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPassword());
        pstmt.execute();
        return 1;
    }

    /**
     * 删除管理员用户
     * @param con 数据库连接
     * @param user 包含要删除的管理员信息的用户对象
     * @return 删除结果（1表示成功，0表示用户名不存在）
     * @throws Exception 数据库操作异常
     */
    public int delete(Connection con, User user) throws Exception {
        // 检查用户名是否存在
        String sql = "select * from auser where username=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        ResultSet rs = pstmt.executeQuery();

        if(!rs.next()) {
            return 0; // 用户名不存在
        } else {
            // 删除管理员
            sql = "delete from auser where username=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.execute();
            return 1;
        }
    }
}

