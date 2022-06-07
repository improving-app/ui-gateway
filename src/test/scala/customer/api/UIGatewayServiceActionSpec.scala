package customer.api

import akka.http.scaladsl.model.StatusCodes
import kalix.scalasdk.action.Action
import kalix.scalasdk.testkit.ActionResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class UIGatewayServiceActionSpec
    extends AnyWordSpec
    with Matchers {

  "UIGatewayServiceAction" must {

    "handle command getRoles" in {
      val service = UIGatewayServiceActionTestKit(new UIGatewayServiceAction(_))
      val result = service.getRoles(GetRoles("foo@example.com"))
      result.isReply shouldBe true
      result.reply shouldBe UserRoles(Seq("User"))
    }
  }
}
