package cleanframes.instances

trait StringInstances {
  implicit lazy val stdStringToString: String => String = (a: String) => a
}
