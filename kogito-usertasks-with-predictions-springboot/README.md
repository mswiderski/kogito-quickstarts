# Kogito user task orchestration with prediction service

## Description

A quickstart project shows very typical user task orchestration. It has single user task that is expected to 
provide approval for requested item (a laptop) that is requested by given user and the item is with given price tag.

Each manual approval, meaning completing user task is input for training the prediction model that will be later on used
to predict possible outcomes for the approval task and at some point with directly complete the task
without any human interaction.

This example shows

* working with user tasks
* using prediction service to automatically complete tasks
	
	
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>


In addition this quickstart also shows how to use prediction aware life cycle for user tasks that can either.

- Recommend possible output in case the confidence threshold is not yet met
- Complete user task directly when confidence threshold is met

This extra functionality is configured in following class

- `org.acme.travels.config.CustomWorkItemHandlerConfig` - responsible for registering work item handler to deal with user tasks

there is also configuration of the prediction service that is based on SMILE framework.



## Build and run

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

### Compile and Run in Local Dev Mode

```
mvn clean package spring-boot:run    
```


### Compile and Run using uberjar

```
mvn clean package 
```
  
To run the generated native executable, generated in `target/`, execute

```
java -jar target/kogito-usertasks-sprintboot-{version}.jar
```

### Use the application

Examine OpenAPI via swagger UI at [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)


### Submit a request to start new approval

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/approvals`  with following content 

```
{
"actor" : "john",
"item" : "Apple",
"price" : 2000
}

```

Complete curl command can be found below:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"actor" : "john", "item" : "Apple", "price" : 2000}' http://localhost:8080/approvals
```

### Show active approvals

```
curl -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/approvals
```

### Show tasks 

```
curl -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=john&group=managers'
```

where `{uuid}` is the id of the given approval instance


### Complete approval task

```
curl -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/decision/{tuuid}?user=john&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task instance


This completes the approval and returns approvals model where both approvals of first and second line can be found, 
plus the approver who made the first one.

```
{
	"approved":true,
	"actor":john,
	"id":"2eeafa82-d631-4554-8d8e-46614cbe3bdf",
	"item":"Apple",
	"price" : 2000
}
```