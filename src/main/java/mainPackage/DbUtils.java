package mainPackage;

import poms.BasicResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbUtils {



    public static void main(String[] args) {

        try {
            //1. get connection to db
//            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/news", "root", "root");
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:"+MainRunner.dbPort+"/"+MainRunner.dbName, "root", "root");
            //2. create statement
            Statement myStmt = myConn.createStatement();
            //3. execute sql query
            ResultSet myRs = myStmt.executeQuery("select * from commonTopics");
            //4. process results set
            List<String> topics = new ArrayList<>();
            while (myRs.next()) {
                System.out.println(myRs.getString("name") + ", " + myRs.getString("newsBody"));
                topics.add(myRs.getString("string"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getItemByTopic(String topic){
        try{
            //1. get connection to db
//            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/news", "root", "root");
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:"+MainRunner.dbPort+"/"+MainRunner.dbName, "root", "root");
            //2. create statement
            Statement myStmt = myConn.createStatement();
            //3. execute sql query
            ResultSet myRs = myStmt.executeQuery("select * from mainnewstable where newsTopic = '"+topic+"'");
            //4. process results set
            Map<String, String> itemFields = new HashMap<>();
            while (myRs.next()){
//                    System.out.println(myRs.getString("name") + ", "+myRs.getString("newsBody"));
                itemFields.put("newsDescription",myRs.getString("newsDescription"));
                itemFields.put("newsUrl",myRs.getString("newsUrl"));
                itemFields.put("newsTopic",myRs.getString("newsTopic"));
                itemFields.put("date",myRs.getString("date"));
                itemFields.put("id", myRs.getString("id"));
            }
            return itemFields;


        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean putItemInDb(BasicResponse item, String topic){
        try{
            //1. get connection to db
//            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/news", "root", "root");
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:"+MainRunner.dbPort+"/"+MainRunner.dbName, "root", "root");
            //2. create statement
            Statement myStmt = myConn.createStatement();
            //3. execute sql query
            int res =  myStmt.executeUpdate("insert into mainnewstable " +
                                                        "(newsDescription, newsUrl, newsTopic)" +
                                                        "values (\""+item.articles.get(0).description+"\",\""+item.articles.get(0).url+"\",\""+topic+"\")");
            if(res == 1){
                return true;
            } else {
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static boolean updateNewsByTopic(String id, BasicResponse item, String topic){
        try{
            //1. get connection to db
//            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/news", "root", "root");
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:"+MainRunner.dbPort+"/"+MainRunner.dbName, "root", "root");
            //2. create statement
            Statement myStmt = myConn.createStatement();
            //3. execute sql query
            return myStmt.execute("update news.mainnewstable\n" +
                    "set\n" +
                    "\tnewsDescription = '"+item.articles.get(0).description+"',\n" +
                    "    newsUrl = '"+item.articles.get(0).url+"',\n" +
                    "    date = now()\n" +
                    "where \n" +
                    "\tid = '"+id+"'");




        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getCommonTopics(){
            try{
                //1. get connection to db
//                Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/news", "root", "root");
                Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:"+MainRunner.dbPort+"/"+MainRunner.dbName, "root", "root");
                //2. create statement
                Statement myStmt = myConn.createStatement();
                //3. execute sql query
                ResultSet myRs = myStmt.executeQuery("select * from commonTopics");
                //4. process results set
                List<String> topics = new ArrayList<>();
                while (myRs.next()){
//                    System.out.println(myRs.getString("name") + ", "+myRs.getString("newsBody"));
                    topics.add(myRs.getString("string"));
                }
                return topics;


            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }



}

