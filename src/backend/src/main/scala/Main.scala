import com.typesafe.config.{Config, ConfigFactory}
import models.response.CoinMarketApiResponse
import models.{ApiGenerator, DataGenerator}
import zio.{
  &,
  Ref,
  Schedule,
  Scope,
  ZIO,
  ZIOAppArgs,
  ZIOAppDefault,
  ZLayer,
  durationInt
}
import zio.http._
import zio.json.EncoderOps
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde

import java.time.Instant
import collection.JavaConverters._
import scala.util.Try

object Main extends ZIOAppDefault {

  val runZIO: ZIO[
    Any with ZIOAppArgs with Scope with DataGenerator with Client with Config with Producer with ProducerTrait,
    Any,
    Unit] = {
    (for {
      config <- ZIO.service[Config]
      _ = println(config.entrySet())
      cryptoSlugs <- ZIO.succeed(
        config.getStringList("cryptoSlugs").asScala.toList
      )
      dataTopic <- ZIO.succeed(
        Try(config.getString("dataTopic")).getOrElse("dataTopic")
      )
      requestsSentTopic <- ZIO.succeed(
        Try(config.getString("requestsSentTopic"))
          .getOrElse("requestsSentTopic")
      )
      // use kafka as database, sum messages up
      // not great but don't want db
      // not doing right now, follow up to make it better
      // or do it properly some other way
      creditsUsedThisMonth <- Ref.make(0)
      dataGenerator <- ZIO.service[DataGenerator]
      producerT <- ZIO.service[ProducerTrait]
      _ <- {
        (
          for {
            data <- dataGenerator.getCryptoPrices(cryptoSlugs)
            _ <- ZIO.logInfo(data.toJsonPretty)
            _ <- creditsUsedThisMonth.update(_ + data.status.credit_count)
            creditsUsed <- creditsUsedThisMonth.get
            cleanedMessage <- ZIO.succeed(
              data.data.values
                .map(x => (x.name, x.quote.head._2.price))
                .toMap
                .toJson
            )
            _ <- ZIO.logInfo(s"Credits used: ${creditsUsed}")

            // produce the data event
            _ <- producerT
              .produce[String](dataTopic,
                               Instant.EPOCH.toEpochMilli.toString,
                               cleanedMessage)(
                Serde.string
              )

            // produce event with credits used here
            _ <- producerT.produce[Int](requestsSentTopic,
                                        Instant.EPOCH.toEpochMilli.toString,
                                        creditsUsed)(
              Serde.int
            )

          } yield data
        ).tapBoth(
            f => ZIO.logError(f.getMessage),
            g => ZIO.logInfo(g.data.toJson)
          )
          .repeat(
            Schedule.fromDuration(5.minute).forever
          )
      }

    } yield ())
  }

  override def run =
    runZIO
      .provide(
        Scope.default,
        ApiGenerator.layer,
        ZIOAppArgs.empty,
        Client.default,
        ProducerObj.producerSettingsLayer,
        Producer.live,
        ProducerObj.producerLayer,
        ZLayer.succeed[Config](ConfigFactory.parseResources("application.conf"))
      )

}
