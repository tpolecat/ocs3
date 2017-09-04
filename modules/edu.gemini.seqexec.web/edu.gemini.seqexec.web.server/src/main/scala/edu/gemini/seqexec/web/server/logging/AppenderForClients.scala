// Copyright (c) 2016-2017 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package edu.gemini.seqexec.web.server.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.Level
import ch.qos.logback.core.AppenderBase
import edu.gemini.seqexec.model.Model.{ServerLogLevel, SeqexecEvent}
import edu.gemini.seqexec.model.Model.SeqexecEvent.ServerLogMessage
import scalaz.stream.async.mutable.Topic

import scalaz._
import Scalaz._
import scalaz.concurrent.Task

import java.time.Instant

/**
 * Custom appender that can take log events from logback and send them
 * to clients via the common pipe/WebSockets
 *
 * This is out of the scala/http4s loop
 */
class AppenderForClients(out: Topic[SeqexecEvent]) extends AppenderBase[ILoggingEvent] {
  override def append(event: ILoggingEvent): Unit = {
    // Convert to a seqexec model to send to clients
    val level = event.getLevel match {
      case Level.INFO  => ServerLogLevel.INFO.some
      case Level.WARN  => ServerLogLevel.WARN.some
      case Level.ERROR => ServerLogLevel.ERROR.some
      case _           => none
    }
    val timestamp = Instant.ofEpochMilli(event.getTimeStamp)

    // Send a message to the clients if level is INFO or higher
    // We are outside the normal execution loop, thus we need to call unsafePerformSync directly
    level.fold(Task.now(()))(l => out.publishOne(ServerLogMessage(l, timestamp, event.getMessage))).unsafePerformSync
  }
}