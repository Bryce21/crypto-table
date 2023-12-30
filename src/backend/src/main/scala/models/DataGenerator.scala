package models

import zio._
import zio.json._
import zio.json.ast.Json
import models.response.CoinMarketApiResponse
import zio.{&, Scope, ZIO}
import zio.http.{Client, URL}
import com.typesafe.config.{Config, ConfigFactory}

sealed trait DataGenerator {
  val name: String

  def getCryptoPrices(cryptoSlugs: List[String])
    : ZIO[Scope & Client & Config, Throwable, CoinMarketApiResponse]

  val layer: ZLayer[Any, Throwable, DataGenerator]
}

case class MockedData(data: CoinMarketApiResponse) extends DataGenerator {
  override val name: String = "mockedData"

  override def getCryptoPrices(cryptoSlugs: List[String])
    : ZIO[Scope & Client & Config, Throwable, CoinMarketApiResponse] =
    ZIO.succeed(data)

  override val layer: ZLayer[Any, Throwable, DataGenerator] = {
    ZLayer.succeed(this)
  }
}

object ApiGenerator extends DataGenerator {
  override val name: String = "ApiGenerator"

  def getCryptoPrices(cryptoSlugs: List[String])
    : ZIO[Scope & Client & Config, Throwable, CoinMarketApiResponse] = {
    for {
      client <- ZIO.service[Client]
      config <- ZIO.service[Config]
      res <- client
        .url(
          URL
            .decode(
              config.getString("apiUrl")
            )
            .toOption
            .get
        )
        .addQueryParam(
          "slug",
          cryptoSlugs.mkString(",")
        )
        // todo move secret to env
        .addHeader(name = config.getString("authHeaderName"),
                   value = config.getString("authHeaderValue"))
        .get("")
      bodyString <- res.body.asString
      bodyJson <- ZIO
        .fromEither(bodyString.fromJson[CoinMarketApiResponse])
        .mapError(new Throwable(_))
    } yield bodyJson
  }

  override val layer: ZLayer[Any, Throwable, DataGenerator] = {
    ZLayer.succeed(ApiGenerator)
  }
}
