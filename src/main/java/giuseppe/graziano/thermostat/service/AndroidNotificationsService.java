package giuseppe.graziano.thermostat.service;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import giuseppe.graziano.thermostat.model.data.Thermostat;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;


@Service
public class AndroidNotificationsService {

    private static final Logger logger = LoggerFactory.getLogger(AndroidNotificationsService.class);

    @Value("${firebase.server.key}")
    private String FIREBASE_SERVER_KEY;

    @Value("${firebase.server.url}")
    private String FIREBASE_API_URL;



    @PostConstruct
    public void init(){
        FileInputStream serviceAccount = null;
        try {


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream("grazianosmarthome-firebase-adminsdk-he7qz-47d234e242.json")))
                .build();

        FirebaseApp firebaseApp =  FirebaseApp.initializeApp(options);
        FirebaseMessaging.getInstance(firebaseApp);

        // See documentation on defining a message payload.
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void senddVWithSDK(String messageText, Thermostat thermostat){

        String title = thermostat.getName() + ": " + messageText;

        Message message = Message.builder()
                .setNotification(new Notification(title, ""))
                .setTopic(String.valueOf(thermostat.getId()))
                .build();

// Send a message to the devices subscribed to the provided topic.
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            logger.error(e.toString());
            return;
        }
// Response is a message ID string.
        System.out.println("Successfully sent message: " + response);

    }

}