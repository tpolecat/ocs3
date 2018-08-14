// Copyright (c) 2016-2018 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package seqexec.server.keywords

import gem.Observation
import seqexec.model.dhs.ImageFileId
import seqexec.server.SeqAction

/**
  * Header implementations know what headers sent before and after an observation
  */
trait Header {
  def sendBefore(obsId: Observation.Id, id: ImageFileId): SeqAction[Unit]
  def sendAfter(id: ImageFileId): SeqAction[Unit]
}