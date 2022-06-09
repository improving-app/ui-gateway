package gateway.customer.api

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UIGatewayServiceAction(creationContext: ActionCreationContext) extends AbstractUIGatewayServiceAction {

  override def getRoles(getRoles: GetRoles): Action.Effect[UserRoles] = {
    throw new RuntimeException("The command handler for `getRoles` is not implemented, yet")
  }
}

