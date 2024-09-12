package uk.gov.companieshouse.servicesdashboardapi.service.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import jakarta.annotation.PostConstruct;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.model.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
   public class EcsService {

   @Value("${aws.profile}")
   private String profile;

   @Value("${aws.region}")
   private String region;

   @Value("${aws.env}")
   private String env;

   private Pattern imagePattern;

   private EcsClient ecsClient;

   @PostConstruct
   private void init() {
      ApiLogger.info(String.format("PROFILE: %s / REGION:%s",profile, region));
      ecsClient = EcsClient.builder()
     .region(Region.of(region))
     .credentialsProvider(ProfileCredentialsProvider.create(profile))
     .build();
   }

   public EcsService() {
      // capture both name and version
      this.imagePattern = Pattern.compile("([^/:]+):([^:]+)$");
   }

   public void fetchClusterInfo() {
         ListClustersResponse clustersResponse = ecsClient.listClusters();
         List<String> clusterArns = clustersResponse.clusterArns();

         clusterArns.forEach(clusterArn -> {
            ApiLogger.info("Cluster: " + clusterArn);

            ListTasksResponse tasksResponse = ecsClient.listTasks(ListTasksRequest.builder()
                     .cluster(clusterArn)
                     .desiredStatus(DesiredStatus.RUNNING)
                     .build());

            List<String> taskArns = tasksResponse.taskArns();

            if (taskArns.isEmpty()) {
               ApiLogger.info("No running tasks in cluster: " + clusterArn);
            } else {
               taskArns.forEach(taskArn -> {
                     describeTaskAndFetchImages(clusterArn, taskArn);
               });
            }
         });
   }

   private void describeTaskAndFetchImages(String clusterArn, String taskArn) {
         DescribeTasksResponse describeTasksResponse = ecsClient.describeTasks(DescribeTasksRequest.builder()
               .cluster(clusterArn)
               .tasks(taskArn)
               .build());

         describeTasksResponse.tasks().forEach(task -> {
            String taskDefinitionArn = task.taskDefinitionArn();
            DescribeTaskDefinitionResponse taskDefinitionResponse = ecsClient.describeTaskDefinition(
                     DescribeTaskDefinitionRequest.builder()
                           .taskDefinition(taskDefinitionArn)
                           .build()
            );

            List<ContainerDefinition> containerDefinitions = taskDefinitionResponse.taskDefinition().containerDefinitions();
            containerDefinitions.forEach(containerDefinition -> {
               ApiLogger.info("   Service: " + extractServiceFromImage(containerDefinition.image()));
            });
         });
   }

   private String extractServiceFromImage(String image) {

      Matcher matcher = imagePattern.matcher(image);

      String name = "";
      String version = "";

      if (matcher.find()) {
         name = matcher.group(1);
         version = matcher.group(2);
      }
      ApiLogger.info(String.format("       name: %s / version:%s", name, version));
      return name;
   }
}
