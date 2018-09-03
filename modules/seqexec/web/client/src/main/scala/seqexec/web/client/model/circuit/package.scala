// Copyright (c) 2016-2018 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package seqexec.web.client

import cats.{Eq, Order}
import cats.implicits._
import cats.data.NonEmptyList
import diode._
import gem.Observation
import gem.enum.Site
import monocle.Lens
import monocle.macros.Lenses
import seqexec.model._
import seqexec.model.enum._
import seqexec.web.client.model._
import seqexec.web.client.components.sequence.steps.StepConfigTable
import seqexec.web.client.components.QueueTableBody
import web.client.table._

package object circuit {
  implicit def CircuitToOps[T <: AnyRef](c: Circuit[T]): CircuitOps[T] = new CircuitOps(c)

  implicit def fastEq[A: Eq]: FastEq[A] = new FastEq[A] {
    override def eqv(a: A, b: A): Boolean = a === b
  }

  implicit def fastNelEq[A: Eq]: FastEq[NonEmptyList[A]] = new FastEq[NonEmptyList[A]] {
    override def eqv(a: NonEmptyList[A], b: NonEmptyList[A]): Boolean = a === b
  }
}

package circuit {
  /**
   * This lets us use monocle lenses to create diode ModelRW instances
   */
  class CircuitOps[M <: AnyRef](circuit: Circuit[M]) {
    def zoomRWL[A: Eq](lens: Lens[M, A]): ModelRW[M, A] = circuit.zoomRW(lens.get)((m, a) => lens.set(a)(m))(fastEq[A])
  }

  // All these classes are focused views of the root model. They are used to only update small sections of the
  // UI even if other parts of the root model change
  final case class WebSocketsFocus(location: Pages.SeqexecPages, sequences: SequencesQueue[SequenceView], user: Option[UserDetails], defaultObserver: Observer, clientId: Option[ClientID], site: Option[Site]) extends UseValueEq

  @Lenses
  final case class SequencesFocus(sequences: SequencesQueue[SequenceView], sod: SequencesOnDisplay)

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  object SequencesFocus {
    implicit val eq: Eq[SequencesFocus] =
      Eq.by(x => (x.sequences, x.sod))

    val sequencesFocusL: Lens[SeqexecUIModel, SequencesFocus] =
      Lens[SeqexecUIModel, SequencesFocus](m => SequencesFocus(m.sequences, m.sequencesOnDisplay))(v => m => m.copy(sequences = v.sequences, sequencesOnDisplay = v.sod))
  }

  @Lenses
  final case class SODLocationFocus(location: Pages.SeqexecPages, sod: SequencesOnDisplay, clientId: Option[ClientID])

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  object SODLocationFocus {
    implicit val eq: Eq[SODLocationFocus] =
      Eq.by(x => (x.location, x.sod, x.clientId))

    val sodLocationFocusL: Lens[SeqexecAppRootModel, SODLocationFocus] =
      Lens[SeqexecAppRootModel, SODLocationFocus](m => SODLocationFocus(m.uiModel.navLocation, m.uiModel.sequencesOnDisplay, m.clientId))(v => m => m.copy(clientId = v.clientId, uiModel = m.uiModel.copy(navLocation = v.location, sequencesOnDisplay = v.sod)))
  }

  @Lenses
  final case class InitialSyncFocus(location: Pages.SeqexecPages, sod: SequencesOnDisplay, firstLoad: Boolean)

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  object InitialSyncFocus {
    implicit val eq: Eq[InitialSyncFocus] =
      Eq.by(x => (x.location, x.sod, x.firstLoad))

    val initialSyncFocusL: Lens[SeqexecUIModel, InitialSyncFocus] =
      Lens[SeqexecUIModel, InitialSyncFocus](m => InitialSyncFocus(m.navLocation, m.sequencesOnDisplay, m.firstLoad))(v => m => m.copy(navLocation = v.location, sequencesOnDisplay = v.sod, firstLoad = v.firstLoad))
  }


  final case class SequenceInQueue(id: Observation.Id, status: SequenceState, instrument: Instrument, active: Boolean, loaded: Boolean, name: String, targetName: Option[TargetName], runningStep: Option[RunningStep]) extends UseValueEq

  object SequenceInQueue {
    implicit val order: Order[SequenceInQueue] = Order.by(_.id)
    implicit val ordering: scala.math.Ordering[SequenceInQueue] = order.toOrdering
  }

  final case class StatusAndLoadedSequencesFocus(status: ClientStatus, sequences: List[SequenceInQueue], tableState: TableState[QueueTableBody.TableColumn]) extends UseValueEq

  final case class SequenceObserverFocus(instrument: Instrument, obsId: Observation.Id, completed: Boolean, observer: Option[Observer]) extends UseValueEq

  final case class HeaderSideBarFocus(status: ClientStatus, conditions: Conditions, operator: Option[Operator], observer: Either[Observer, SequenceObserverFocus]) extends UseValueEq

  final case class InstrumentStatusFocus(instrument: Instrument, active: Boolean, idState: Option[(Observation.Id, SequenceState)], runningStep: Option[RunningStep]) extends UseValueEq

  final case class InstrumentTabFocus(tabs: NonEmptyList[AvailableTab], defaultObserver: Observer) extends UseValueEq

  object InstrumentTabFocus {
    implicit val eq: Eq[InstrumentTabFocus] =
      Eq.by(x => (x.tabs, x.defaultObserver))
  }
  final case class SequenceTabContentFocus(instrument: Option[Instrument], id: Option[Observation.Id], sequenceSelected: Boolean, logDisplayed: SectionVisibilityState) extends UseValueEq

  object SequenceTabContentFocus {
    implicit val eq: Eq[SequenceTabContentFocus] =
      Eq.by(x => (x.instrument, x.id, x.sequenceSelected, x.logDisplayed))
  }

  final case class SequenceInfoFocus(isLogged: Boolean, obsName: Option[String], observer: Option[Observer], status: Option[SequenceState], targetName: Option[TargetName])

  object SequenceInfoFocus{
    implicit val eq: Eq[SequenceInfoFocus] =
      Eq.by(x => (x.isLogged, x.obsName, x.observer, x.status, x.targetName))
  }

  final case class StatusAndStepFocus(isLogged: Boolean, instrument: Instrument, obsId: Observation.Id, stepConfigDisplayed: Option[Int], totalSteps: Int, isPreview: Boolean)

  object StatusAndStepFocus {
    implicit val eq: Eq[StatusAndStepFocus] =
      Eq.by(x => (x.isLogged, x.instrument, x.obsId, x.stepConfigDisplayed, x.totalSteps, x.isPreview))
  }

  final case class StepsTableFocus(id: Observation.Id, instrument: Instrument, state: SequenceState, steps: List[Step], stepConfigDisplayed: Option[Int], nextStepToRun: Option[Int], isPreview: Boolean)

  object StepsTableFocus {
    implicit val eq: Eq[StepsTableFocus] =
      Eq.by(x => (x.id, x.instrument, x.state, x.steps, x.stepConfigDisplayed, x.nextStepToRun, x.isPreview))
  }

  final case class StepsTableAndStatusFocus(status: ClientStatus, stepsTable: Option[StepsTableFocus], configTableState: TableState[StepConfigTable.TableColumn])

  object StepsTableAndStatusFocus {
    implicit val eq: Eq[StepsTableAndStatusFocus] =
      Eq.by(x => (x.status, x.stepsTable, x.configTableState))
  }

  final case class ControlModel(id: Observation.Id, isPartiallyExecuted: Boolean, nextStepToRun: Option[Int], status: SequenceState) extends UseValueEq

  final case class SequenceControlFocus(isLogged: Boolean, isConnected: Boolean, control: Option[ControlModel], syncInProgress: Boolean) extends UseValueEq

  final case class TableStates(queueTable: TableState[QueueTableBody.TableColumn], stepConfigTable: TableState[StepConfigTable.TableColumn]) extends UseValueEq
}
