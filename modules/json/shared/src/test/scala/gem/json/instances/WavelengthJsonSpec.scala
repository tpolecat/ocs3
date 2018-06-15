// Copyright (c) 2016-2018 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.json.instances

import cats.tests.CatsSuite
import gem.math.Wavelength
import gem.arb._
import io.circe.testing.CodecTests
import io.circe.testing.instances._

final class WavelengthJsonSpec extends CatsSuite {
  import ArbWavelength._
  import wavelength._

  checkAll("Wavelength", CodecTests[Wavelength].unserializableCodec)

}
