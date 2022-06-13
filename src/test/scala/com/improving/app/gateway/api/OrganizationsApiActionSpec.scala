package com.improving.app.gateway.api

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsApiActionSpec
    extends AnyWordSpec
    with Matchers {

  "OrganizationsApiAction" must {
    "handle command establishOrganization where" must {
      val caPostalCode = Some(PostalCode(PostalCode.Region.Ca(CAPostalCode("A1B2C3"))))
      val usPostalCode = Some(PostalCode(PostalCode.Region.Us(USPostalCode("12345"))))
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
        url = None,
      )

      "there is no parent" in {
        val command = EstablishOrganizationCommand(Some(info))
        val replyInfo = command.baseInfo.get.copy(isPrivate = Some(false))

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).reply.orgInfo shouldBe Some(replyInfo)
      }
      "there is a parent" in {
        val command = EstablishOrganizationCommand(Some(info.copy(parentOrg = Some(UUID.randomUUID().toString))))
        val replyInfo = command.baseInfo.get.copy(isPrivate = Some(false))

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).reply.orgInfo shouldBe Some(replyInfo)
      }
      "there is a US postal code" in {
        val command = EstablishOrganizationCommand(Some(info.copy(address = Some(usAddress))))
        val replyInfo = command.baseInfo.get.copy(isPrivate = Some(false))

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).reply.orgInfo shouldBe Some(replyInfo)
      }
      "there is an invalid CA postal code" in {
        val invalidCaPostalCode = Some(PostalCode(PostalCode.Region.Ca(CAPostalCode("12345"))))
        val command = EstablishOrganizationCommand(Some(info.copy(address = Some(info.address.get.copy(postalCode = invalidCaPostalCode)))))

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).isError shouldBe true
      }
      "there is an invalid US postal code with a letter" in {
        val invalidUsPostalCode = Some(PostalCode(PostalCode.Region.Us(USPostalCode("A1B2C3"))))
        val command = EstablishOrganizationCommand(Some(info.copy(address = Some(info.address.get.copy(postalCode = invalidUsPostalCode)))))

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).isError shouldBe true
      }
      "there is no OrganizationInfo" in {
        val command = EstablishOrganizationCommand(None)

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).isError shouldBe true
      }
      "there is no PostalCode" in {
        val command = EstablishOrganizationCommand(Some(info.copy(address =
          Some(caAddress.copy(postalCode = None))))
        )

        val service = OrganizationsApiActionTestKit(new OrganizationsApiAction(_))
        service.establishOrganization(command).isError shouldBe true
      }
    }
  }
}
