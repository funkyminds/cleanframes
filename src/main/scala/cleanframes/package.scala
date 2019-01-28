package object cleanframes {

  /**
    * Top-level alias to access columns.
    * If not specified, DataFrame schema starts as : struct_type(col1, col2, col3, ..., colN).
    * Column cannot be accessed via `struct_type` alias.
    */
  private[cleanframes] val reserved_root_level_alias = "cleanframes_root_alias"
}
