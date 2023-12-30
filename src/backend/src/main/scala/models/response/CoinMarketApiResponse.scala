package models.response

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

/*
{"status":{"timestamp":"2023-12-10T17:42:25.544Z","error_code":0,"error_message":null,"elapsed":16,"credit_count":1,"notice":null},"data":{"1":{"id":1,"name":"Bitcoin","symbol":"BTC","slug":"bitcoin","num_market_pairs":10601,"date_added":"2010-07-13T00:00:00.000Z","tags":["mineable","pow","sha-256","store-of-value","state-channel","coinbase-ventures-portfolio","three-arrows-capital-portfolio","polychain-capital-portfolio","binance-labs-portfolio","blockchain-capital-portfolio","boostvc-portfolio","cms-holdings-portfolio","dcg-portfolio","dragonfly-capital-portfolio","electric-capital-portfolio","fabric-ventures-portfolio","framework-ventures-portfolio","galaxy-digital-portfolio","huobi-capital-portfolio","alameda-research-portfolio","a16z-portfolio","1confirmation-portfolio","winklevoss-capital-portfolio","usv-portfolio","placeholder-ventures-portfolio","pantera-capital-portfolio","multicoin-capital-portfolio","paradigm-portfolio","bitcoin-ecosystem","ftx-bankruptcy-estate"],"max_supply":21000000,"circulating_supply":19566118,"total_supply":19566118,"is_active":1,"infinite_supply":false,"platform":null,"cmc_rank":1,"is_fiat":0,"self_reported_circulating_supply":null,"self_reported_market_cap":null,"tvl_ratio":null,"last_updated":"2023-12-10T17:41:00.000Z","quote":{"USD":{"price":43789.71204023702,"volume_24h":13002784941.259054,"volume_change_24h":-35.9144,"percent_change_1h":-0.06845744,"percent_change_24h":-0.45936959,"percent_change_7d":10.66834571,"percent_change_30d":17.73793594,"percent_change_60d":64.42340687,"percent_change_90d":74.10640216,"market_cap":856794672965.2983,"market_cap_dominance":52.4544,"fully_diluted_market_cap":919583952844.98,"tvl":null,"last_updated":"2023-12-10T17:41:00.000Z"}}}}}
 */

case class CoinMarketApiResponse(
    status: Status,
    data: Map[String, CoinPriceData]
)

object CoinMarketApiResponse {
  implicit val decoder: JsonDecoder[CoinMarketApiResponse] =
    DeriveJsonDecoder.gen[CoinMarketApiResponse]
  implicit val encoder: JsonEncoder[CoinMarketApiResponse] =
    DeriveJsonEncoder.gen[CoinMarketApiResponse]
}
