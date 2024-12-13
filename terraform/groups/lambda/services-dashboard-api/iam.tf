resource "aws_iam_user" "efs_queue_message_producer" {
  name = "efs-queue-message-producer-${var.environment}"
  path = "/applications/${var.service}/${var.environment}/"

  tags = {
    Name        = "efs-queue-message-producer-${var.environment}"
    Environment = var.environment
    Service     = var.service
  }
}

resource "aws_iam_access_key" "efs_queue_message_producer" {
  user = aws_iam_user.efs_queue_message_producer.name
}

resource "aws_iam_user_policy" "efs_queue_message_producer" {
  name   = "efs-queue-message-producer-${var.environment}"
  user   = aws_iam_user.efs_queue_message_producer.name
  policy = data.aws_iam_policy_document.efs_queue_message_producer.json
}

