package mainPackage;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static mainPackage.TestNewsApi.recursivePostRegisterInGate;

public class MainRunner {
    public static ConcurrentHashMap<String, SingleProcessValues> requestsMap = new ConcurrentHashMap<String, SingleProcessValues>();

    public static String servicePort;
    public static int poolSize;
    public static String gateIp;
    public static String gatePort;
    public static String dbType;
    public static String dbIp;
    public static String dbPort;
    public static String dbName;
    public static String serviceHost = "localhost";

    public static ConcurrentHashMap<String, SingleProcessValues> getRequestsMap() {
        return requestsMap;
    }

    public static void setRequestsMap(ConcurrentHashMap<String, SingleProcessValues> requestsMap) {
        MainRunner.requestsMap = requestsMap;
    }

    public static void main(String[] args) throws IOException {

        //part with arguments from command line, except they are swapped with default values when emptyrv

        //args:
//        0 : service port
        servicePort = "8080";
//        servicePort = args[0];
//        1 : pool size
        poolSize = 10;
//        poolSize = Integer.valueOf(args[1]);
//        2 : gate ip
//        gateIp = args[2];
        gateIp = "no such";
//        3 : gate port
//        gatePort = args[3];
        gatePort = "no such";
//        4 : db type
//        dbType = args[4];
        dbType = "mysql";
//        5 : db ip
//        dbIp = args[5];
        dbIp = "172.17.0.2";
//        6 : db port
//        dbPort = args[6];
        dbPort = "33060";
//        7 : db name
//        dbName = args[7];
        dbName = "news";



        //Register in service
        //TODO: uncomment
//        recursivePostRegisterInGate();
        //

        //we need a part for storing all requests data

        //we need distinct classes for 1) threadpool manager 2) common requests cycle 3) class for single requests, thread safe list of them. supporting methods?
        //p.s. when choosing answer, check in db if it is up to date

        // 2) common requests
        Timer timer = new Timer();
        timer.schedule(new CommonTopicsRoutineRunner(), 0, 12*60*60*1000);

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", Integer.parseInt(servicePort)), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        server.createContext("/news", new  NewsHttpHandler());
        server.setExecutor(threadPoolExecutor);
        server.start();

    }
}
