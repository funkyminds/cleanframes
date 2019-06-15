package cleanframes.instances

import scala.util.Try

trait TryToOption {
  implicit def tryToOption[A, B](implicit func: A => B): A => Option[B] = {
    input => Try(func(input)).toOption
  }
}
