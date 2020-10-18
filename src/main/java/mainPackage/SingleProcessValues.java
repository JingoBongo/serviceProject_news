package mainPackage;

public class SingleProcessValues {
    public static int idCounter = 0;
    public String id;
    public String topic;
    public String language;
    public String date;
    public String content;
    public String contentUrl;
    public String status;

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        SingleProcessValues.idCounter = idCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SingleProcessValues(String topic){
        this.topic = topic;
        this.id = MainRunner.servicePort+String.valueOf(idCounter++);
    }

    public static String getNextId(){
        return MainRunner.servicePort+String.valueOf(idCounter + 1);
    }
}
