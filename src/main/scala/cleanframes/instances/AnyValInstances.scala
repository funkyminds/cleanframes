package cleanframes.instances

import java.{lang => j}

trait AnyValInstances
  extends IntInstances
    with ByteInstances
    with CharInstances
    with ShortInstances
    with LongInstances
    with FloatInstances
    with DoubleInstances
    with BooleanInstances

trait IntInstances {
  implicit lazy val stdStringToInt: String => Int = Integer.parseInt
}

trait ByteInstances {
  implicit val stdStringToByte: String => Byte = j.Byte.parseByte
}

trait CharInstances {
  implicit val stdStringToChar: String => Char = _.charAt(0)
}

trait ShortInstances {
  implicit val stdStringToShort: String => Short = j.Short.parseShort
}

trait LongInstances {
  implicit val stdStringToLong: String => Long = j.Long.parseLong
}

trait FloatInstances {
  implicit val stdStringToFloat: String => Float = j.Float.parseFloat
}

trait DoubleInstances {
  implicit val stdStringToDouble: String => Double = j.Double.parseDouble
}

trait BooleanInstances {
  implicit val stdStringToBoolean: String => Boolean = j.Boolean.parseBoolean
}