package com.improving.app.gateway.api

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

import java.util.UUID
import scala.util.matching.Regex

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsApiAction(creationContext: ActionCreationContext) extends AbstractOrganizationsApiAction {

  override def establishOrganization(establishOrganizationCommand: EstablishOrganizationCommand): Action.Effect[OrganizationEstablishedReply] = {
    def checkParentAndReply(orgInfo: OrganizationInfo, parentOrg: Option[String], isPrivate: Option[Boolean]): Action.Effect[OrganizationEstablishedReply] ={
      val info = orgInfo.copy(isPrivate = Some(isPrivate.getOrElse(false)))

      val result = parentOrg match {
        case Some(_) => OrganizationEstablishedReply(UUID.randomUUID().toString, OrgType.ORG_TYPE_SUB, Some(info))
        case None => OrganizationEstablishedReply(UUID.randomUUID().toString, OrgType.ORG_TYPE_BASE, Some(info))
      }

      effects.reply(result)
    }

    establishOrganizationCommand.baseInfo match {
      case None => effects.error("No organization info provided - cannot establish organization")
      case Some(organizationInfo: OrganizationInfo) if organizationInfo.address.isEmpty =>
        effects.error("No address provided in organization info - cannot establish organization")
      case Some(OrganizationInfo(_, _, Some(address), _, _, _, _, _)) if address.postalCode.isEmpty =>
        effects.error("No postal code provided in organization info - cannot establish organization")
      case Some(info @ OrganizationInfo(_, _, Some(Address(_, _, _, _, _, Some(PostalCode(region, _)), _)), parentOrg, _, isPrivate, _, _)) =>
        if(region.isUs) region.us match {
          case None => effects.error("No postal code provided in organization info - cannot establish organization")
          case Some(USPostalCode(uspc, _)) =>
            val regex = new Regex("\\d{5}(-\\d{4})?")
            if(regex.matches(uspc)) checkParentAndReply(info, parentOrg, isPrivate)
            else effects.error("US Postal Code provided has invalid format - cannot establish organization")
        }
        else if(region.isCa) region.ca match {
          case None => effects.error("No postal code provided in organization info - cannot establish organization")
          case Some(CAPostalCode(capc, _)) =>
            val regex = new Regex("[A-Z]\\d[A-Z]\\d[A-Z]\\d")
            if(regex.matches(capc)) checkParentAndReply(info, parentOrg, isPrivate)
            else effects.error("CA Postal Code provided has invalid format - cannot establish organization")
        }
        else effects.error("Postal Code region not valid - cannot establish organization")
      case Some(orgInfo) => checkParentAndReply(orgInfo, orgInfo.parentOrg, orgInfo.isPrivate)
    }
  }
}

