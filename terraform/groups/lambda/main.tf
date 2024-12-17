terraform {
  backend "s3" {
  }
  required_version = "~> 1.3"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.72.0"
    }
    vault = {
      source  = "hashicorp/vault"
      version = "~> 3.18.0"
    }
  }
}

provider "aws" {
  region  = var.aws_region
}

# provider "vault" {
#   auth_login {
#     path = "auth/userpass/login/${var.vault_username}"

#     parameters = {
#       password = var.vault_password
#     }
#   }
# }
module "secrets" {
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=1.0.296"

  name_prefix = "${local.service_name}-${var.environment}"
  environment = var.environment
  kms_key_id  = data.aws_kms_key.kms_key.id
  secrets     = nonsensitive(local.service_secrets)
}
# module "services_dashboard_api" {
#   source = "./services-dashboard-api"

#   aws_account                           = var.aws_account
#   aws_account_id                        = data.aws_caller_identity.current.account_id
#   aws_profile                           = var.aws_profile
#   aws_region                            = var.aws_region
#   # data_queue_max_receive_count          = var.data_queue_max_receive_count
#   # data_queue_visibility_timeout_seconds = var.data_queue_visibility_timeout_seconds
#   environment                           = var.environment
#   # file_transfer_api_kms_key_id          = local.file_transfer_api_kms_key_id
#   # image_bucket_name                     = var.image_bucket_name
#   # image_bucket_prefix                   = var.image_bucket_prefix
#   lambda_event_source_batch_size        = var.lambda_event_source_batch_size
#   lambda_handler_name                   = var.lambda_handler_name
#   lambda_logs_retention_days            = var.lambda_logs_retention_days
#   lambda_memory_size                    = var.lambda_memory_size
#   lambda_runtime                        = var.lambda_runtime
#   lambda_timeout_seconds                = var.lambda_timeout_seconds
#   lambda_vpc_access_subnet_ids          = local.lambda_vpc_access_subnet_ids
#   # message_retention_seconds             = var.message_retention_seconds
#   release_artifact_key                  = var.release_artifact_key
#   release_bucket_name                   = var.release_bucket_name
#   service                               = var.service
#   vpc_id                                = local.vpc_id
# }
