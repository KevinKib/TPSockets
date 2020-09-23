package stream;

import java.sql.Date;
import java.util.Calendar;

public class Message {

    private String pseudo;
    private String content;
    private Date date;

    public Message(String pseudo, String content) {
        this.pseudo = pseudo;
        this.content = content;
        this.date = new Date(Calendar.getInstance().getTime().getTime());
    }

}
