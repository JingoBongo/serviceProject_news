package mainPackage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class TestNewsApi {
    static String myPersonalApiKey = "4c950226914b4be989bc860c8763d56f";
    static String newsDefUrl = "http://newsapi.org/v2/everything";

    public static void main(String[] args) throws IOException {
//        callPostToServer();
//        callPutToServer();
//        callGetToServer();
//        callPostToCashServer();
        callPutToCashServer();
        callGetToCashServer();

    }


    public static void recursivePostRegisterInGate() throws UnsupportedEncodingException {
        try {
            String serviceUrl = "http://" + MainRunner.serviceHost + ":" + MainRunner.servicePort + "/news";
            String gateUrl = "http://" + MainRunner.gateIp + ":" + MainRunner.gatePort + "/";


            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(gateUrl);

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("functionName", "news");
            jsonRequest.put("address", serviceUrl);

            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("Service-Call", "true");
            StringEntity entity = new StringEntity(jsonRequest.toString(), ContentType.APPLICATION_JSON);
            httppost.setEntity(entity);

//Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != (HttpStatus.SC_OK)) {
                recursivePostRegisterInGate();
            } else {
                System.out.println("Registered at gateway.");
            }
            System.out.println();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String callToNews(String url, String topic, List<String> parameters) throws IOException {
        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpClient client = HttpClientBuilder.create().build();
        String allTextFromPage = "";
        try {
            StringBuilder finalUrl = new StringBuilder();
            finalUrl.append(url + "?q=" + topic);
            if (parameters != null && parameters.size() > 0) {
                parameters.forEach(s -> finalUrl.append("&").append(s));
            } else {
                finalUrl.append("&language=en");
            }
            finalUrl.append("&sortBy=popularity");
//            finalUrl.append("&sortBy=popularity&language=en");

            HttpGet request = new HttpGet(finalUrl.toString());
            request.addHeader("X-Api-Key", myPersonalApiKey);
            HttpResponse response = client.execute(request);
            allTextFromPage = handler.handleResponse(response);
        } finally {
            ((CloseableHttpClient) client).close();
        }
        return allTextFromPage;
    }

    public static void callPostToServer() throws IOException {
        String urrl = "http://localhost:8080/createEmployee";
//        String urrl = "http://localhost:8003/";

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(urrl);

        httppost.setHeader("Content-Type", "application/json");
//        StringEntity param = new StringEntity("{\"functionName\":\"news\",\"topic\":\"lada\"}");
        StringEntity param = new StringEntity("{\"name\":\"badaboi\"}");


        httppost.setEntity(param);

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textRepo = handler.handleResponse(response);
//        HttpEntity entity = response.getEntity();
        System.out.println(textRepo);

    }

    public static void callPostToCashServer() throws IOException {
        String urrl = "http://localhost:8081/createMoneyRecords";
//        String urrl = "http://localhost:8003/";

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(urrl);

        httppost.setHeader("Content-Type", "application/json");
//        StringEntity param = new StringEntity("{\"functionName\":\"news\",\"topic\":\"lada\"}");
        StringEntity param = new StringEntity("{\"name\":\"badaboi\"}");


        httppost.setEntity(param);

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textRepo = handler.handleResponse(response);
//        HttpEntity entity = response.getEntity();
        System.out.println(textRepo);

    }

    public static void callPutToServer() throws IOException {
        String urrl = "http://localhost:8080/createEmployee";
//        String urrl = "http://localhost:8003/";
        HttpClient httpclient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(urrl);

        httpput.setHeader("Content-Type", "application/json");
//        StringEntity param = new StringEntity("{\"topic\":\"lada\"}");
        StringEntity param = new StringEntity("{\"id\":\"80810\",\"role\":\"manager\",\"location\":\"roof\"}");


        httpput.setEntity(param);

//Execute and get the response.
//        HttpResponse response = httpclient.execute(httpput);
        HttpResponse response = httpclient.execute(httpput);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textRepo = handler.handleResponse(response);
//        HttpEntity entity = response.getEntity();
        System.out.println(textRepo);

    }

    public static void callPutToCashServer() throws IOException {
        String urrl = "http://localhost:8081/createMoneyRecords";
//        String urrl = "http://localhost:8003/";
        HttpClient httpclient = HttpClients.createDefault();
        HttpPut httpput = new HttpPut(urrl);

        httpput.setHeader("Content-Type", "application/json");
//        StringEntity param = new StringEntity("{\"topic\":\"lada\"}");
        StringEntity param = new StringEntity("{\"id\":\"80810\",\"salary\":\"9000\",\"daysOff\":\"27\"}");


        httpput.setEntity(param);

//Execute and get the response.
//        HttpResponse response = httpclient.execute(httpput);
        HttpResponse response = httpclient.execute(httpput);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String textRepo = handler.handleResponse(response);
//        HttpEntity entity = response.getEntity();
        System.out.println(textRepo);

    }

    public static void callGetToServer() throws IOException {
        String urrl = "http://localhost:8080/createEmployee";
//        String urrl = "http://localhost:8003/";
//        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(urrl + "?id=80800");
//        request.addHeader("X-Api-Key", myPersonalApiKey);
        HttpResponse response = client.execute(request);
//        String allTextFromPage = handler.handleResponse(response);

        ResponseHandler<String> handler = new BasicResponseHandler();
        String textRepo = handler.handleResponse(response);
//        HttpEntity entity = response.getEntity();
        System.out.println(textRepo);

        ((CloseableHttpClient) client).close();
//        return allTextFromPage;
    }
    public static void callGetToCashServer() throws IOException {
        String urrl = "http://localhost:8081/createMoneyRecords";
//        String urrl = "http://localhost:8003/";
//        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(urrl + "?id=80810");
//        request.addHeader("X-Api-Key", myPersonalApiKey);
        HttpResponse response = client.execute(request);
//        String allTextFromPage = handler.handleResponse(response);

        ResponseHandler<String> handler = new BasicResponseHandler();
        String textRepo = handler.handleResponse(response);
//        HttpEntity entity = response.getEntity();
        System.out.println(textRepo);

        ((CloseableHttpClient) client).close();
//        return allTextFromPage;
    }


}
