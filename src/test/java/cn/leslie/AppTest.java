package cn.leslie;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()

    {
        assertTrue( true );
        //连接的四要素
        String driverName="com.mysql.jdbc.Driver";
        String url="jdbc.mysql://localhost:3306/news";
        String username="root";
        String password="0000";
        /**
         * JDBC的API
         */
        Connection connection=null;
        PreparedStatement ps=null;
        ResultSet  rs=null;
        /**
         * 创建反射加载创建实例对象
         */
        //因为不知道自己拿到的是什么类型 所以一Object父类来代替
        Object object=null;

        //加载类
        try {
            //找到News创建实例做准备
            object= Class.forName("cn.leslie.News").newInstance();
            //加载驱动
            Class.forName(driverName);
            //创建连接
            try {
                connection= DriverManager.getConnection(url,username,password);
                String sql="select id categoryId,title,summary from News";
                //返回指令
                ps=connection.prepareStatement(sql);
                //给结构赋值
                rs=ps.executeQuery();
                //遍历拿到的结果集
                while (rs.next()){
                    //先获取描述数据的元数据
                    ResultSetMetaData data=rs.getMetaData();
                    int count=data.getColumnCount();//获取列数
                    //在遍历的时候因为数据库的时候下标是从1开始的
                    for (int i = 1; i <=count ; i++) {
                        //获取方法名 而不是方法
                            String columnName= data.getColumnName(i);
                            //获取到方法名之后需要得到：比如说 setXxx()这种类型的才是属性名 所以还要获取这个setXxx()方法才能赋值
                        String methodName=changName(columnName);
                        //得到方法之后给赋值 要对应实体类以及数据库的类型
                        String columeType=data.getColumnTypeName(i);
                        if(columeType.equalsIgnoreCase("int")){
                            try {
                                object.getClass().getMethod(methodName,int.class).invoke(object,rs.getInt(columnName));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }else if(columeType.equalsIgnoreCase("String")){
                            try {
                                object.getClass().getMethod(methodName,String.class).invoke(object,rs.getString(columnName));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
     //转换格式类型
    private String changName(String columnName) {
        return "set"+columnName.substring(0,1).toUpperCase()+columnName.substring(1);
    }


}
