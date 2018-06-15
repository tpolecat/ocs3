// Copyright (c) 2016-2017 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.json.instances

import cats.laws.discipline.arbitrary._
import cats.tests.CatsSuite
import io.circe.testing.CodecTests
import io.circe.testing.instances._
import scala.collection.immutable.SortedSet

final class SortedSetJsonSpec extends CatsSuite {
  import sortedset._

  checkAll("SortedSet", CodecTests[SortedSet[Int]].unserializableCodec)

}
