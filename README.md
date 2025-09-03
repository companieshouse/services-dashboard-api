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
Current values, of 302 projects, give for a full deep scan, a total execution time of `~ 212822 ms` (3 minutes and 33 seconds). It is therefore convenient and economical to use AWS resources only for this daily time
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
- [services-dashboard-tidyup](https://github.com/companieshouse/services-dashboard-tidyup): responsible for removing old versions from both Dep. Track and Mongo)

The DB is a meeting point for other components of the services dashboard.
In general, the services dashboard is therefore extensible with any other components that add information to the DB.

## Deployments
The various services are currently deployed in AWS/cidev. They work together to update Mongo, whose Atlas cluster must then be accessible from that AWS account.
- `services-dashboard-api`: one instance is sufficient (currently deployed in cidev)
- `services-dashboard-web`: one instance is sufficient (currently deployed in cidev)
- `services-dashboard-ecs`: one instance is sufficient (currently deployed in cidev)
- `services-dashboard-tidyup`: one instance is sufficient (currently deployed in cidev)


## AWS trigger/ manual test

The default execution ("light" scan) is triggered by [this json](https://github.com/companieshouse/services-dashboard-api/blob/5f167bd62a2a6bbdb7d6fc022430504522131e13/terraform/groups/lambda/lambda.tf#L100-L104):
```javascript
{
  "detail": {
    "action": "loadAllInfo"
  }
}
```
a different (manual) "deepscan" execution can instead be triggered with this :
```javascript
{
  "detail": {
    "action": "loadAllInfo",
    "deepscan": "true"
  }
}
```

### The lambda function performs this procedure:
1. it gets the whole list of projects/versions from Dep.Track (it's a list of their `uuids`)
2. all the uuids already stored/known in Mongo are removed, leaving a shorter (possible empty) list, with only the "new" [new=since the last run] projects.
3. for every entry of the list, the associated info (from GitHub and Sonar) are retrieved and the entry is stored in Mongo.

### "light" vs "deep" scan:
- light scan:
    - performs the above 2nd step.
    - An average light scan takes a few seconds.
- deep scan:
    - skips the 2nd step. This will query GitHub and Sonar for every entry. Possible use cases: some info in GitHub for that project have changed (ex. the scrum/owner of the repo is changed), a new version of Sonar (ex. different server-url becomes available and we want to refresh the data in Mongo [this happened])
    - A deepscan takes slightly more than `1min/100uuids`
