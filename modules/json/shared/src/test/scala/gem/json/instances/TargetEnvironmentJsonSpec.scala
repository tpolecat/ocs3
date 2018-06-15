// Copyright (c) 2016-2017 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.json.instances

import cats.tests.CatsSuite
import gem.TargetEnvironment
import gem.arb._
import io.circe.testing.CodecTests
import io.circe.testing.instances._

final class TargetEnvironmentJsonSpec extends CatsSuite {
  import ArbTargetEnvironment._
  import targetenvironment._

  checkAll("TargetEnvironment", CodecTests[TargetEnvironment].unserializableCodec)

}
