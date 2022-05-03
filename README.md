# POC: Spring AWS Kinesis Multistream

It demonstrates how to publish and consume records from multiple Kinesis streams using the [Kinesis Client Library (KCL)](https://github.com/awslabs/amazon-kinesis-client).

The goal is to develop a type-safe and null-safe abstraction for [Domain Events](https://www.martinfowler.com/eaaDev/DomainEvent.html) that can be serialized to JSON and sent to a Kinesis Stream. We also want to consume those events published to the streams in a type-safe and null-safe way as well. The communication with the Kinesis cluster should be implemented using the KCL and we must be able to select which types of events we want to consume and which type of events should be ignored based on configuration.

We must be able to enable record consumption without modifying existing code or changing code related to consumption of other types of events and each event consumption can have its own behavior. The Kinesis streams must be retrieved from the environment (variable or property file) and none configuration should be hard-coded. The code must be testes using automated tests only and no manual testing should be required.

This experiment is written in Java, uses the KCL to publish and consume Kinesis stream records and [Spring Context](https://github.com/spring-projects/spring-framework) to configure stream consumption and dependencies resolution. It depends on [Docker](https://github.com/docker) to provision an instance of [Localstack](https://github.com/localstack/localstack) with the AWS infrastructure required to interact with Kinesis, which is the Kinesis cluster, DynamoDB and CloudWatch. It also does not use the experimental API added to the KCL to support multistream consumption.

## Software Design

The application domain is pretty simple: we should be able to create courses and students and allow students to enroll in a course. Every operation should publish a domain event representing what happened to a Kinesis stream with the entity ID as the partition key. For example, when we register the student called `John Smith` and event of type `StudentCreated` containing the student data should be published to the `student_stream` stream. Code related to this domain is implemented in a package with the entity name, such as `com.example.student`, `com.example.course` and `com.example.enrollment`.

Apart from the domain code, we also have an abstraction to define the events. These files belongs to the package `com.example.event` and are mostly interfaces or records (final classes). The package `com.example.streaming` contains the implementation required to deal with events using Kinesis, but we may have other packages or participants that can work with Kafka in the future in case our requirements changed. We defined everything as interfaces so we can extend our software without modifying existing behavior. The goal is to keep the package which contains the abstraction agnostic from any external dependencies such as streaming providers, protocols, libraries and frameworks.

Still on the `com.example.event` package, the `Event` interfaces represents the event itself and should have an unique ID represented as an UUID, `payload` with any data we want depending on the event type and also `metadata` with any data as well as the `payload`. Both `payload` and `metadata` are defined in terms of generic interfaces as `EventPayload` and `EventMetadata`, both are markers only (empty interfaces). Each event also has the `type` property which is used by Jackson (the JSON library) to find the concrete implementation of the `Event` interface for polymorphic serialization/deserialization. To avoid coupling the abstraction with the library, we've created a Mix In interface for that in the implementation package.

```json
{
  "id": "8a556f2e-072c-4f64-9c21-923b628c6541",
  "type": "com.example.StudentEnrolled",
  "payload": {
    "enrollmentId": "999e002a-69ae-453a-9d5a-11b3c5c22d46"
  },
  "metadata": {
    "courseId": "3851b640-2a53-4f3b-8759-079612ea5a1a",
    "studentId": "56a3da44-5278-405e-bf4b-311b344ea9e9"
  }
}
```

The JSON above is an example of domain event serialized to JSON that may be published to the `enrollment_stream` stream. We have an interface called `EventPublisher` which is implemented by `StreamPublisher`. This participant is responsible to serialize an instance of `Event<Payload, Metadata>` to JSON and publish it to a Kinesis stream. To support Kafka (or other stream provider) we may create another implementation and switch the instance passed to other participants using Spring Context without them knowing that the underlying implementation has changed.

We also have an interface called `EventConsumer` which represents the behavior we want for a given type of event, so we may have one implementation per event type. Some examples are `StudentCreatedConsumer`, `CourseCreatedConsumer` and `StudentEnrolledConsumer`. The last one, for example, only accepts events of type `StudentEnrolled`. Instances of `EventConsumer` interface are created using a factory based on the interface `EventConsumerFactory` which is implemented by `StreamConsumerFactory` using Spring Context. We find the instance based on the class name to enforce consistency between the event type and the consumer implementation.

To reduce boilerplate code we have a common implementation of `Scheduler` class from KCL named `EventStreamScheduler`. It is responsible to handle the I/O logic required to communicate with AWS APIs and created instances of `ShardRecordProcessor` interface using an implementation of `ShardRecordProcessorFactory` interface, both from the KCL also. The current implementation of the factory is the class called `StreamProcessorFactory` which also uses Spring Context to resolve which `ShardRecordProcessor` instance should be used by the `Scheduler`. The instance is found based on the stream name provided by the `Scheduler` and also have a base implementation of `ShardRecordProcessor` called `EventStreamProcessor` to void boilerplate code required to handle the stream shard record lifecycle and to provide observability (logging only). Every stream we may want to handle can inherit from this class and use the `EventConsumerFactory` to create `EventConsumer` instances to handle the records.

Since the all the application configuration is based on Spring Context, supporting the consumption of records from another stream is pretty straightforward. We just need to create a configuration class that reads the stream name from the environment and provides implementations of `Scheduler` and `ShardRecordProcessor`. Mostly copy-and-paste from the existing examples while changing the configuration identifier.

The stream consumption may also be disabled on application startup. The instances of `Scheduler` class (from KCL) must be initialized in a thread (one thread per instance) so it can communicate with AWS APIs and consume events properly. We've used Spring Context lifecycle events to retrieve all the instances registered as beans on context refresh (application startup) and to start a daemon thread for each one. It may be disabled based on the environment (variable or property).

## How to run

| Description | Command |
| :--- | :--- |
| Run tests | `./gradlew test` |

## Preview

Logs:

```
INFO com.example.streaming.configuration.StreamPublisher - Event 2c031192-b012-4d20-83d1-b62cfa223c56 published: Optional[OK]
INFO com.example.course.CourseServiceDefault - Course created: NewCourse[code=CS101, title=Introduction to Computer Science, rating=4]
INFO com.example.streaming.configuration.StreamPublisher - Event 66711f3e-cfd1-424e-8e50-f53dc45ffa5b published: Optional[OK]
INFO com.example.student.StudentServiceDefault - Student created: Student[id=a2d1ae1c-5035-44d8-908a-132f183fef4d, firstName=John, lastName=Smith]
INFO com.example.streaming.configuration.StreamPublisher - Event 70f3d7f7-7382-4c20-8b08-91a46a8aff61 published: Optional[OK]
INFO com.example.enrollment.EnrollmentServiceDefault - Enrollment created: Enrollment[enrollmentId=1673ea25-bf29-4f2a-b952-232cad7c4f1c, studentId=a2d1ae1c-5035-44d8-908a-132f183fef4d, courseId=bbfa637a-40a6-4190-b627-ab547fe98252]
```
