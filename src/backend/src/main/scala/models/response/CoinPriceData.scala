package models.response

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class CoinPriceData(
    id: Int,
    name: String,
    slug: String,
    quote: Map[String, QuoteData]
)
object CoinPriceData {
  implicit val decoder: JsonDecoder[CoinPriceData] =
    DeriveJsonDecoder.gen[CoinPriceData]
  implicit val encoder: JsonEncoder[CoinPriceData] =
    DeriveJsonEncoder.gen[CoinPriceData]
}
