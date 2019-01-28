package cleanframes

import org.apache.spark.sql.DataFrame

package object syntax {

  implicit class CleanerOps(frame: DataFrame) {
    def clean[A](implicit env: Cleaner[A]): DataFrame = {
      frame
        .select(env.clean(frame, None, Some(reserved_root_level_alias)): _*)
        .selectExpr(s"$reserved_root_level_alias.*")
    }
  }

}