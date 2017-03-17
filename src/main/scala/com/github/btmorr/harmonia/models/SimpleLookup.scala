package com.github.btmorr.harmonia.models

case object SimpleLookup extends Model[String, String] {
  def apply(in: String) = in.toLowerCase match {
    case i if in contains "weather" => "I'm working on learning how to look up the weather"
    case i if in contains "how are you" => "I'm doing well. How's about you?"
    case i if in contains "ring" => "If you like it, you should've put a ring on it"
    case _ => "I'm not sure what you're trying to say"
  }
}
