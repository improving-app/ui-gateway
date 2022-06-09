package gateway.organizations.api

import gateway.organizations.domain.{OrgType, OrganizationEstablished}
import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

import java.util.UUID

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationsApiAction(creationContext: ActionCreationContext) extends AbstractOrganizationsApiAction {

  override def establishOrganization(establishOrganization: EstablishOrganization): Action.Effect[OrganizationEstablished] = {
    if(establishOrganization.baseInfo.isEmpty) effects.error("You need to provide organization info to establish an organization")
    else {
      establishOrganization.parentOrg match {
        case Some(_) => effects.reply(OrganizationEstablished(UUID.randomUUID().toString, OrgType.ORG_TYPE_SUB))
        case None => effects.reply(OrganizationEstablished(UUID.randomUUID().toString, OrgType.ORG_TYPE_BASE))
      }
    }
  }
}

