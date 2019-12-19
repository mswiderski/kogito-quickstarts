package org.acme.travels.config;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemHandler;
import org.kie.kogito.prediction.api.PredictionAwareHumanTaskLifeCycle;
import org.kie.kogito.predictions.smile.AttributeType;
import org.kie.kogito.predictions.smile.RandomForestConfiguration;
import org.kie.kogito.predictions.smile.SmileRandomForest;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.springframework.stereotype.Component;


/**
 * Custom work item handler configuration to change default work item handler for user tasks
 * to take into predictions
 *
 */
@Component
public class CustomWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {{
    register("Human Task", new HumanTaskWorkItemHandler(new PredictionAwareHumanTaskLifeCycle(new SmileRandomForest(readConfigurationFromFile()))));
}

    RandomForestConfiguration readConfigurationFromFile() {

        final RandomForestConfiguration configuration = new RandomForestConfiguration();
    
        final Map<String, AttributeType> inputFeatures = new HashMap<>();
        inputFeatures.put("ActorId", AttributeType.NOMINAL);
        inputFeatures.put("price", AttributeType.NUMERIC);
        inputFeatures.put("item", AttributeType.NOMINAL);
        configuration.setInputFeatures(inputFeatures);
    
        configuration.setOutcomeName("approved");
        configuration.setOutcomeType(AttributeType.BOOLEAN);
        configuration.setConfidenceThreshold(0.7);
        configuration.setNumTrees(100);
    
        return configuration;
    }
    }