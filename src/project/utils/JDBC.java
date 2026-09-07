package project.utils;

import java.sql.*;

/**
 * JDBC工具类
 * 提供数据库连接和关闭功能
 */
public class JDBC {
    // 数据库连接参数
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/yangguotushuguan";
    private String user = "root";
    private String password = "your_password"; // 请修改为你自己的MySQL密码

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     * @throws Exception 连接异常
     */
    public Connection getCon() throws Exception {
        Connection con = null;
        try {
            // 注册JDBC驱动程序
            Class.forName(driver);
            // 建立数据库连接
            con = DriverManager.getConnection(url, user, password);

            // 连接状态验证（此处存在逻辑错误，isClosed()返回false表示连接正常）
            if (!con.isClosed()) {
                System.out.println("杂鱼~数据库销毁成功！跑路呀~");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("真是个杂鱼呢~数据库驱动没有安装啦！");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("笨蛋杂鱼~数据库又双叒连接失败了诶");
        }
        return con;
    }

    /**
     * 关闭数据库连接
     * @param con 数据库连接对象
     * @throws Exception 关闭异常
     */
    public void closeCon(Connection con) throws Exception {
        if (con != null) {
            con.close();
        }
    }

    /**
     * 测试数据库连接
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        JDBC jdbc = new JDBC();
        try {
            jdbc.getCon();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

