package com.appirio.event;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;

/**
 * Created by thabo on 3/13/15.
 *
 * A topic that can be published to.
 */
public class Topic {
    private final AmazonSNSClient client = new AmazonSNSClient(new ProfileCredentialsProvider());

    private String arn;

    public Topic(String name) {
        client.setRegion(Configuration.REGION);

        //create a new SNS topic
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(name);
        CreateTopicResult createTopicResult = client.createTopic(createTopicRequest);
        arn = createTopicResult.getTopicArn();
    }

    public String getArn() {
        return arn;
    }

    public void Publish(String msg) {
        PublishRequest publishRequest = new PublishRequest(arn, msg);
        client.publish(publishRequest);
    }
}
