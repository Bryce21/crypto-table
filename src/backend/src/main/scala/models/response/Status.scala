package models.response

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Status(
    credit_count: Int,
    error_code: Int,
    //      error_message: String
)

object Status {
  implicit val decoder: JsonDecoder[Status] =
    DeriveJsonDecoder.gen[Status]
  implicit val encoder: JsonEncoder[Status] =
    DeriveJsonEncoder.gen[Status]
}
