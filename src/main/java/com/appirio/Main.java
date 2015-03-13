import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Created by thabo on 3/12/15.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //EventSubscriber es = new EventSubscriber("EventTopic1", "TestClient");
//@JsonSerialize


        EventPublisher foo = new EventPublisher("EventTopic1");

        TestModel testObj = new TestModel("Hello World");

        foo.Publish(testObj);
    }

}

