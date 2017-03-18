package com.github.btmorr.harmonia.models

/* Pass-thru, just for the lolz. Says the string out loud, if you're on a mac, and then just returns it */
case object SayIt extends Model[String, String] {
  def apply(in: String) = {
    import scala.sys.process._
    s"say $in".!
    in
  }
}
