variable "github_read_token" {
  type        = string
  description = "GitHub access token to read repos' info from both 'repo' or 'public_repo' scopes"
}

variable "aws_account" {
  type        = string
  description = "The AWS account name"
  default     = "development"
}

    variable "aws_account_id" {
      type        = string
      description = "The AWS account identifier"
    }
variable "aws_profile" {
  type        = string
  description = "The AWS profile name; used as a prefix for Vault secrets"
}

variable "aws_region" {
  type        = string
  description = "The AWS region in which resources will be created"
}

variable "environment" {
  type        = string
  description = "The environment name to be used when creating AWS resources"
}

# variable "service" {
#   type        = string
#   description = "The service name to be used when creating AWS resources"
#   default     = "services-dashboard-api"
# }

# variable "file_transfer_api_kms_key_alias" {
#   type        = string
#   description = "The KMS key alias of the customer-managed key in KMS used by the file transfer API service for S3 bucket encryption"
# }

# variable "image_bucket_name" {
#   type        = string
#   description = "The name of the S3 bucket that will store processed images"
# }

# variable "image_bucket_prefix" {
#   type        = string
#   description = "The prefix for objects stored in the S3 image bucket; must begin with a leading forward slash and end without a trailing forward slash"
# }

# variable "lambda_event_source_batch_size" {
#   type        = number
#   description = "The largest number of records that Lambda will retrieve from the SQS event source at the time of invocation"
#   default     = 10
# }

variable "lambda_handler_name" {
  type        = string
  description = "The lambda function entrypoint"
  default     = "uk.gov.companieshouse.efs.documentconverter.DocumentMessageHandler::handleRequest"
}

variable "lambda_logs_retention_days" {
  type        = number
  description = "The number of days to retain Lambda logs in CloudWatch"
  default     = 7
}

variable "lambda_memory_size" {
  type        = string
  description = "The amount of memory made available to the Lambda function at runtime in megabytes"
  default     = "4096"
}

variable "lambda_timeout_seconds" {
  type        = string
  description = "The amount of time the lambda function is allowed to run before being stopped"
  default     = 600
}

variable "lambda_runtime" {
  type        = string
  description = "The lambda runtime to use for the function"
  default     = "java21"
}

    # variable "lambda_vpc_access_subnet_ids" {
    #   type        = list(string)
    #   description = "A list of subnet identifiers the Lambda function will be able to access resources in"
    # }

# variable "vpc_id" {
#   type        = string
#   description = "The VPC in which to create resources"
# }


variable "release_bucket_name" {
  type        = string
  description = "The name of the S3 bucket containing the release artefact for the Lambda function"
}

variable "release_artifact_key" {
  type        = string
  description = "The release artifact key for the Lambda function"
}

# variable "data_queue_max_receive_count" {
#   type        = number
#   description = "The maximum number of times a message may be received before being moved to the dead-letter queue"
#   default     = 2
# }

# variable "data_queue_visibility_timeout_seconds" {
#   type        = number
#   description = "The visibility timeout for the queue in seconds"
#   default     = 1200
# }

variable "network_state_bucket_name" {
  type        = string
  description = "The name of the S3 bucket containing the application network remote state"
}

variable "network_state_bucket_key" {
  type        = string
  description = "The key name used when constructing the path to the application network remote state in the S3 bucket"
}

# variable "vpc_name" {
#   type        = string
#   description = "The VPC in which to create resources"
#   default     = "Test & Development"
# }

# variable vault_username {
#   type        = string
#   description = "The HashiCorp Vault username used to retrieve secrets"
# }

# variable vault_password {
#   type        = string
#   description = "The HashiCorp Vault password used to retrieve secrets"
# }

# variable "message_retention_seconds" {
#   type        = number
#   default     = 345600
#   description = "The number of seconds Amazon SQS retains a message"
# }


# ------------------------------------------------------------------------------
# Service environment variable configs
# ------------------------------------------------------------------------------
variable "ssm_version_prefix" {
  type        = string
  description = "String to use as a prefix to the names of the variables containing variables and secrets version."
  default     = "SSM_VERSION_"
}

# variable "use_set_environment_files" {
#   type        = bool
#   default     = false
#   description = "Toggle default global and shared  environment files"
# }

variable "services_dashboard_api_version" {
  type        = string
  description = "The version of the services dashboard api container to run."
}

variable "vault_stack_path" {
  type        = string
  description = "Vault stack path."
}

variable "vault_service_path" {
  type        = string
  description = "Vault service path."
}