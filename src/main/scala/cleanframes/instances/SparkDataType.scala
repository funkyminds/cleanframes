package cleanframes.instances

import org.apache.spark.sql.types.DataType

trait SparkDataType[T] {
  def getDataType: DataType
}