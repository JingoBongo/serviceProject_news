package mainPackage;

import com.ctc.wstx.exc.WstxOutputException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
//import jdk.vm.ci.meta.Local;
//import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import poms.BasicResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static mainPackage.DbUtils.*;
import static mainPackage.TestNewsApi.callToNews;
import static mainPackage.TestNewsApi.newsDefUrl;

public class NewsHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            //  check if this is GET request
            if("GET".equals(httpExchange.getRequestMethod())) {
                handleGetResponse(httpExchange);
                return;
            }

            //  otherwise, request must have payload in its body
            String requestBody;
            //  try to get payload from request body
            if ((requestBody = getRequestPayload(httpExchange)) == null) {
                System.err.println("Couldn't take content of request");
                return;
            }

            //  handle request basing on type of request
            if("POST".equals(httpExchange.getRequestMethod())) {
                handlePostResponse(httpExchange, requestBody);
            } else if("PUT".equals(httpExchange.getRequestMethod())) {
                handlePutResponse(httpExchange, requestBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handlePutResponse(HttpExchange httpExchange, String requestPayload) throws IOException {
        //here I need to get what exact parameter is added, add needed param to item in map, change status to euevupdated : UPD: change status to building
        try {
            //  start deserialization of json payload
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode car = objectMapper.readValue(requestPayload, ObjectNode.class);

            //  check if such process exists in storage
            if (MainRunner.getRequestsMap().containsKey(car.get("id").asText())) {
                //  get existing process from storage
                String id = car.get("id").asText();
                //  perform actions depending on payload fields
                if (car.has("date")) {
                    MainRunner.getRequestsMap().get(id).setDate(car.get("date").asText());
                    MainRunner.getRequestsMap().get(id).setStatus("building");
                } else if (car.has("language")) {
                    MainRunner.getRequestsMap().get(id).setLanguage(car.get("language").asText());
                    MainRunner.getRequestsMap().get(id).setStatus("building");
                } else if (car.has("finalize")) {
                    if (car.get("finalize").asBoolean() == true)
                        MainRunner.getRequestsMap().get(id).setStatus("processing");
                }

                //  make json-formatted string
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("status", MainRunner.getRequestsMap().get(id).getStatus());
                String payload = jsonObject.toString();

                sendResponse(httpExchange, payload);
            } else {
                System.err.println("there is no such process");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handlePostResponse(HttpExchange httpExchange, String requestPayload)  throws  IOException {
        //post is very first request. new item in list is created,
        try {
            //  start deserialization of json payload
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode car = objectMapper.readValue(requestPayload, ObjectNode.class);

            //  start process with taken from JSON command name
            String topic = car.get("topic").toString().replaceAll("\"", "");

            SingleProcessValues newProcess = new  SingleProcessValues(topic);

            MainRunner.getRequestsMap().put(newProcess.id, newProcess);
            MainRunner.getRequestsMap().get(newProcess.id).setStatus("created");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", newProcess.id);
            jsonObject.put("status", "created");
            String payload = jsonObject.toString();

            sendResponse(httpExchange, payload);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleGetResponse(HttpExchange httpExchange) throws IOException {
        //after finalize, this will return
        try {
            //  get from uri required process ID
            long requestedIndex = Long.parseLong(httpExchange.getRequestURI().toString().
                    split("\\?")[1].
                    split("=")[1]);

            //  check if there is such id
            if (MainRunner.getRequestsMap().containsKey(String.valueOf(requestedIndex))) {
                //  get requested id
                String id = String.valueOf(requestedIndex);

                //  if process is not ready for transmission then inform client about it
                if (!MainRunner.getRequestsMap().get(id).getStatus().equals("processing")) {
                    System.err.println("process has not received all required arguments");

                    //  inform client that process is unfinished
                    String response = "{\"response\":undone}";
                    sendResponse(httpExchange, response);
                    return;
                }

                //  set status of process to "done"
                MainRunner.getRequestsMap().get(id).setStatus("done");
                //ACTUALLY make request
                String topic = MainRunner.getRequestsMap().get(id).topic;
                List<String> params = new ArrayList<>();
                boolean dateParamPresent = false;
                int daysDifference = 0;
                if(MainRunner.getRequestsMap().get(id).getDate() != null && MainRunner.getRequestsMap().get(id).getDate().length() > 0){
                    dateParamPresent = true;
                    LocalDate today = LocalDate.now();
                    if(MainRunner.getRequestsMap().get(id).getDate().equals("today")){
                        daysDifference = 0;
                        params.add("&from="+today.toString()+"&to="+today.toString());
                    } else if(MainRunner.getRequestsMap().get(id).getDate().equals("last week")){
                        daysDifference = 7;
                        LocalDate lastWeek = today.minus(Period.ofDays(7));
                        params.add("&from="+today.toString()+"&to="+lastWeek.toString());
                    } else if(MainRunner.getRequestsMap().get(id).getDate().equals("last month")){
                        daysDifference = 30;
                        LocalDate lastMonth = today.minus(Period.ofMonths(1));
                        params.add("&from="+today.toString()+"&to="+lastMonth.toString());
                    }
                }

                if(MainRunner.getRequestsMap().get(id).getLanguage() != null && MainRunner.getRequestsMap().get(id).getLanguage().length() > 0){
                    String tempLan = "";
                    switch (MainRunner.getRequestsMap().get(id).getLanguage()){
                        case "en":
                            tempLan = "en";
                            break;
                        case "ro":
                            tempLan = "ro";
                            break;
                        case "ru":
                            tempLan = "ru";
                            break;
                        default:
                            System.err.println("What is this language? "+MainRunner.getRequestsMap().get(id).getLanguage());
                    }
                        params.add("&language="+tempLan);
                }
                // so, params are added, now search for this new in bd by topics, compare dates
                Map<String, String> itemFromDb = getItemByTopic(topic);

                // check if news is missing, get new one.
                ObjectMapper om = new ObjectMapper();
                BasicResponse responseToReturn = new BasicResponse();
                boolean itemFromDbHasValidDate = false;
                //if there is no item is DB
                if(itemFromDb!=null && itemFromDb.isEmpty()){
                    //call to get new news for this topic
                    responseToReturn = om.readValue( callToNews(newsDefUrl, topic, params), BasicResponse.class);
                    //put this new into db, DONT DO following steps
                    boolean status = putItemInDb(responseToReturn, topic);
                    assert status;
                } else {
                    // if there IS ITEM IN DB
                    //first, compare DATE to one in REQUEST
                    //compare dates, difference should be < 1 day
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //add current date in needed format
                    Date currentDate = new Date(System.currentTimeMillis());
                    //date from db item:
                    Date dateFromDbItem = sdformat.parse(  itemFromDb.get("date"));
                    long diff = currentDate.getTime() - dateFromDbItem.getTime();
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    if(dateParamPresent == true){
                        if (diffHours > daysDifference){
                            //dates arent equal, need to update news on this topic
                            responseToReturn = om.readValue( callToNews(newsDefUrl, topic, null), BasicResponse.class);
                            boolean newsUpdated = updateNewsByTopic(itemFromDb.get("id"), responseToReturn, topic);
                            assert newsUpdated;
                        }else {
                            itemFromDbHasValidDate = true;
                        }
                    } else {
                        if (diffHours > 24){
                            //dates arent equal, need to update news on this topic
                            responseToReturn = om.readValue( callToNews(newsDefUrl, topic, null), BasicResponse.class);
                            boolean newsUpdated = updateNewsByTopic(itemFromDb.get("id"), responseToReturn, topic);
                            assert newsUpdated;
                        } else {
                            itemFromDbHasValidDate = true;
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("status", "done");
                if(itemFromDbHasValidDate){
                    jsonObject.put("newsDescription", itemFromDb.get("newsDescription"));
                    jsonObject.put("newsUrl", itemFromDb.get("newsUrl"));
                } else {
                    jsonObject.put("newsDescription", responseToReturn.articles.get(0).description);
                    jsonObject.put("newsUrl", responseToReturn.articles.get(0).url);
                }
                String payload = jsonObject.toString();
                System.out.println();
                sendResponse(httpExchange, payload);
            } else {
                System.err.println("there is no such process");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private String getRequestPayload(HttpExchange httpExchange) throws IOException {
        //  check that there is specified content type of request
        if(httpExchange.getRequestHeaders().containsKey("Content-Type")) {
            //  check content to be equal to json formatted data
            if(httpExchange.getRequestHeaders().get("Content-Type").get(0).equals("application/json")){
                //  open stream for getting UTF-8 formatted characters
                InputStreamReader inputStreamReader = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8);

                //  insert stream to buffer for reading through input stream
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                //  initialize current character and buffer for appending all incoming characters
                int currentCharacter;
                StringBuilder buffer = new StringBuilder(512);

                //  while it's not the end of all stream, read char-by-char all incoming data
                while((currentCharacter = bufferedReader.read()) != -1) {
                    buffer.append((char) currentCharacter);
                }

                //  close buffer and input stream
                bufferedReader.close();
                inputStreamReader.close();

                //  return string-formatted data
                return buffer.toString();
            } else {
                System.err.println("Unknown content-type");
            }
        } else {
            System.err.println("No content-type specified");
        }
        return null;
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        //  set headers of response
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, response.length());
        System.out.println(response);

        //  send response to the client
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes(), 0, response.length());
        outputStream.flush();
        outputStream.close();
    }

}
