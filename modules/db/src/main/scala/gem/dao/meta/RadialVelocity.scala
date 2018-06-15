// Copyright (c) 2016-2018 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.dao.meta

import doobie._
import gem.math._

trait RadialVelocityMeta {
  import PrismMeta._

  /** Radial velocity in meters per second. */
  implicit lazy val RadialVelocityMeta: Meta[RadialVelocity] =
    RadialVelocity.fromMetersPerSecond.asMeta

}
object RadialVelocityMeta extends RadialVelocityMeta
