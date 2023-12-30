package models.response

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class QuoteData(
    price: Float
)
object QuoteData {
  implicit val decoder: JsonDecoder[QuoteData] =
    DeriveJsonDecoder.gen[QuoteData]
  implicit val encoder: JsonEncoder[QuoteData] =
    DeriveJsonEncoder.gen[QuoteData]
}
