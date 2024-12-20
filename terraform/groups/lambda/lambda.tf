
# Create an IAM role for the Lambda function
resource "aws_iam_role" "lambda_execution_role" {
  name               = "${local.lambda_function_name}-lambda-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_trust.json
}

# Create a policy to allow Lambda to access SSM Parameter Store
resource "aws_iam_policy" "ssm_access_policy" {
  name        = "${local.lambda_function_name}-access-policy"
  description = "Policy to allow Lambda access to SSM Parameter Store"

  policy = data.aws_iam_policy_document.ssm_access_policy.json
}

# Attach the SSM access policy to the Lambda execution role
resource "aws_iam_role_policy_attachment" "ssm_policy_attachment" {
  role       = aws_iam_role.lambda_execution_role.name
  policy_arn = aws_iam_policy.ssm_access_policy.arn
}

# SSM Parameters
resource "aws_ssm_parameter" "secrets" {
   for_each = local.ssm_secrets_keys

  name  = "${local.ssm_prefix}/${each.key}"
  value = local.ssm_secrets_nonsensitive[each.key]
  type  = "SecureString"
}


# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name              = "/aws/lambda/${local.lambda_function_name}"
  retention_in_days = var.lambda_logs_retention_days
}

# Create the Lambda function
resource "aws_lambda_function" "java_lambda" {
  depends_on = [aws_cloudwatch_log_group.lambda_log_group]

  function_name = local.lambda_function_name
  role          = aws_iam_role.lambda_execution_role.arn

  s3_bucket = var.release_bucket_name
  s3_key    = var.release_artifact_key

  handler = var.lambda_handler_name
  runtime = var.lambda_runtime

  memory_size = var.lambda_memory_size
  timeout     = var.lambda_timeout_seconds

  environment {
    variables = {
      MONGODB_PROTOCOL  = local.mongo_protocol
      MONGODB_DBNAME    = local.mongo_dbname
      DT_SERVER_BASEURL = local.dt_server_baseurl
    }
  }

  tags = {
    Name        = local.lambda_function_name
    Environment = var.environment
  }
}

# Create a CloudWatch Event Rule to trigger the Lambda function at scheduled intervals
resource "aws_cloudwatch_event_rule" "daily_load_all" {
  name                = "${local.lambda_function_name}-loadall"
  description         = "Trigger Lambda at 06:00AM daily"
  schedule_expression = "cron(0 6 * * ? *)"
}

# Create a CloudWatch Event Target to trigger the Lambda function
resource "aws_cloudwatch_event_target" "lambda_target" {
  rule = aws_cloudwatch_event_rule.daily_load_all.name
  arn  = aws_lambda_function.java_lambda.arn

  input = jsonencode({
    action = "loadAllInfo"
  })
}
# Allow the CloudWatch Event Rule to trigger the Lambda function
resource "aws_lambda_permission" "allow_eventbridge" {
  statement_id  = "AllowEventBridgeToInvokeLambda"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.java_lambda.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.daily_load_all.arn
}
