// Copyright (c) 2016-2017 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package edu.gemini.seqexec.server.gnirs

import java.lang.{Double => JDouble}

import edu.gemini.epics.acm._
import edu.gemini.seqexec.server.EpicsCommand.setParameter
import edu.gemini.seqexec.server.{EpicsCommand, EpicsSystem, ObserveCommand, SeqAction}
import org.log4s.{Logger, getLogger}

class GnirsEpics(epicsService: CaService, tops: Map[String, String]) {

  val GNIRS_TOP: String = tops.getOrElse("nirs", "nirs:")

  object configCCCmd extends EpicsCommand {
    override protected val cs: Option[CaCommandSender] = Option(epicsService.getCommandSender("nirs::config"))

    val cover: Option[CaParameter[String]] = cs.map(_.getString("cover"))
    def setCover(v: String): SeqAction[Unit] = setParameter(cover, v)

    val filter1: Option[CaParameter[String]] = cs.map(_.getString("filter1"))
    def setFilter1(v: String): SeqAction[Unit] = setParameter(filter1, v)

    val filter2: Option[CaParameter[String]] = cs.map(_.getString("filter2"))
    def setFilter2(v: String): SeqAction[Unit] = setParameter(filter2, v)

    val focus: Option[CaParameter[String]] = cs.map(_.getString("focus"))
    def setFocus(v: String): SeqAction[Unit] = setParameter(focus, v)

    val tilt: Option[CaParameter[String]] = cs.map(_.getString("tilt"))
    def setTilt(v: String): SeqAction[Unit] = setParameter(tilt, v)

    val prism: Option[CaParameter[String]] = cs.map(_.getString("prism"))
    def setPrism(v: String): SeqAction[Unit] = setParameter(prism, v)

    val acqMirror: Option[CaParameter[String]] = cs.map(_.getString("acqMirror"))
    def setAcqMirror(v: String): SeqAction[Unit] = setParameter(acqMirror, v)

    val focusbest: Option[CaParameter[String]] = cs.map(_.getString("focusbest"))
    def setFocusBest(v: String): SeqAction[Unit] = setParameter(focusbest, v)

    val centralWavelength: Option[CaParameter[JDouble]] = cs.map(_.getDouble("centralWavelength"))
    def setCentralWavelength(v: Double): SeqAction[Unit] = setParameter(centralWavelength, JDouble.valueOf(v))

    val camera: Option[CaParameter[String]] = cs.map(_.getString("camera"))
    def setCamera(v: String): SeqAction[Unit] = setParameter(camera, v)

    val gratingMode: Option[CaParameter[String]] = cs.map(_.getString("gratingMode"))
    def setGratingMode(v: String): SeqAction[Unit] = setParameter(gratingMode, v)

    val gratingOrder: Option[CaParameter[Integer]] = cs.map(_.getInteger("order"))
    def setOrder(v: Int): SeqAction[Unit] = setParameter(gratingOrder, Integer.valueOf(v))

    val grating: Option[CaParameter[String]] = cs.map(_.getString("grating"))
    def setGrating(v: String): SeqAction[Unit] = setParameter(grating, v)

    val slitWidth: Option[CaParameter[String]] = cs.map(_.getString("slitWidth"))
    def setSlitWidth(v: String): SeqAction[Unit] = setParameter(slitWidth, v)

    val decker: Option[CaParameter[String]] = cs.map(_.getString("decker"))
    def setDecker(v: String): SeqAction[Unit] = setParameter(decker, v)

  }

  object configDCCmd extends EpicsCommand {
    override protected val cs: Option[CaCommandSender] = Option(epicsService.getCommandSender("nirs::dcconfig"))

    val lowNoise: Option[CaParameter[Integer]] = cs.map(_.getInteger("lowNoise"))
    def setLowNoise(v: Int): SeqAction[Unit] = setParameter(lowNoise, Integer.valueOf(v))

    val exposureTIme: Option[CaParameter[JDouble]] = cs.map(_.getDouble("exposureTIme"))
    def setExposureTIme(v: Double): SeqAction[Unit] = setParameter(exposureTIme, JDouble.valueOf(v))

    val wcs: Option[CaParameter[String]] = cs.map(_.getString("wcs"))
    def setWcs(v: String): SeqAction[Unit] = setParameter(wcs, v)

    val digitalAvgs: Option[CaParameter[Integer]] = cs.map(_.getInteger("digitalAvgs"))
    def setDigitalAvgs(v: Int): SeqAction[Unit] = setParameter(digitalAvgs, Integer.valueOf(v))

    val detBias: Option[CaParameter[JDouble]] = cs.map(_.getDouble("detBias"))
    def setDetBias(v: Double): SeqAction[Unit] = setParameter(detBias, JDouble.valueOf(v))

    val coadds: Option[CaParameter[Integer]] = cs.map(_.getInteger("coadds"))
    def setCoadds(v: Int): SeqAction[Unit] = setParameter(coadds, Integer.valueOf(v))

  }

  object endObserveCmd extends EpicsCommand {
    override protected val cs:Option[CaCommandSender] = Option(epicsService.getCommandSender("nirs::endObserve"))
  }

  private val stopCS: Option[CaCommandSender] = Option(epicsService.getCommandSender("nirs::stop"))
  private val observeAS: Option[CaApplySender] = Option(epicsService.createObserveSender("nirs::observeCmd",
      GNIRS_TOP + "apply", GNIRS_TOP + "applyC", GNIRS_TOP + "dc:observeC", GNIRS_TOP + "stop", GNIRS_TOP + "abort", ""))

  object stopCmd extends EpicsCommand {
    override protected val cs: Option[CaCommandSender] = stopCS
  }

  object stopAndWaitCmd extends ObserveCommand {
    override protected val cs: Option[CaCommandSender] = stopCS
    override protected val os: Option[CaApplySender] = observeAS
  }

  private val abortCS: Option[CaCommandSender] = Option(epicsService.getCommandSender("nirs::abort"))

  object abortCmd extends EpicsCommand {
    override protected val cs: Option[CaCommandSender] = abortCS
  }

  object abortAndWait extends ObserveCommand {
    override protected val cs: Option[CaCommandSender] = abortCS
    override protected val os: Option[CaApplySender] = observeAS
  }

  object observeCmd extends ObserveCommand {
    override protected val cs: Option[CaCommandSender] = Option(epicsService.getCommandSender("nirs::observe"))
    override protected val os: Option[CaApplySender] = observeAS

    val label: Option[CaParameter[String]] = cs.map(_.getString("label"))
    def setLabel(v: String): SeqAction[Unit] = setParameter(label, v)
  }

  val state: CaStatusAcceptor = epicsService.getStatusAcceptor("nirs::status")
  val dcState: CaStatusAcceptor = epicsService.getStatusAcceptor("nirs::dcstatus")

  def arrayId: Option[String] = Option(dcState.getStringAttribute("arrayid").value)

  def arrayType: Option[String] = Option(dcState.getStringAttribute("arraytyp").value)

  def obsEpoch: Option[Double] = Option(dcState.getStringAttribute("OBSEPOCH").value).map(_.toDouble)

  def detBias: Option[Double] = Option(dcState.getStringAttribute("detBias").value).map(_.toDouble)

  def countDown: Option[String] = Option(dcState.getStringAttribute("countdown").value)

  def numCoadds: Option[Int] = Option(dcState.getIntegerAttribute("numCoAdds").value).map(_.toInt)

  def wcs: Option[String] = Option(dcState.getStringAttribute("wcs").value)

  def exposureTime: Option[Double] = Option(dcState.getStringAttribute("exposureTime").value).map(_.toDouble)

  def digitalAvgs: Option[Int] = Option(dcState.getIntegerAttribute("digitalAvgs").value).map(_.toInt)

  def lowNoise: Option[Int] = Option(dcState.getIntegerAttribute("lowNoise").value).map(_.toInt)

  def dhcConnected: Option[Int] = Option(dcState.getIntegerAttribute("dhcConnected").value).map(_.toInt)

  def minInt: Option[Double] = Option(dcState.getStringAttribute("minInt").value).map(_.toDouble)

  def dettemp: Option[Double] = Option(dcState.getStringAttribute("dettemp").value).map(_.toDouble)

  def prism: Option[String] = Option(state.getStringAttribute("prism").value)

  def focus: Option[String] = Option(state.getStringAttribute("focus").value)

  def slitWidth: Option[String] = Option(state.getStringAttribute("slitWidth").value)

  def acqMirror: Option[String] = Option(state.getStringAttribute("acqMirror").value)

  def cover: Option[String] = Option(state.getStringAttribute("cover").value)

  def grating: Option[String] = Option(state.getStringAttribute("grating").value)

  def filter1: Option[String] = Option(state.getStringAttribute("filter1").value)

  def filter2: Option[String] = Option(state.getStringAttribute("filter2").value)

  def camera: Option[String] = Option(state.getStringAttribute("camera").value)

  def decker: Option[String] = Option(state.getStringAttribute("decker").value)

  def centralWavelength: Option[Double] = Option(state.getStringAttribute("centralWavelength").value).map(_.toDouble)

  def gratingTilt: Option[Double] = Option(state.getStringAttribute("grattilt").value).map(_.toDouble)

  def nirscc: Option[String] = Option(state.getStringAttribute("nirscc").value)

  def gratingOrder: Option[Int] = Option(state.getIntegerAttribute("gratord").value).map(_.toInt)

}

object GnirsEpics extends EpicsSystem[GnirsEpics] {

  override val className: String = getClass.getName
  override val Log: Logger = getLogger
  override val CA_CONFIG_FILE: String = "/Gmos.xml"

  override def build(service: CaService, tops: Map[String, String]) = new GnirsEpics(service, tops)
}