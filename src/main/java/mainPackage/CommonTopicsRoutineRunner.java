package mainPackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import poms.BasicResponse;

import java.text.SimpleDateFormat;
import java.util.*;

import static mainPackage.DbUtils.*;
import static mainPackage.TestNewsApi.callToNews;
import static mainPackage.TestNewsApi.newsDefUrl;

public class CommonTopicsRoutineRunner extends TimerTask {

    //    public CommonTopicsRoutineRunner(int i2){
//        this.i2 = i2;
//    }
    @Override
    public void run() {
        //need to update most "popular topics"
        List<String> commonTopics = getCommonTopics();

        assert commonTopics != null;
        for (String str : commonTopics) {
            processOneCommonTopic(str);
        }


    }

    public void processOneCommonTopic(String topic) {

        boolean newsItemWasInvalid = false;

        List<String> parameters = new ArrayList<>();
//        parameters.add(categories.get(2));

        String res = null;
        try {
            res = callToNews(newsDefUrl, topic, parameters);
            ObjectMapper om = new ObjectMapper();

            assert res != null;
            BasicResponse car = om.readValue(res, BasicResponse.class);
            //get latest DB item for this topic. extract date
            Map<String, String> itemFromDb = getItemByTopic(topic);
            // check if news is missing, get new one.
            if(itemFromDb!=null && itemFromDb.isEmpty()){
                newsItemWasInvalid = true;
                //call to get new news for this topic
                BasicResponse newNews = om.readValue( callToNews(newsDefUrl, topic, null), BasicResponse.class);
                //put this new into db, DONT DO following steps
                boolean status = putItemInDb(newNews, topic);
                assert status;

            } else {
                System.out.println();
                //compare dates, difference should be < 1 day
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //add current date in needed format
                Date currentDate = new Date(System.currentTimeMillis());
                //date from db item:
                Date dateFromDbItem = sdformat.parse(  itemFromDb.get("date"));

                long diff = currentDate.getTime() - dateFromDbItem.getTime();
                long diffHours = diff / (60 * 60 * 1000) % 24;
                if (diffHours > 12){
                    //dates arent equal, need to update news on this topic
                    BasicResponse updatedNews = om.readValue( callToNews(newsDefUrl, topic, null), BasicResponse.class);
                    boolean newsUpdated = updateNewsByTopic(itemFromDb.get("id"), updatedNews, topic);
                    assert newsUpdated;
                }
                    //
//                    System.out.println(car.articles.get(0).content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO: after we get info, we need to replace it in DB if necessary


    }
}
