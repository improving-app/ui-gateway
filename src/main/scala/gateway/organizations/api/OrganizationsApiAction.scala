package gateway.organizations.api

import kalix.scalasdk.action.{Action, ActionCreationContext}

import java.util.UUID
import scala.util.matching.Regex

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsApiAction(creationContext: ActionCreationContext) extends AbstractOrganizationsApiAction {
  override def establishOrganization(establishOrganization: EstablishOrganizationCommand): Action.Effect[OrganizationEstablishedEvent] = {
    def checkParentAndReply: Action.Effect[OrganizationEstablishedEvent] ={
      val result = establishOrganization.parentOrg match {
        case Some(_) => OrganizationEstablishedEvent(UUID.randomUUID().toString, OrgType.ORG_TYPE_SUB)
        case None => OrganizationEstablishedEvent(UUID.randomUUID().toString, OrgType.ORG_TYPE_BASE)
      }

      effects.reply(result)
    }

    establishOrganization.baseInfo match {
      case None => effects.error("No organization info provided - cannot establish organization")
      case Some(OrganizationInfo(_, _, None, _, _, _)) =>
        effects.error("No address provided in organization info - cannot establish organization")
      case Some(OrganizationInfo(_, _, Some(Address(_, _, _, _, _, None, _)), _, _, _)) =>
        effects.error("No postal code provided in organization info - cannot establish organization")
      case Some(OrganizationInfo(_, _, Some(Address(_, _, _, _, _, Some(PostalCode(region, _)), _)), _, _, _)) =>
        if(region.isUs) region.us match {
            case None => effects.error("No postal code provided in organization info - cannot establish organization")
            case Some(USPostalCode(uspc, _)) =>
              val regex = new Regex("\\d{5}(-\\d{4})?")
              if(regex.matches(uspc)) checkParentAndReply
              else effects.error("US Postal Code provided has invalid format - cannot establish organization")
          }
        else if(region.isCa) region.ca match {
            case None => effects.error("No postal code provided in organization info - cannot establish organization")
            case Some(CAPostalCode(capc, _)) =>
              val regex = new Regex("[A-Z]\\d[A-Z]\\d[A-Z]\\d")
              if(regex.matches(capc)) checkParentAndReply
              else effects.error("CA Postal Code provided has invalid format - cannot establish organization")
          }
        else effects.error("Postal Code region not valid - cannot establish organization")
      case Some(_) => checkParentAndReply
    }
  }
}

