package com.improving.app.gateway.api

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UIGatewayClientSpec extends AnyWordSpec with Matchers {

  "UIGatewayClient" must {
    "receive appropriate reply upon calling establishOrganization, where" must {
      val caPostalCode =
        Some(PostalCode(PostalCode.Region.Ca(CAPostalCode("A1B2C3"))))
      val usPostalCode =
        Some(PostalCode(PostalCode.Region.Us(USPostalCode("12345"))))
      val caAddress = Address(
        "123 LineOne Lane",
        "123 LineTwo Lane",
        "Ottawa",
        "ON",
        "CA",
        caPostalCode
      )
      val usAddress = Address(
        "123 LineOne Lane",
        "123 LineTwo Lane",
        "Bozeman",
        "Montana",
        "US",
        usPostalCode
      )
      val info = OrganizationInfo(
        name = Some("Test Name"),
        shortName = Some("Test"),
        address = Some(caAddress),
        parentOrg = None,
        childOrgs = Seq(),
        isPrivate = None,
        url = None
      )
      val client = UIGatewayClient.initiateClient()
      "there is no parent (success)" in {
        val command = EstablishOrganizationCommand(Some(info))
        val replyInfo = command.baseInfo.get.copy(isPrivate = Some(false))
        val reply = client.establishOrganization(command)
        reply.map(_.orgInfo shouldEqual replyInfo)
      }
      "there is a parent (success)" in {
        val command = EstablishOrganizationCommand(
          Some(info.copy(parentOrg = Some(UUID.randomUUID().toString)))
        )
        val replyInfo = command.baseInfo.get.copy(isPrivate = Some(false))
        val reply = client.establishOrganization(command)
        reply.map(_.orgInfo should be(replyInfo))
      }
      "there is a US postal code (success)" in {
        val command = EstablishOrganizationCommand(
          Some(info.copy(address = Some(usAddress)))
        )
        val replyInfo = command.baseInfo.get.copy(isPrivate = Some(false))
        val reply = client.establishOrganization(command)
        reply.map(_.orgInfo should be(replyInfo))
      }
      "there is an invalid CA postal code (fail)" in {
        val invalidCaPostalCode =
          Some(PostalCode(PostalCode.Region.Ca(CAPostalCode("12345"))))
        val command = EstablishOrganizationCommand(
          Some(
            info.copy(address =
              Some(info.address.get.copy(postalCode = invalidCaPostalCode))
            )
          )
        )
        val reply = client.establishOrganization(command)
        reply.onComplete {
          case Failure(_) => assert(true)
          case Success(_) => assert(false)
        }
      }
      "there is an invalid US postal code with a letter (fail)" in {
        val invalidUsPostalCode =
          Some(PostalCode(PostalCode.Region.Us(USPostalCode("A1B2C3"))))
        val command = EstablishOrganizationCommand(
          Some(
            info.copy(address =
              Some(info.address.get.copy(postalCode = invalidUsPostalCode))
            )
          )
        )
        val reply = client.establishOrganization(command)
        //TODO: turn into function
        reply.onComplete {
          case Failure(_) => assert(true)
          case Success(_) => assert(false)
        }
      }
      "there is no OrganizationInfo (fail)" in {
        val command = EstablishOrganizationCommand(None)
        val reply = client.establishOrganization(command)
        reply.onComplete {
          case Failure(_) => assert(true)
          case Success(_) => assert(false)
        }
      }
      "there is no PostalCode (fail)" in {
        val command = EstablishOrganizationCommand(
          Some(info.copy(address = Some(caAddress.copy(postalCode = None))))
        )
        val reply = client.establishOrganization(command)
        reply.onComplete {
          case Failure(_) => assert(true)
          case Success(_) => assert(false)
        }
      }
    }
  }
}
