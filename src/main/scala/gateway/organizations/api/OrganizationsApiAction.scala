package gateway.organizations.api

import gateway.organizations.domain.{OrgType, OrganizationEstablished}
import kalix.scalasdk.action.{Action, ActionCreationContext}

import java.util.UUID

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsApiAction(creationContext: ActionCreationContext) extends AbstractOrganizationsApiAction {
  override def establishOrganization(establishOrganization: EstablishOrganizationDto): Action.Effect[OrganizationEstablishedDto] = {
    if(establishOrganization.baseInfo.isEmpty) effects.error("You need to provide organization info to establish an organization")
    else {
      val domainResult = establishOrganization.parentOrg match {
        case Some(_) => OrganizationEstablished(UUID.randomUUID().toString, OrgType.ORG_TYPE_SUB)
        case None => OrganizationEstablished(UUID.randomUUID().toString, OrgType.ORG_TYPE_BASE)
      }

      effects.reply(OrganizationEstablishedDto.defaultInstance.copy(orgId = domainResult.orgId, `type` = domainResult.`type`.name))
    }
  }
}

