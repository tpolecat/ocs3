// Copyright (c) 2016-2017 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.json.instances

import cats.tests.CatsSuite
import gem.EphemerisKey
import gem.math.Ephemeris
import gem.arb._
import io.circe.testing.CodecTests
import io.circe.testing.instances._

final class EphemerisJsonSpec extends CatsSuite {
  import ArbEphemeris._
  import ArbEphemerisKey._
  import ephemeris._

  checkAll("Ephemeris",    CodecTests[Ephemeris].unserializableCodec)
  checkAll("EphemerisKey", CodecTests[EphemerisKey].unserializableCodec)

}
