import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
//import java.util.UUID;
//import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.model.*;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

/**
 * Created by thabo on 3/12/15.
 */
public class EventSubscriber {
    final Integer LONG_POLL_WAIT_TIME = 20;

    //private AmazonSQSAsyncClient sqsClient = new AmazonSQSAsyncClient(new ProfileCredentialsProvider());
    private AmazonSQSClient sqs = new AmazonSQSClient(new ProfileCredentialsProvider());
    private AmazonSNSClient sns = new AmazonSNSClient(new ProfileCredentialsProvider());
    private String topicArn;
    private String queueUrl;

    public EventSubscriber(String subscription, String subscriber) {
        sqs.setRegion(Region.getRegion(Regions.US_EAST_1));

//        AsyncHandler asyncHandler = new AsyncHandler() {
//            @Override
//            public void handleResponse(Response res) {
//                System.out.println(res.get().toString())
//            }
//        };

        //create a new SNS topic
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(subscription);
        CreateTopicResult createTopicResult = sns.createTopic(createTopicRequest);
        topicArn = createTopicResult.getTopicArn();

        CreateQueueRequest createQueueRequest = new CreateQueueRequest(subscription + "-" + subscriber);
        queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

        sns.subscribe(topicArn, "sqs", topicArn.replaceAll("sns", "sqs") + "-" + subscriber);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        sqsClient.createQueueAsync((CreateQueueRequest req, CreateQueueResult res) => {
//
//        });

        //create a new SNS topic
//        CreateTopicRequest createTopicRequest = new CreateTopicRequest(name);
//        CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
//        topicArn =  createTopicResult.getTopicArn();


    }

    private void subscribe() {
        ReceiveMessageRequest rmr = new ReceiveMessageRequest();
        rmr.setQueueUrl(queueUrl);
        rmr.setWaitTimeSeconds(LONG_POLL_WAIT_TIME);
        final ReceiveMessageResult result = sqs.receiveMessage(rmr);

        for (Message message : result.getMessages())
            System.out.println(message.getBody());
    }
}
