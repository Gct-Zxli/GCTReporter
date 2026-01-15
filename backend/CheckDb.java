import java.sql.*;
public class CheckDb {
    public static void main(String[] args) {
        try {
            Class.forName(\"org.sqlite.JDBC\");
            Connection conn = DriverManager.getConnection(\"jdbc:sqlite:gct_reporter.db\");
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println(\"数据库连接成功！\");
            System.out.println(\"数据库产品: \" + meta.getDatabaseProductName());
            System.out.println(\"数据库版本: \" + meta.getDatabaseProductVersion());
            
            ResultSet tables = meta.getTables(null, null, \"%\", new String[]{\"TABLE\"});
            System.out.println(\"\n数据表列表:\");
            while (tables.next()) {
                System.out.println(\"  - \" + tables.getString(\"TABLE_NAME\"));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(\"错误: \" + e.getMessage());
            e.printStackTrace();
        }
    }
}
