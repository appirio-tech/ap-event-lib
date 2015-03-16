# ap-event-lib
Event Framework Library

## Usage
```
import com.appirio.event.Topic;
import com.appirio.event.Publisher;
import com.appirio.event.Subscriber;

Publisher pub = new Publisher("TopicName");
Subscriber sub = new Subscriber("TopicName", "ApplicationName");

pub.Publish("hello");

sub.getMessages();
```
