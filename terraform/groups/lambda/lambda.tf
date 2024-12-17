data "aws_iam_policy_document" "lambda_trust" {
  statement {
    sid = "LambdaCanAssumeThisRole"

    effect = "Allow"

    actions = [
      "sts:AssumeRole"
    ]

    principals {
      type = "Service"

      identifiers = [
        "lambda.amazonaws.com"
      ]
    }
  }
}
resource "aws_iam_role" "lambda_execution" {
  name               = "${local.service_name}-${var.environment}-lambda-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_trust.json
}

# CloudWatch Log Groups

resource "aws_cloudwatch_log_group" "services_dashboard_api" {
  name              = "/aws/lambda/${local.lambda_function_name}"
  retention_in_days = var.lambda_logs_retention_days
}

resource "aws_lambda_function" "services_dashboard_api" {
  depends_on = [aws_cloudwatch_log_group.services_dashboard_api]

  function_name = local.lambda_function_name
  role          = aws_iam_role.lambda_execution.arn

  s3_bucket     = var.release_bucket_name
  s3_key        = var.release_artifact_key

  handler       = var.lambda_handler_name
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout_seconds
  runtime       = var.lambda_runtime

  environment {
    variables = {
      MONGODB_PROTOCOL      = local.mongo_protocol
      MONGODB_USER          = local.mongo_user
      MONGODB_PASSWORD      = local.mongo_password
      MONGODB_HOST_AND_PORT = local.mongo_host_and_port
      MONGODB_DBNAME        = local.mongo_dbname

      DT_SERVER_APIKEY      = local.dt_server_apikey
      DT_SERVER_BASEURL     = local.dt_server_baseurl

      SONAR_TOKEN           = local.sonar_token

      GH_TOKEN              = var.github_read_token
        # {
        #   "admin_prefix_list_name": "administration-cidr-ranges",
        #   "dt_server_apikey": "odt_G3YLDqaOz7VnR0cSIOcgzQIeQ1FsuLQX",
        #   "hosted_zone_name": "aws.chdev.org",
        #   "mongo_host_and_port": "ci-dev-pl-0.ueium.mongodb.net",
        #   "mongo_password": "RDEw5zzhvnm23Hgu",
        #   "mongo_user": "rand_dev",
        #   "on_premise_prefix_list_name": "on-premise-cidr-ranges",
        #   "sonar_token": "4f55cde8c26cc05242c9e75ccba7dd92aa3a00f8"
        # }      
    }
  }

  # vpc_config {
  #   subnet_ids         = var.lambda_vpc_access_subnet_ids
  #   security_group_ids = [aws_security_group.services_dashboard_api.id]
  # }

  tags = {
    Name        = local.lambda_function_name
    Environment = var.environment
    Service     = local.service_name
  }
}

# resource "aws_lambda_event_source_mapping" "data" {
#   event_source_arn = aws_sqs_queue.data.arn
#   function_name    = aws_lambda_function.services_dashboard_api.arn
#   batch_size       = var.lambda_event_source_batch_size
# }

# resource "aws_security_group" "services_dashboard_api" {
#   name        = "${local.service_name}-${var.environment}-lambda-function"
#   description = "Security group for Lambda function access to VPC resources"
#   vpc_id      = var.vpc_id

#   # TODO limit access to required resources in VPC

#   egress {
#     from_port   = 0
#     to_port     = 0
#     protocol    = "-1"
#     cidr_blocks = [ "0.0.0.0/0" ]
#   }

#   tags = {
#     Name        = "${local.service_name}-${var.environment}-lambda-function"
#     Environment = var.environment
#     Service     = local.service_name
#   }
# }

# IAM role and associated resources that permit Lambda function to assume the
# role granting it all necessary permissions to maintain logs, manage network
# interfaces, read and write to the S3 buckets specified, and generate data
# keys for client-side encryption


# resource "aws_iam_role_policy_attachment" "vpc_access" {
#   role = aws_iam_role.lambda_execution.name
#   policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
# }

# resource "aws_iam_role_policy_attachment" "sqs_access" {
#   role = aws_iam_role.lambda_execution.name
#   policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaSQSQueueExecutionRole"
# }

# resource "aws_iam_role_policy" "vpc_execution" {
#   name   = "${local.service_name}-${var.environment}-lambda-policy"
#   role   = aws_iam_role.lambda_execution.id
#   policy = data.aws_iam_policy_document.lambda_execution.json
# }

# TODO retrieve key ID from remote state (post file-transfer-api rework)

# data "aws_kms_key" "file_transfer_api" {
#   key_id = var.file_transfer_api_kms_key_id
# }

# data "aws_iam_policy_document" "lambda_execution" {
#   statement {
#     sid = "LambdaHasFullAccessToImageBucket"

#     effect = "Allow"

#     # TODO further restrict permissions

#     actions = [
#       "s3:*",
#     ]

#     resources = [
#       "${data.aws_s3_bucket.images.arn}/*"
#     ]
#   }

#   statement {
#     sid = "LambdaCanUseCustomerManagedKeyToGenerateDataKeys"

#     effect = "Allow"

#     actions = [
#       "kms:GenerateDataKey"
#     ]

#     resources = [
#       data.aws_kms_key.file_transfer_api.arn
#     ]
#   }
# }

# data "aws_vpc" "selected" {
#   id = var.vpc_id
# }