package stream;

import java.sql.Date SQLDate;
import java.util.Calendar;
import java.util.Date;

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
