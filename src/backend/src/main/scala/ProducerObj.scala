import com.typesafe.config.Config
import models.response.CoinMarketApiResponse
import org.apache.kafka.clients.producer.RecordMetadata
import zio.{ZIO, ZLayer}
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

import scala.jdk.CollectionConverters.CollectionHasAsScala

trait ProducerTrait {
  def produce[A](topic: String, key: String, value: A)(
      implicit serializer: Serializer[Any, A])
    : ZIO[Producer, Throwable, RecordMetadata]

  def producerSettingsLayer: ZLayer[Config, Throwable, ProducerSettings]

  def producerLayer: ZLayer[Producer, Throwable, ProducerTrait]
}

object ProducerObj extends ProducerTrait {

  def produce[A](topic: String, key: String, value: A)(
      implicit serializer: Serializer[Any, A])
    : ZIO[Producer, Throwable, RecordMetadata] = {
    Producer.produce[Any, String, A](
      topic = topic,
      key = key,
      value = value,
      keySerializer = Serde.string,
      valueSerializer = serializer
    )
  }

  def producerLayer: ZLayer[Producer, Throwable, ProducerTrait] =
    ZLayer.succeed(ProducerObj)

  override def producerSettingsLayer
    : ZLayer[Config, Throwable, ProducerSettings] = {

    ZLayer.fromZIO(
      ZIO.serviceWith { (config: Config) =>
        {
          ProducerSettings.apply(
            config.getStringList("bootstrapServers").asScala.toList
          )
        }
      }
    )

  }
}

object ProducerTestObj extends ProducerTrait {
  override def produce[A](topic: String, key: String, value: A)(
      implicit serializer: Serializer[Any, A])
    : ZIO[Producer, Throwable, RecordMetadata] = ???

  override def producerLayer: ZLayer[Producer, Throwable, ProducerTrait] = ???

  override def producerSettingsLayer
    : ZLayer[Config, Throwable, ProducerSettings] = ???
}
