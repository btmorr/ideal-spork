package com.github.btmorr.harmonia

/* Models defined in this package should extend the Model trait, and define their input and output formats
 * either in terms of simple types, or define their inputs here with type definitions (a FeatureVector type
 * or case class is probably forthcoming...
 *
 * Follow the pattern demonstrated with SimpleLookupModel--any object extending Model must define an `apply`
 * method, making the object a callable that will take a relatively raw input, perform whatever computations
 * are necessary to generate a final response (including going out to other servers or databases), and then
 * return that response. For any external interactions, queries and responses should be recorded immutably
 * so that there's no question what data the model was operating upon (for instance, if a model does a map
 * search for a location, the query string used to make the request and the response from the map service
 * should be cached, so that if the result of the identical query changes in the future, the initial response
 * could still be reproduced by injeting the prior reponse).
 */
object Models {
  sealed trait Model[Q, R] extends (Q => R)

  case object SimpleLookupModel extends Model[String, String] {
    def apply(in: String) = in.toLowerCase match {
      case i if in contains "weather" => "I'm working on learning how to look up the weather"
      case i if in contains "how are you" => "I'm doing well. How's about you?"
      case _ => "I'm not sure what you're trying to say"
    }
  }
}
