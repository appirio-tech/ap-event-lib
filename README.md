# Appirio Event Framework Library

Appirio Microservices should follow this model for asnychronous notification of events via a raw json payload. This library provides a Java implementation for our standard service stack.

* Publishers publish objects representing events to topics. Publishers must provide a unique application name. Objects should carry a unique identifier to allow subscribers to determine duplicate events.
* Each subscribing application must identify itself by a unique application name. A subscriber may have multiple workers reading messages on the same application, but a message will be delivered at least once per subscriber. <b>Subscribers must be capable of receiving the same event multiple times without side affect.</b>

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

Publisher pub = new Publisher("PublisherAppName", "TopicName");
Subscriber sub = new Subscriber("PublisherAppName", "TopicName", "SubscriberAppName");

pub.Publish(new TestModel("hello"));

String json = sub.getMessages()[0];
// or
TestModel testReceived = (TestModel) sub.getItems(TestModel.class)[0];
```
