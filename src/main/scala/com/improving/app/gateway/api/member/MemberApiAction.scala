package com.improving.app.gateway.api.member

import kalix.scalasdk.action.Action
import kalix.scalasdk.action.ActionCreationContext

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class MemberApiAction(creationContext: ActionCreationContext) extends AbstractMemberApiAction {

  override def registerMember(memberInfo: MemberInfo): Action.Effect[MemberData] = {
    throw new RuntimeException("The command handler for `registerMember` is not implemented, yet")
  }
}

