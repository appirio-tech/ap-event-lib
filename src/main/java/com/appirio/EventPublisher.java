import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
//import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
//import com.amazonaws.services.sns.model.DeleteTopicRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.cedarsoftware.util.io.JsonWriter;

import java.io.IOException;

public class EventPublisher {
    private AmazonSNSClient snsClient = new AmazonSNSClient(new ProfileCredentialsProvider());
    private ObjectMapper mapper = new ObjectMapper();

    private String topicArn;

    public EventPublisher(String name) {
        //create a new SNS client and set endpoint
        snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

        //create a new SNS topic
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(name);
        CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
        topicArn = createTopicResult.getTopicArn();
    }

    public void Publish(Object o) throws IOException {

        //String json = JsonWriter.objectToJson(o);
        String json = mapper.writeValueAsString(o);
        Publish(json);
    }

    private void Publish(String msg) {
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        PublishResult publishResult = snsClient.publish(publishRequest);
    }
}