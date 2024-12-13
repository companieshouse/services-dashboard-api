data "aws_iam_policy_document" "data_queue" {
  statement {
    sid = "ApiProducerFullQueueAccess"

    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [aws_iam_user.efs_queue_message_producer.arn]
    }

    actions = [
      "sqs:*"
    ]

    resources = [
      aws_sqs_queue.data.arn
    ]
  }
}



data "terraform_remote_state" "network" {
  backend = "s3"
  config = {
    bucket = var.network_state_bucket_name
    key    = "env:/${var.network_state_bucket_key}/${var.network_state_bucket_key}/${var.network_state_bucket_key}.tfstate"
    region = var.region
  }
}

data "aws_vpc" "vpc" {
  filter {
    name   = "tag:Name"
    values = [var.vpc_name]
  }
}

data "aws_kms_key" "kms_key" {
  key_id = var.file_transfer_api_kms_key_alias
}


data "aws_caller_identity" "current" {}




data "aws_s3_bucket" "images" {
  bucket = var.image_bucket_name
}

data "aws_s3_bucket" "efs_payment_reports" {
  bucket = "${var.aws_account}-${var.region}.efs-payment-reports.ch.gov.uk"
}

data "aws_iam_policy_document" "efs_queue_message_producer" {

  # API producer can list these buckets
  statement {
    effect = "Allow"

    actions = [
      "s3:ListBucket",
      "s3:GetBucketLocation"
    ]

    resources = [
      data.aws_s3_bucket.images.arn,
      data.aws_s3_bucket.efs_payment_reports.arn
    ]
  }

  # API producer can write to these buckets
  statement {
    effect = "Allow"

    actions = [
      "s3:ListObjectsV2",
      "s3:PutObject",
      "s3:PutObjectAcl",
      "s3:GetObject",
      "s3:GetObjectAcl",
      "s3:DeleteObject"
    ]

    resources = [
      "${data.aws_s3_bucket.images.arn}${var.image_bucket_prefix}/*",
      "${data.aws_s3_bucket.efs_payment_reports.arn}/${var.environment}/*"
    ]
  }

  # Presigned links can read from these buckets
  statement {
    effect = "Allow"

    actions = [
      "s3:GetObject"
    ]

    resources = [
      "${data.aws_s3_bucket.images.arn}/*",
      "${data.aws_s3_bucket.efs_payment_reports.arn}/${var.environment}/*"
    ]
  }
}



data "vault_generic_secret" "configuration" {
  path = "applications/${var.aws_profile}/${var.environment}/${var.service}/lambda_environment_variables"
}








data "vault_generic_secret" "stack_secrets" {
  path = "applications/${var.aws_profile}/${var.environment}/${local.stack_name}-stack"
}

data "aws_kms_key" "kms_key" {
  key_id = local.kms_alias
}

data "vault_generic_secret" "service_secrets" {
  path = "applications/${var.aws_profile}/${var.environment}/${local.stack_name}-stack/${local.service_name}"
}

data "aws_vpc" "vpc" {
  filter {
    name   = "tag:Name"
    values = [local.vpc_name]
  }
}

#Get application subnet IDs
data "aws_subnets" "application" {
  filter {
    name   = "tag:Name"
    values = [local.application_subnet_pattern]
  }
}

data "aws_ecs_cluster" "ecs_cluster" {
  cluster_name = "${local.name_prefix}-cluster"
}

data "aws_iam_role" "ecs_cluster_iam_role" {
  name = "${local.name_prefix}-ecs-task-execution-role"
}

data "aws_lb" "rand_lb" {
  name = "${local.lb_name}"
}

data "aws_lb_listener" "rand_lb_listener" {
  load_balancer_arn = data.aws_lb.rand_lb.arn
  port = 443
}

# retrieve all secrets for this stack using the stack path
data "aws_ssm_parameters_by_path" "secrets" {
  path = "/${local.name_prefix}"
}

# create a list of secrets names to retrieve them in a nicer format and lookup each secret by name
data "aws_ssm_parameter" "secret" {
  for_each = toset(data.aws_ssm_parameters_by_path.secrets.names)
  name = each.key
}

# retrieve all global secrets for this env using global path
data "aws_ssm_parameters_by_path" "global_secrets" {
  path = "/${local.global_prefix}"
}

# create a list of secrets names to retrieve them in a nicer format and lookup each secret by name
data "aws_ssm_parameter" "global_secret" {
  for_each = toset(data.aws_ssm_parameters_by_path.global_secrets.names)
  name     = each.key
}
