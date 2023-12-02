package com.example;

import com.example.course.CourseService;
import com.example.enrollment.EnrollmentService;
import com.example.enrollment.NewEnrollment;
import com.example.streaming.CourseStreamConfiguration;
import com.example.streaming.EnrollmentStreamConfiguration;
import com.example.streaming.StudentStreamConfiguration;
import com.example.streaming.configuration.StreamProperties;
import com.example.streaming.json.JSONConfiguration;
import com.example.student.StudentService;
import com.example.testing.CourseTestBuilder;
import com.example.testing.StudentTestBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.StreamStatus;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringJUnitConfig({
    AWSConfiguration.class,
    KinesisConfiguration.class,
    JSONConfiguration.class,
    CourseStreamConfiguration.class,
    EnrollmentStreamConfiguration.class,
    StudentStreamConfiguration.class,
    MainTestConfiguration.class
})
@Testcontainers
class MainTest {

    @Container
    private static final LocalStackContainer CONTAINER =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.12.15"))
            .withServices(
                LocalStackContainer.Service.CLOUDWATCH,
                LocalStackContainer.Service.DYNAMODB,
                LocalStackContainer.Service.KINESIS
            );

    @DynamicPropertySource
    private static void setApplicationProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.accessKey", CONTAINER::getAccessKey);
        registry.add("aws.secretKey", CONTAINER::getSecretKey);
        registry.add("aws.region", CONTAINER::getRegion);
        registry.add("aws.url.cloudwatch", () -> CONTAINER.getEndpointOverride(LocalStackContainer.Service.CLOUDWATCH));
        registry.add("aws.url.dynamodb", () -> CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB));
        registry.add("aws.url.kinesis", () -> CONTAINER.getEndpointOverride(LocalStackContainer.Service.KINESIS));
    }

    @BeforeAll
    public static void beforeAll(ApplicationContext applicationContext) throws InterruptedException {
        final var kinesisAsyncClient = applicationContext.getBean(KinesisAsyncClient.class);
        final var streamProperties = applicationContext.getBeansOfType(StreamProperties.class);

        streamProperties.forEach((name, properties) -> {
            final var createStreamRequest = CreateStreamRequest.builder()
                .streamName(properties.stream().value())
                .shardCount(1)
                .build();

            kinesisAsyncClient.createStream(createStreamRequest)
                .join();
        });

        streamProperties.forEach((name, properties) -> {
            final var describeStreamRequest = DescribeStreamRequest.builder()
                    .streamName(properties.stream().value())
                    .build();

            Awaitility.await()
                    .timeout(Duration.ofSeconds(10))
                    .until(() -> {
                        final var describeStreamResponse = kinesisAsyncClient.describeStream(describeStreamRequest).join();
                        final var streamDescription = describeStreamResponse.streamDescription();
                        return StreamStatus.ACTIVE.equals(streamDescription.streamStatus());
                    });
        });
    }

    private CourseService courseService;
    private EnrollmentService enrollmentService;
    private StudentService studentService;

    @BeforeEach
    public void beforeEach(ApplicationContext applicationContext) {
        this.courseService = applicationContext.getBean(CourseService.class);
        this.enrollmentService = applicationContext.getBean(EnrollmentService.class);
        this.studentService = applicationContext.getBean(StudentService.class);
    }

    @Test
    void creatingCourseAndStudentAndEnrollment() throws InterruptedException {
        final var newCourse = CourseTestBuilder.newCourse();
        final var newStudent = StudentTestBuilder.newStudent();

        final var course = assertDoesNotThrow(() -> courseService.create(newCourse));
        final var student = assertDoesNotThrow(() -> studentService.create(newStudent));

        final var newEnrollment = new NewEnrollment(student.id(), course.id());
        assertDoesNotThrow(() -> enrollmentService.enroll(newEnrollment));
    }
}
