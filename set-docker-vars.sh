LOCAL_ENV_FILE='source.me.sh'
DOCKER_ENV_FILE='env.list'
source "${LOCAL_ENV_FILE}"

> "${DOCKER_ENV_FILE}"

grep '^ *export' "${LOCAL_ENV_FILE}" | awk '{print $2}' | cut -d'=' -f1 | while read VAR; do

  VALUE=$(printenv $VAR)  # Get the value of the var from the current env
  if [ ! -z "$VALUE" ]; then
    echo "$VAR=$VALUE" >> "${DOCKER_ENV_FILE}"
  fi
done

## append AWS env vars
#AWS_PROFILE='dev'
#
#
#AWS_ACCESS_KEY_ID=$(    aws configure get aws_access_key_id     --profile $AWS_PROFILE)
#AWS_SECRET_ACCESS_KEY=$(aws configure get aws_secret_access_key --profile $AWS_PROFILE)
#AWS_SESSION_TOKEN=$(    aws configure get aws_session_token     --profile $AWS_PROFILE)
#
#echo "AWS_ACCESS_KEY_ID='$AWS_ACCESS_KEY_ID'"         >> "${DOCKER_ENV_FILE}"
#echo "AWS_SECRET_ACCESS_KEY='$AWS_SECRET_ACCESS_KEY'" >> "${DOCKER_ENV_FILE}"
#echo "AWS_SESSION_TOKEN='$AWS_SESSION_TOKEN'"         >> "${DOCKER_ENV_FILE}"
