# services-dashboard-api

## What it does
This API is designed to retrieve various information about the services that are monitored on our Dependency Track server and integrate them with information about the same services retrieved from SonarQube and GitHub.
It saves this information in a Mongo collection (specified by the respective env vars).

### Example of a project's info in Mongo:
![mongo projects info](https://github.com/companieshouse/services-dashboard-api/blob/7779a9d/images/mongo.projects.png?raw=true)

It also retrieves information about the life cycle of libraries and frameworks of interest from the site https://endoflife.date/ and saves the same in another collection of the same Mongo DB

### Example of metadada/config's info in Mongo:
![mongo config info](https://github.com/companieshouse/services-dashboard-api/blob/7779a9d/images/mongo.config.png?raw=true)

## Deployed as lambda function
This service is released as a lambda function. The reason for running it as a lambda function instead of another resource (example ECS) is that it is currently designed to be executed once a day. 
Current values, of 302 projects, give a total execution time of `~ 212822 ms` (3 minutes and 33 seconds). It is therefore convenient and economical to use AWS resources only for this daily time 
rather than keeping them busy and unused for longer.

### Number of projects monitored in Dependecy Track:
![dependecy track projects](https://github.com/companieshouse/services-dashboard-api/blob/7779a9d/images/dependency-track.projects.png?raw=true)

### Lambda execution time:
![lambda execution time](https://github.com/companieshouse/services-dashboard-api/blob/7779a9d/images/lambda.duration.png?raw=true)

## services-dashboard components
The services dashboard is made up of several independent and coordinated components.
In addition to this API that plays the role of main information in Mongo, other components include:

- [services-dashboard-web](https://github.com/companieshouse/services-dashboard-web): the frontend that displays the actual dashboard
- [services-dashboard-ecs](https://github.com/companieshouse/services-dashboard-ecs): responsible for collecting information about tasks running on the various ECS clusters (cidev / staging / live)

The DB is a meeting point for other components of the services dashboard.
In general, the services dashboard is therefore extensible with any other components that add information to the DB.

## Deployments
Depending on what they do, the various components have different needs to be deployed in the various environments:
- `services-dashboard-api`: one instance is sufficient (currently deployed in cidev)
- `services-dashboard-web`: one instance is sufficient (currently deployed in cidev)
- `services-dashboard-ecs`: one instance is required for each AWS account (and relatedf ECS clusters) that needs to be inspected (so 3 instances: cidev/staging/live)
