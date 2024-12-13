locals {
  lambda_function_name = "${var.service}-${var.environment}"
  vault_secrets        = data.vault_generic_secret.configuration.data
}



resource "aws_lambda_function" "services_dashboard_api" {
  depends_on = [aws_cloudwatch_log_group.services_dashboard_api]

  function_name = local.lambda_function_name
  s3_bucket     = var.release_bucket_name
  s3_key        = var.release_artifact_key
  role          = aws_iam_role.lambda_execution.arn
  handler       = var.lambda_handler_name
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout_seconds
  runtime       = var.lambda_runtime

  environment {
    variables = {
      CONVERSION_COLOUR_SPACE             = local.vault_secrets["conversion_colour_space"]
      CONVERSION_COMPRESSION              = local.vault_secrets["conversion_compression"]
      CONVERSION_DOTS_PER_INCH            = local.vault_secrets["convertion_dots_per_inch"]
      CONVERSION_NOTIFY_FAILED_CONVERSION = local.vault_secrets["conversion_notify_failed_conversion"]
      CONVERSION_FULL_PAGE_HEIGHT         = local.vault_secrets["conversion_full_page_height"]
      CONVERSION_FULL_PAGE_WIDTH          = local.vault_secrets["conversion_full_page_width"]
      CONVERSION_BINARY_CONTRAST_OFFSET   = local.vault_secrets["conversion_binary_contrast_offset"]
      CONVERSION_BINARY_CONTRAST_SCALE    = local.vault_secrets["conversion_binary_contrast_scale"]
      EFS_DOCUMENT_CONVERTED_API_KEY      = local.vault_secrets["efs_document_converted_api_key"]
      EFS_DOCUMENT_CONVERTED_URL          = local.vault_secrets["efs_document_converted_url"]
      FILE_TRANSFER_API_KEY               = local.vault_secrets["file_transfer_api_key"]
      FILE_TRANSFER_API_URL               = local.vault_secrets["file_transfer_api_url"]
      S3_BUCKET_NAME                      = local.vault_secrets["s3_bucket_name"]
    }
  }

  vpc_config {
    subnet_ids         = var.lambda_vpc_access_subnet_ids
    security_group_ids = [aws_security_group.services_dashboard_api.id]
  }

  tags = {
    Name        = local.lambda_function_name
    Environment = var.environment
    Service     = var.service
  }
}

resource "aws_lambda_event_source_mapping" "data" {
  event_source_arn = aws_sqs_queue.data.arn
  function_name    = aws_lambda_function.services_dashboard_api.arn
  batch_size       = var.lambda_event_source_batch_size
}

resource "aws_security_group" "services_dashboard_api" {
  name        = "${var.service}-${var.environment}-lambda-function"
  description = "Security group for Lambda function access to VPC resources"
  vpc_id      = var.vpc_id

  # TODO limit access to required resources in VPC

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = [ "0.0.0.0/0" ]
  }

  tags = {
    Name        = "${var.service}-${var.environment}-lambda-function"
    Environment = var.environment
    Service     = var.service
  }
}

# CloudWatch Log Groups

resource "aws_cloudwatch_log_group" "services_dashboard_api" {
  name              = "/aws/lambda/${local.lambda_function_name}"
  retention_in_days = var.lambda_logs_retention_days
}

# IAM role and associated resources that permit Lambda function to assume the
# role granting it all necessary permissions to maintain logs, manage network
# interfaces, read and write to the S3 buckets specified, and generate data
# keys for client-side encryption

resource "aws_iam_role" "lambda_execution" {
  name               = "${var.service}-${var.environment}-lambda-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_trust.json
}

resource "aws_iam_role_policy_attachment" "vpc_access" {
  role = aws_iam_role.lambda_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "sqs_access" {
  role = aws_iam_role.lambda_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaSQSQueueExecutionRole"
}

resource "aws_iam_role_policy" "vpc_execution" {
  name   = "${var.service}-${var.environment}-lambda-policy"
  role   = aws_iam_role.lambda_execution.id
  policy = data.aws_iam_policy_document.lambda_execution.json
}

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

# TODO retrieve key ID from remote state (post file-transfer-api rework)

data "aws_kms_key" "file_transfer_api" {
  key_id = var.file_transfer_api_kms_key_id
}

data "aws_iam_policy_document" "lambda_execution" {
  statement {
    sid = "LambdaHasFullAccessToImageBucket"

    effect = "Allow"

    # TODO further restrict permissions

    actions = [
      "s3:*",
    ]

    resources = [
      "${data.aws_s3_bucket.images.arn}/*"
    ]
  }

  statement {
    sid = "LambdaCanUseCustomerManagedKeyToGenerateDataKeys"

    effect = "Allow"

    actions = [
      "kms:GenerateDataKey"
    ]

    resources = [
      data.aws_kms_key.file_transfer_api.arn
    ]
  }
}

data "aws_vpc" "selected" {
  id = var.vpc_id
}