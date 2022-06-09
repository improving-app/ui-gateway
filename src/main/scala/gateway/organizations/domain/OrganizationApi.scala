package gateway.organizations.domain

import gateway.organizations.api
import kalix.scalasdk.eventsourcedentity.EventSourcedEntity
import kalix.scalasdk.eventsourcedentity.EventSourcedEntityContext

import java.util.UUID

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class OrganizationApi(context: EventSourcedEntityContext) extends AbstractOrganizationApi {
  override def emptyState: Organization = Organization.defaultInstance

  override def establishOrganization(currentState: Organization, establishOrganization: api.EstablishOrganization): EventSourcedEntity.Effect[OrganizationEstablished] = {
    val newOrgId = UUID.randomUUID().toString
    val event = OrganizationEstablished(orgId = newOrgId)
    establishOrganization.parentOrg match {
      case None => currentState.withBase(BaseOrganization(newOrgId))
      case Some(parentId) => currentState.withSub(SubOrganization(newOrgId, parentOrg = parentId))
    }
    effects.emitEvent(event).thenReply(_ => event)
  }

  override def getOrganization(currentState: Organization, getOrganization: api.GetOrganization): EventSourcedEntity.Effect[Organization] =
    effects.error("The command handler for `getOrganization` is not implemented, yet")

  override def updateOrganizationInfo(currentState: Organization, updateOrganizationInfo: api.UpdateOrganizationInfo): EventSourcedEntity.Effect[OrganizationInfoUpdated] =
    effects.error("The command handler for `updateOrganizationInfo` is not implemented, yet")

  override def updateBaseOrganizationUrl(currentState: Organization, updateBaseOrganizationUrl: api.UpdateBaseOrganizationUrl): EventSourcedEntity.Effect[BaseOrganizationUrlUpdated] =
    effects.error("The command handler for `updateBaseOrganizationUrl` is not implemented, yet")

  override def assignNewParentOrg(currentState: Organization, assignNewParentOrg: api.AssignNewParentOrg): EventSourcedEntity.Effect[NewParentOrgAssigned] =
    effects.error("The command handler for `assignNewParentOrg` is not implemented, yet")

  override def changeOrganizationState(currentState: Organization, changeOrganizationState: api.ChangeOrganizationState): EventSourcedEntity.Effect[OrganizationStateChanged] =
    effects.error("The command handler for `changeOrganizationState` is not implemented, yet")

  override def organizationEstablished(currentState: Organization, organizationEstablished: OrganizationEstablished): Organization = {
    if(currentState.org.isEmpty) throw new RuntimeException("Organization was not successfully established")
    else currentState
  }

  override def organization(currentState: Organization, organization: Organization): Organization =
    throw new RuntimeException("The event handler for `Organization` is not implemented, yet")

  override def organizationInfoUpdated(currentState: Organization, organizationInfoUpdated: OrganizationInfoUpdated): Organization =
    throw new RuntimeException("The event handler for `OrganizationInfoUpdated` is not implemented, yet")

  override def baseOrganizationUrlUpdated(currentState: Organization, baseOrganizationUrlUpdated: BaseOrganizationUrlUpdated): Organization =
    throw new RuntimeException("The event handler for `BaseOrganizationUrlUpdated` is not implemented, yet")

  override def newParentOrgAssigned(currentState: Organization, newParentOrgAssigned: NewParentOrgAssigned): Organization =
    throw new RuntimeException("The event handler for `NewParentOrgAssigned` is not implemented, yet")

  override def organizationStateChanged(currentState: Organization, organizationStateChanged: OrganizationStateChanged): Organization =
    throw new RuntimeException("The event handler for `OrganizationStateChanged` is not implemented, yet")

}
