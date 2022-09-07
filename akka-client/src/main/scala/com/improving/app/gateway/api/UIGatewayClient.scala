package com.improving.app.gateway.api

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import com.typesafe.config.ConfigFactory

object UIGatewayClient {
  def initiateClient(): UIGatewayServiceClient = {
    // Boot akka
    implicit val sys =
      ActorSystem("UIGatewayClient", ConfigFactory.load("application.conf"))
    //implicit val ec = sys.dispatcher

    // Configure the client by code:
    val clientSettings = GrpcClientSettings
      .connectToServiceAt("gateway.improving.app", 443)
      .withTls(true)

    UIGatewayServiceClient(clientSettings)
  }
}
