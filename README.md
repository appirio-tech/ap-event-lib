# ap-event-lib
Event Framework Library

Appirio Java Microservices should use this library for asnychronous notification of events via a raw json payload. 

* Publishers publish objects representing events to topics. Objects must carry a unique identifier to allow subscribers to determine duplicate events
* Each subscribing application shoud identify itself by a unique application name. A subscriber may have multiple workers reading messages on the same application, but a message will be delivered at least once per subscriber. <b>Subscribers must be capable of receiving the same event multipe times without side affect.</b>

## Credentials
Provide AWS credentials in ~/.aws/credentials (where ~ is home directory of user running service) 
```
[AWS_IAM_username]
aws_access_key_id = BLAHBLAHBLAH
aws_secret_access_key = blahBlahBlahBlahBlah
```

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
