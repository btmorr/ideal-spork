package com.github.btmorr.harmonia.models

/* The Vowpal Wabbit implementation of SEARN search requires input in a particular format. This can be submitted
 * to a stationary vw model (executing vw in a subprocess with a pretrained model and the test flag, to keep the
 * model stationary, equivalent to `echo '| <tag> <word>' | vw -t -i policy.vw -P 1`, or to an active
 */
case object SearnPredictor extends Model[(Word, PosTag), Boolean] {
  def apply(wt: (Word, PosTag)) = {
    val req = s"| ${wt._2} ${wt._1}"
    println(s"Assembled request for VW: $req")
    // submit req to vw server, get prediction
    false
  }
}
