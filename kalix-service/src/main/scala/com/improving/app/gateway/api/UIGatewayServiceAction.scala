package com.improving.app.gateway.api

import kalix.scalasdk.action.{Action, ActionCreationContext}

import java.util.UUID
import scala.annotation.unused
import scala.util.matching.Regex
// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UIGatewayServiceAction(@unused creationContext: ActionCreationContext)
    extends AbstractUIGatewayServiceAction {

  override def establishOrganization(
      establishOrganizationCommand: EstablishOrganizationCommand
  ): Action.Effect[OrganizationEstablishedReply] = {
    def checkIsPrivateAndReply(
        orgInfo: OrganizationInfo,
        isPrivate: Option[Boolean]
    ): Action.Effect[OrganizationEstablishedReply] = {
      val info = orgInfo.copy(isPrivate = Some(isPrivate.getOrElse(false)))

      effects.reply(
        OrganizationEstablishedReply(UUID.randomUUID().toString, Some(info))
      )
    }

    establishOrganizationCommand.baseInfo match {
      case None =>
        effects.error(
          "No organization info provided - cannot establish organization"
        )
      case Some(organizationInfo: OrganizationInfo)
          if organizationInfo.address.isEmpty =>
        effects.error(
          "No address provided in organization info - cannot establish organization"
        )
      case Some(OrganizationInfo(_, _, Some(address), _, _, _, _, _))
          if address.postalCode.isEmpty =>
        effects.error(
          "No postal code provided in organization info - cannot establish organization"
        )
      case Some(
            info @ OrganizationInfo(
              _,
              _,
              Some(Address(_, _, _, _, _, Some(PostalCode(region, _)), _)),
              _,
              _,
              isPrivate,
              _,
              _
            )
          ) =>
        if (region.isUs) region.us match {
          case None =>
            effects.error(
              "No postal code provided in organization info - cannot establish organization"
            )
          case Some(USPostalCode(uspc, _)) =>
            val regex = new Regex("\\d{5}(-\\d{4})?")
            if (regex.matches(uspc)) checkIsPrivateAndReply(info, isPrivate)
            else
              effects.error(
                "US Postal Code provided has invalid format - cannot establish organization"
              )
        }
        else if (region.isCa) region.ca match {
          case None =>
            effects.error(
              "No postal code provided in organization info - cannot establish organization"
            )
          case Some(CAPostalCode(capc, _)) =>
            val regex = new Regex("[A-Z]\\d[A-Z]\\d[A-Z]\\d")
            if (regex.matches(capc)) checkIsPrivateAndReply(info, isPrivate)
            else
              effects.error(
                "CA Postal Code provided has invalid format - cannot establish organization"
              )
        }
        else
          effects.error(
            "Postal Code region not valid - cannot establish organization"
          )
      case Some(orgInfo) => checkIsPrivateAndReply(orgInfo, orgInfo.isPrivate)
    }
  }
}
