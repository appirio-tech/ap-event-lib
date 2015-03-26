# ap-event-lib
Event Framework Library

## Usage
```
import com.appirio.event.Topic;
import com.appirio.event.Publisher;
import com.appirio.event.Subscriber;

public class TestModel {
    private String testString;
    public TestModel() {
    }
    public TestModel(String testString) {
        this.testString = testString;
    }
    public String getTestString() {
        return testString;
    }
}

Publisher pub = new Publisher("TopicName");
Subscriber sub = new Subscriber("TopicName", "ApplicationName");

pub.Publish(new TestModel("hello"));

String json = sub.getMessages()[0];
// or
TestModel testReceived = (TestModel) sub.getItems(TestModel.class)[0];
```
