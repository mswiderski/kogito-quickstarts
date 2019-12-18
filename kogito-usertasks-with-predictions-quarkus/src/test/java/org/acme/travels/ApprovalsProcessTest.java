package org.acme.travels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.identity.StaticIdentityProvider;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ApprovalsProcessTest {

    private static final int ITERATIONS = 200;
    
    private int tasksManuallyCompleted = 0;
    private int tasksAutoCompleted = 0;
    
    @Named("approvals")
    @Inject
    Process<? extends Model> approvalsProcess;
    
    @Test
    public void testSeveralApprovalTasks() {
        
        final NormalDistribution lenovoPrice = new NormalDistribution(1500.0, 40.0);
        final NormalDistribution applePrice = new NormalDistribution(2500.0, 40.0);
        for (int i = 0 ; i < ITERATIONS ; i++) {
            testApprovalProcess("John", lenovoPrice.sample(), "Lenovo", true);
            testApprovalProcess("Mary", applePrice.sample(), "Apple", true);
            testApprovalProcess("Mary", lenovoPrice.sample(), "Lenovo", true);
            testApprovalProcess("John", applePrice.sample(), "Apple", true);
        }
        for (int i = 0 ; i < ITERATIONS ; i++) {
            testApprovalProcess("John", applePrice.sample(), "Lenovo", false);
            testApprovalProcess("Mary", applePrice.sample(), "Lenovo", false);
        }
        
        System.out.println("Tasks manually completed " + tasksManuallyCompleted + " and tasks automatically completed based on predictions " + tasksAutoCompleted);
    }
    
    
    private void testApprovalProcess(String actor, double price, String item, boolean approved) {
                
        assertNotNull(approvalsProcess);
        
        Model m = approvalsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("actor", actor);
        parameters.put("item", item);
        parameters.put("price", price);
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = approvalsProcess.createInstance(m);
        processInstance.start();
        
        
        if (org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE == processInstance.status()) { 
        
            StaticIdentityProvider identity = new StaticIdentityProvider(actor, Collections.singletonList("managers"));
            SecurityPolicy policy = SecurityPolicy.of(identity);
            
            processInstance.workItems(policy);
            
            List<WorkItem> workItems = processInstance.workItems(policy);
            assertEquals(1, workItems.size());
            Map<String, Object> results = new HashMap<>();
            results.put("approved", approved);
            processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);
            // just for the sake of the test, record how many tasks have been completed manually
            tasksManuallyCompleted++;

        } else {
            // and how many where completed automatically based on prediction service
            tasksAutoCompleted++;
        }
        
        Model result = (Model)processInstance.variables();
        assertEquals(4, result.toMap().size());
        assertEquals(result.toMap().get("actor"), actor);
        assertEquals(result.toMap().get("item"), item);
        assertEquals(result.toMap().get("price"), price);
        
    }
    
}
