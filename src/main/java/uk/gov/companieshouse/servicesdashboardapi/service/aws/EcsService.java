package uk.gov.companieshouse.servicesdashboardapi.service.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ServicesInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//====================================================================
// this service is no more used as this is done by
// https://github.com/companieshouse/services-dashboard-ecs
//====================================================================
@Service
   public class EcsService {

   @Autowired
   private ServicesInfo servicesInfo;

   @Value("${aws.profile}")
   private String profile;

   @Value("${aws.region}")
   private String region;
   private String env;

   private Map<String, ProjectInfo> projectInfoMap;

   private Pattern imagePattern;
   private EcsClient ecsClient;

   @PostConstruct
   private void init() {
      ApiLogger.info(String.format("PROFILE: %s / REGION:%s", profile, region ));

      // Use default provider chain (DefaultCredentialsProvider) which
      // allows the AWS SDK to automatically look for credentials in the following order:
      //
      // 1. Environment variables (AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY)
      // 2. Java system properties (aws.accessKeyId and aws.secretKey)
      // 3. Profile credentials file (like .aws/credentials)
      // 4. IAM roles for ECS tasks or EC2 instances

      // this code will then run both locally and on AWS
      ecsClient = EcsClient.builder()
      .region(Region.of(region))
      .credentialsProvider(DefaultCredentialsProvider.create())  // Use default provider chain
      .build();
   }

   public EcsService() {
      // capture both name and version
      this.imagePattern = Pattern.compile("([^/:]+):([^:]+)$");
   }

   public void fetchClusterInfo(String awsEnv) {
      ApiLogger.info("=======> STARTING SOURCING ECS: " + awsEnv);
      env = awsEnv;
      projectInfoMap = servicesInfo.getProjectInfoMap();
      if (projectInfoMap != null && ! projectInfoMap.isEmpty()) {
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
         ApiLogger.info("=======> COMPLETED SOURCING ECS: " + env);
      }
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
               addEcsInfo(containerDefinition.image());
            });
         });
   }

   private void addEcsInfo(String image) {
      ApiLogger.info(String.format("       Adding image: %s", image));
      Matcher matcher = imagePattern.matcher(image);

      if (matcher.find()) {
         String name    = matcher.group(1);
         String version = matcher.group(2);
         // Check if the key is present in projectInfoMap
         if (!projectInfoMap.containsKey(name)) {
            ApiLogger.info("ECS '" + name + "' not present in DT");
         } else {
            // Update the ecs map in the ProjectInfo for the given key
            ProjectInfo projectInfo = projectInfoMap.get(name);
            // Check if ecs is null or empty, and initialize if needed
            if (projectInfo.getEcs() == null || projectInfo.getEcs().isEmpty()) {
                  projectInfo.setEcs(new HashMap<>());
            }
            projectInfo.getEcs().computeIfAbsent(env, k -> new HashSet<>()).add(version);
            ApiLogger.info(String.format("       Added name: %s / version:%s", name, version));
         }
      }
   }
}
