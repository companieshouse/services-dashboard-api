 export SERVER_PORT=8080

 export MONGODB_PROTOCOL='mongodb+srv'
 export MONGODB_USER='rand_dev'
 export MONGODB_PASSWORD="${DT_MONGODB_PASSWORD}"
 export MONGODB_HOST_AND_PORT='ci-dev-pl-0.ueium.mongodb.net'
 export MONGODB_DBNAME='services_dashboard'


 export DT_SERVER_APIKEY="${DT_SERVER_APIKEY}"
 export DT_SERVER_BASEURL='https://dependency-track.companieshouse.gov.uk'
 export GH_TOKEN="${GH_LAMBDA_TOKEN}"

 export AWS_PROFILE=dev
 export AWS_ENVS=cidev,staging
 export SSM_PREFIX='/some/value'

 export SONAR_URL='https://sonarqube.companieshouse.gov.uk'
 #export SONAR_TOKEN="${DT_SONAR_TOKEN}"
 export SONAR_TOKEN="${DT_SONAR_TOKEN_NEW}"
