package cleanframes

package object instances {

  // @formatter:off
  object all          extends AllInstances
  object anyVals      extends AnyValInstances
  object boolean      extends BooleanInstances
  object byte         extends ByteInstances     with NumericAnyValInstance
  object char         extends CharInstances
  object double       extends DoubleInstances   with NumericAnyValInstance
  object float        extends FloatInstances    with NumericAnyValInstance
  object int          extends IntInstances      with NumericAnyValInstance
  object long         extends LongInstances     with NumericAnyValInstance
  object short        extends ShortInstances    with NumericAnyValInstance
  object string       extends StringInstances
  object tryToOption  extends TryToOption
  object higher       extends HigherOrderKind
  // @formatter:on
}
